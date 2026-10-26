package com.draco18s.harderores.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.capability.SiftableItemsHandler;
import com.draco18s.harderores.inventory.SifterContainerMenu;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.inventory.ModContainerBlockEntity;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class SifterBlockEntity extends ModContainerBlockEntity {
	private static final int SiftTime = 40;
	/** Ticks between pulls from the inventory above and pickups of items on top (a hopper's cooldown). */
	private static final int PULL_INTERVAL = 8;
	private int siftTime;
	private int activeSlot;
	protected final ItemStacksResourceHandler inputSlots = new SiftableItemsHandler(2);
	protected final ItemStacksResourceHandler outputSlots = new ItemStacksResourceHandler(1);
	protected final OutputItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);

	public final ResourceHandler<ItemResource> input = inputSlots;
	public final ResourceHandler<ItemResource> output = outputHandler;
	public final ResourceHandler<ItemResource> all = new CombinedResourceHandler<>(inputSlots,outputHandler);

	protected final ContainerData dataAccess = new ContainerData() {
		public int get(int p_58431_) {
			switch (p_58431_) {
			case 0:
				return SifterBlockEntity.this.siftTime;
			default:
				return 0;
			}
		}

		public void set(int p_58433_, int p_58434_) {
			switch (p_58433_) {
			case 0:
				SifterBlockEntity.this.siftTime = p_58434_;
				break;
			}

		}

		public int getCount() {
			return 1;
		}
	};

	public SifterBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(HarderOres.ModBlockEntities.machine_sifter, worldPosition, blockState);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.harderores.sifter");
	}

	@Override
	protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
		return new SifterContainerMenu(p_58627_, p_58628_, inputSlots, outputHandler, this, dataAccess);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, SifterBlockEntity sifter) {
		if(world.getBlockState(pos).getBlock() != sifter.getBlockState().getBlock()) return;
		if(!world.isClientSide() && world.getGameTime() % PULL_INTERVAL == 0) {
			sifter.pullFromAbove();
			sifter.suckItems();
		}
		if(sifter.siftTime > 0) {
			--sifter.siftTime;
			setChanged(world, pos, state);
			int slot = canSiftAny(sifter.inputSlots, sifter.outputSlots);
			boolean canAnySift = slot >= 0;
			if(!canAnySift) {
				sifter.siftTime = 0;
				sifter.activeSlot = -1;
				setChanged(world, pos, state);
				return;
			}
			if(slot != sifter.activeSlot && !sifter.level.isClientSide()) {
				sifter.activeSlot = slot;
				sifter.siftTime = SiftTime;
				setChanged(world, pos, state);
				return;
			}
			if (sifter.siftTime <= 0) {
				siftItem(sifter, sifter.activeSlot);
				setChanged(world, pos, state);
			}
		}
		else {
			sifter.activeSlot = -1;
			int slot = canSiftAny(sifter.inputSlots, sifter.outputSlots);
			if(slot >= 0) {
				sifter.siftTime = SiftTime;
				sifter.activeSlot = slot;
				setChanged(world, pos, state);
			}
		}
	}

	private static void siftItem(SifterBlockEntity sifter, int slot) {
		ItemStack stack = ItemUtil.getStack(sifter.inputSlots, slot);
		if(stack.isEmpty()) return;
		ItemStack result = HardLibAPI.oreMachines.getSiftResult(stack, true);
		if(result.isEmpty()) {
			if(stack.is(Tags.Items.DUSTS))
				result = stack.copyWithCount(1);
			else
				return;
		}
		try (Transaction tx = Transaction.openRoot()) {
			if(sifter.outputSlots.insert(0, ItemResource.of(result), result.getCount(), tx) < result.getCount()) return;
			sifter.inputSlots.extract(slot, ItemResource.of(stack), HardLibAPI.oreMachines.getSiftAmount(stack), tx);
			tx.commit();
		}
	}

	private static int canSiftAny(ItemStacksResourceHandler input, ItemStacksResourceHandler output) {
		for(int s = 0; s < input.size(); s++) {
			if(canSift(s, input, output)) {
				return s;
			}
		}
		return -1;
	}

	private static boolean canSift(int slot, ItemStacksResourceHandler input, ItemStacksResourceHandler output) {
		if(input.getResource(slot).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getSiftResult(ItemUtil.getStack(input, slot), true);
		if(result.isEmpty()) return false;
		if(!ItemUtil.insertItemReturnRemaining(output, 0, result, true, null).isEmpty()) return false;
		return true;
	}

	/** Takes siftable items out of the inventory above (such as a millstone's output), like a hopper. */
	private void pullFromAbove() {
		ResourceHandler<ItemResource> source = level.getCapability(Capabilities.Item.BLOCK, worldPosition.above(), Direction.DOWN);
		if(ResourceHandlerUtil.moveStacking(source, inputSlots, resource -> true, Integer.MAX_VALUE, null) > 0) {
			setChanged();
		}
	}

	/** Picks up siftable items lying on top of the sifter (a millstone with air under it drops its output). */
	private void suckItems() {
		AABB box = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1.25, worldPosition.getZ() + 1);
		for(ItemEntity ent : level.getEntitiesOfClass(ItemEntity.class, box, EntitySelector.ENTITY_STILL_ALIVE)) {
			ItemStack item = ent.getItem();
			int inserted = ResourceHandlerUtil.insertStacking(inputSlots, ItemResource.of(item), item.getCount(), null);
			ItemStack rest = item.copyWithCount(item.getCount() - inserted);
			if(rest.getCount() == ent.getItem().getCount()) continue;
			if(rest.isEmpty()) ent.discard();
			else ent.setItem(rest);
			setChanged();
		}
	}

	protected static void setChanged(Level p_155233_, BlockPos p_155234_, BlockState p_155235_) {
		p_155233_.blockEntityChanged(p_155234_);
		if (!p_155235_.isAir()) {
			p_155233_.updateNeighbourForOutputSignal(p_155234_, p_155235_.getBlock());
		}

	}

	/*@Override
	public void setChanged() {
		super.setChanged();
		this.dataAccess.set(0, this.siftTime);
	}*/

	@Override
	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
		if(side == null) {
			return all;
		}
		switch(side) {
			case UP:
				return input;
			case DOWN:
				return output;
			default:
				return null;
		}
	}

	@Override
	protected List<ItemStacksResourceHandler> getInventories() {
		return List.of(inputSlots, outputSlots);
	}

	@Override
	protected void modSave(ValueOutput output) {
		inputSlots.serialize(output.child("%s:inputslot".formatted(HarderOres.MODID)));
		outputSlots.serialize(output.child("%s:outputSlot".formatted(HarderOres.MODID)));
		output.putInt("%s:siftTime".formatted(HarderOres.MODID), siftTime);
		output.putInt("%s:activeSlot".formatted(HarderOres.MODID), activeSlot);
	}

	@Override
	public void modLoad(ValueInput input) {
		inputSlots.deserialize(input.childOrEmpty("%s:inputslot".formatted(HarderOres.MODID)));
		outputSlots.deserialize(input.childOrEmpty("%s:outputSlot".formatted(HarderOres.MODID)));
		siftTime = input.getIntOr("%s:siftTime".formatted(HarderOres.MODID), 0);
		activeSlot = input.getIntOr("%s:activeSlot".formatted(HarderOres.MODID), 0);
	}
}
