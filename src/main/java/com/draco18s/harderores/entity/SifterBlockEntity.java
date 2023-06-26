package com.draco18s.harderores.entity;

import javax.annotation.Nullable;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.inventory.SifterContainerMenu;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.inventory.ModContainerBlockEnity;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class SifterBlockEntity extends ModContainerBlockEnity {
	private static final int SiftTime = 40;
	private int siftTime;
	private int activeSlot;
	protected final ItemStackHandler inputSlots = new ItemStackHandler(2);
	protected final ItemStackHandler outputSlots = new ItemStackHandler(1);
	protected final ItemStackHandler outputHandler = new OutputItemStackHandler(outputSlots);

	public LazyOptional<IItemHandler> input = LazyOptional.of(() -> inputSlots);
	public LazyOptional<IItemHandler> output = LazyOptional.of(() -> outputHandler);
	public LazyOptional<IItemHandler> all = LazyOptional.of(() -> new CombinedInvWrapper(inputSlots,outputHandler));

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
		return MutableComponent.create(new TranslatableContents("harderores:sifter.name", "Sifter", TranslatableContents.NO_ARGS));
	}

	@Override
	protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
		return new SifterContainerMenu(p_58627_, p_58628_, inputSlots, outputHandler, this, dataAccess);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, SifterBlockEntity sifter) {
		if(world.getBlockState(pos).getBlock() != sifter.getBlockState().getBlock()) return;
		suckItems();
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
			if(slot != sifter.activeSlot && !sifter.level.isClientSide) {
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
		ItemStack stack = sifter.inputSlots.getStackInSlot(slot);
		if(stack.isEmpty()) return;
		ItemStack result = HardLibAPI.oreMachines.getSiftResult(stack, true);
		if(result.isEmpty()) return;
		if(!sifter.outputSlots.insertItem(0, result, true).isEmpty()) return;
		sifter.inputSlots.extractItem(slot, HardLibAPI.oreMachines.getSiftAmount(stack), false);
		sifter.outputSlots.insertItem(0, result.copy(), false);
	}

	private static int canSiftAny(ItemStackHandler input, ItemStackHandler output) {
		for(int s = 0; s < input.getSlots(); s++) {
			if(canSift(s, input, output)) {
				return s;
			}
		}
		return -1;
	}

	private static boolean canSift(int slot, ItemStackHandler input, ItemStackHandler output) {
		if(input.getStackInSlot(slot).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getSiftResult(input.getStackInSlot(slot), true);
		if(result.isEmpty()) return false;
		if(!output.insertItem(0, result, true).isEmpty()) return false;
		return true;
	}

	private static void suckItems() {

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

	@SuppressWarnings({ "unchecked" })
	@NonNull
	@Override
	protected <T> LazyOptional<T> getInventoryCapability(Capability<T> cap, @Nullable Direction side) {
		if(side == null) {
			return (LazyOptional<T>) all;
		}
		switch(side) {
			case UP:
				return (LazyOptional<T>) input;
			case DOWN:
				return (LazyOptional<T>) output;
			default:
				return LazyOptional.empty();
		}
	}

	@NonNull
	@Override
	protected IItemHandlerModifiable getInventory(@Nullable Direction direction) {
		if(direction == null) {
			return new CombinedInvWrapper(inputSlots,outputSlots);
		}
		switch(direction) {
		case UP:
			return inputSlots;
		case DOWN:
			return outputHandler;

		default:
			return EMPTY_HANDLER;
		}
	}

	@Override
	protected void modSave(CompoundTag nbt) {
		nbt.put("%s:inputslot".formatted(HarderOres.MODID), inputSlots.serializeNBT());
		nbt.put("%s:outputSlot".formatted(HarderOres.MODID), outputSlots.serializeNBT());
		nbt.putInt("%s:siftTime".formatted(HarderOres.MODID), siftTime);
		nbt.putInt("%s:activeSlot".formatted(HarderOres.MODID), activeSlot);
	}

	@Override
	public void modLoad(CompoundTag nbt) {
		inputSlots.deserializeNBT(nbt.getCompound("%s:inputslot".formatted(HarderOres.MODID)));
		outputSlots.deserializeNBT(nbt.getCompound("%s:outputSlot".formatted(HarderOres.MODID)));
		siftTime = nbt.getInt("%s:siftTime".formatted(HarderOres.MODID));
		activeSlot = nbt.getInt("%s:activeSlot".formatted(HarderOres.MODID));
	}
}
