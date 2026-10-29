package com.draco18s.industry.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public abstract class AbstractHopper extends RandomizableContainerBlockEntity implements Hopper /*ICustomContainer, MenuProvider*/ {
	protected ItemStacksResourceHandler inventory;
	private int cooldownTime = -1;
	//private long tickedGameTime;

	public AbstractHopper(BlockEntityType<?> BlockEntityTypeIn,BlockPos pos, BlockState state) {
		super(BlockEntityTypeIn, pos, state);
		inventory = new ItemStacksResourceHandler(5);
	}

	public void setCooldown(int p_59396_) {
		this.cooldownTime = p_59396_;
	}

	private boolean isOnCooldown() {
		return this.cooldownTime > 0;
	}

	public boolean isOnCustomCooldown() {
		return this.cooldownTime > 8;
	}

	/** How many items move in one transfer, in or out (a vanilla hopper moves one). */
	protected int getTransferAmount() {
		return 1;
	}

	public static void pushItemsTick(Level level, BlockPos pos, BlockState state, AbstractHopper hopper) {
		--hopper.cooldownTime;
		//hopper.tickedGameTime = level.getGameTime();
		if (!hopper.isOnCooldown()) {
			hopper.setCooldown(0);
			tryMoveItems(level, pos, state, hopper, () -> {
				return suckInItems(level, hopper);
			});
		}
	}

	private static boolean tryMoveItems(Level level, BlockPos pos, BlockState state, AbstractHopper hopper, Supplier<Boolean> supplier) {
		if (level != null && !level.isClientSide()) {
			if (!hopper.isOnCooldown() && hopper.getBlockState().getValue(HopperBlock.ENABLED)) {
				boolean flag = false;
				if (!isInventoryEmpty(hopper.inventory)) {
					flag = transferItemsOut(level, pos, state, hopper);
				}
				if (!isInventoryFull(hopper.inventory)) {
					flag |= supplier.get();
				}

				if (flag) {
					hopper.setCooldown(8);
					//hopper.markDirty();
					return true;
				}
			}
		}
		return false;
	}

	private static boolean transferItemsOut(Level level, BlockPos pos, BlockState state, AbstractHopper hopper) {
		ResourceHandler<ItemResource> iinventory = getInventoryForHopperTransfer(level, pos, state);
		if (iinventory == null) {
			return false;
		} else {
			//Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
			if (isInventoryFull(iinventory)) {
				return false;
			}
			else {
				for(int i = 0; i < hopper.inventory.size(); ++i) {
					if (move(hopper.inventory, i, iinventory, hopper.getTransferAmount())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Nullable
	private static ResourceHandler<ItemResource> getInventoryForHopperTransfer(Level level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(HopperBlock.FACING);
		return getInventoryAtPosition(level, pos.relative(direction), direction.getOpposite());
	}

	public static boolean suckInItems(Level level, AbstractHopper hopper) {
		ResourceHandler<ItemResource> sourceInven = getSourceInventory(hopper);
		if(sourceInven != null) {
			Direction direction = Direction.DOWN;
			ResourceHandler<ItemResource> hopperInven = hopper.inventory;
			return isInventoryEmpty(sourceInven) ? false : getSlotStream(sourceInven, direction).anyMatch((slot) -> {
				return move(sourceInven, slot, hopperInven, hopper.getTransferAmount());
			});
		}
		else {
			if (suckItems(hopper.level, hopper.worldPosition, hopper.inventory)) {
				return true;
			}

			return false;
		}
	}
	
	protected static boolean pullItemFromSlot(ResourceHandler<ItemResource> hopperInven, ResourceHandler<ItemResource> sourceInven, int slot) {
		return moveOne(sourceInven, slot, hopperInven);
	}

	/** Moves one item from a slot into any slot of the destination; nothing moves if it doesn't fit. */
	protected static boolean moveOne(ResourceHandler<ItemResource> source, int slot, ResourceHandler<ItemResource> destination) {
		return move(source, slot, destination, 1);
	}

	/** Moves up to max items from a slot into the destination, as many as it has room for. */
	protected static boolean move(ResourceHandler<ItemResource> source, int slot, ResourceHandler<ItemResource> destination, int max) {
		ItemResource resource = source.getResource(slot);
		if(resource.isEmpty()) return false;
		int amount = Math.min(max, source.getAmountAsInt(slot));
		try (Transaction tx = Transaction.openRoot()) {
			int inserted = destination.insert(resource, amount, tx);
			if(inserted > 0 && source.extract(slot, resource, inserted, tx) == inserted) {
				tx.commit();
				return true;
			}
		}
		return false;
	}
	
	@Nullable
	public static ResourceHandler<ItemResource> getSourceInventory(AbstractHopper hopper) {
		return getInventoryAtPosition(hopper.level, hopper.worldPosition.above(), Direction.DOWN);
	}

	@Nullable
	public static ResourceHandler<ItemResource> getInventoryAtPosition(Level world, BlockPos pos, Direction direction) {
		ResourceHandler<ItemResource> handler = world.getCapability(Capabilities.Item.BLOCK, pos, direction);

		/*List<Entity> list = world.getEntitiesInAABBexcluding((Entity)null, getAABB(pos), EntityPredicates.HAS_INVENTORY);
		if (!list.isEmpty()) {
			handler = list.get(world.getRandom().nextInt(list.size())).getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalArgumentException("Invalid LazyOptional, must not be empty"));
		}*/
		List<Entity> list = world.getEntities((Entity)null, getAABB(pos), EntitySelector.CONTAINER_ENTITY_SELECTOR);
        if (!list.isEmpty()) {
           return list.get(world.getRandom().nextInt(list.size())).getCapability(Capabilities.Item.ENTITY_AUTOMATION, direction);
        }

		return handler;
	}
	
	private static boolean suckItems(Level world, BlockPos pos, ResourceHandler<ItemResource> inventory) {
		List<ItemEntity> ents = world.getEntitiesOfClass(ItemEntity.class, getAABB(pos));
		if(ents.size() > 0) {
			ItemStack stack;
			ItemEntity ent;
			for(int e = ents.size()-1; e >= 0; e--) {
				ent = (ItemEntity) ents.get(e);
				stack = ent.getItem().copy();
				ItemStack stackn = ItemUtil.insertItemReturnRemaining(inventory, stack, false, null);
				if(stackn.isEmpty()) {
					ent.discard();
				}
				else {
					ent.setItem(stackn);
				}
				if(stack.getCount() != stackn.getCount()) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected static AABB getAABB(BlockPos pos) {
	//	return new AABB(pos.getX() - 0.5D, pos.getY() - 0.5D, pos.getZ() - 0.5D, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		return new AABB(pos.getX()+1/8f, pos.getY()+.75f, pos.getZ()+1/8f, pos.getX()+7/8f, pos.getY()+1.05f, pos.getZ()+7/8f);
	}
	
	protected static IntStream getSlotStream(ResourceHandler<ItemResource> sourceInven, Direction direction) {
		return IntStream.range(0, sourceInven.size());
	}

	protected static boolean isInventoryEmpty(ResourceHandler<ItemResource> inven) {
		return ResourceHandlerUtil.isEmpty(inven);
	}

	protected static boolean isInventoryFull(ResourceHandler<ItemResource> inven) {
		return ResourceHandlerUtil.isFull(inven);
	}

	@Override
	public double getLevelX() {
		return (double)this.worldPosition.getX() + 0.5D;
	}

	@Override
	public double getLevelY() {
		return (double)this.worldPosition.getY() + 0.5D;
	}

	@Override
	public double getLevelZ() {
		return (double)this.worldPosition.getZ() + 0.5D;
	}

	@Override
	public int getContainerSize() {
		return inventory.size();
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.unpackLootTable((Player)null);
		/*this.getItems().set(p_59616_, p_59617_);
		if (p_59617_.getCount() > this.getMaxStackSize()) {
			p_59617_.setCount(this.getMaxStackSize());
		}*/

		inventory.set(slot, ItemResource.of(stack), stack.getCount());
		this.setChanged();
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		this.unpackLootTable((Player)null);
		ItemResource resource = inventory.getResource(slot);
		if(resource.isEmpty() || count <= 0) return ItemStack.EMPTY;
		try (Transaction tx = Transaction.openRoot()) {
			int extracted = inventory.extract(slot, resource, count, tx);
			tx.commit();
			this.setChanged();
			return resource.toStack(extracted);
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		this.unpackLootTable((Player)null);
		ItemStack stack = ItemUtil.getStack(inventory, slot);
		inventory.set(slot, ItemResource.EMPTY, 0);
		return stack;
	}

	/** The inventory lives in the item handler; this is a copy of it. */
	@Override
	protected NonNullList<ItemStack> getItems() {
		NonNullList<ItemStack> nlist = NonNullList.create();
		for(int i=0; i < inventory.size(); i++) {
			nlist.add(ItemUtil.getStack(inventory, i));
		}
		return nlist;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		for(int i = 0; i < inventory.size() && i < items.size(); i++) {
			inventory.set(i, ItemResource.of(items.get(i)), items.get(i).getCount());
		}
	}

	@Override
	public boolean isGridAligned() {
		return true;
	}

	/** The item handler capability: the hopper's inventory, from any side (see ExpandedIndustry's capability registration). */
	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
		return inventory;
	}

	public ItemStacksResourceHandler getInventory() {
		return inventory;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		modSave(output);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		modLoad(input);
	}

	/** The client gets what is saved; NeoForge's default onDataPacket and handleUpdateTag load it. */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
	    return saveCustomOnly(registries);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    return ClientboundBlockEntityDataPacket.create(this);
	}
	
	protected void modSave(ValueOutput output) {
		inventory.serialize(output.child("%s:inventory".formatted(ExpandedIndustry.MODID)));
	}

	public void modLoad(ValueInput input) {
		inventory.deserialize(input.childOrEmpty("%s:inventory".formatted(ExpandedIndustry.MODID)));
	}
}
