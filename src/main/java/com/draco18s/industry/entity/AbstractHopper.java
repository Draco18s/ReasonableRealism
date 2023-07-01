package com.draco18s.industry.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class AbstractHopper extends RandomizableContainerBlockEntity implements Hopper /*ICustomContainer, MenuProvider*/ {
	protected ItemStackHandler inventory;
	protected final LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> inventory);
	private int cooldownTime = -1;
	//private long tickedGameTime;

	public AbstractHopper(BlockEntityType<?> BlockEntityTypeIn,BlockPos pos, BlockState state) {
		super(BlockEntityTypeIn, pos, state);
		inventory = new ItemStackHandler(5);
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
		if (level != null && !level.isClientSide) {
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
		IItemHandler iinventory = getInventoryForHopperTransfer(level, pos, state);
		if (iinventory == null) {
			return false;
		} else {
			//Direction direction = this.getBlockState().get(HopperBlock.FACING).getOpposite();
			if (isInventoryFull(iinventory)) {
				return false;
			}
			else {
				for(int i = 0; i < hopper.inventory.getSlots(); ++i) {
					if (!hopper.inventory.getStackInSlot(i).isEmpty()) {
						ItemStack itemstack = hopper.inventory.extractItem(i, 1, false);
						itemstack = insertIntoAnySlot(iinventory, itemstack);
						if(itemstack.isEmpty()) {
							return true;
						}
						hopper.inventory.insertItem(i, itemstack, false);
					}
				}
			}
		}
		return false;
	}
	
	@Nullable
	private static IItemHandler getInventoryForHopperTransfer(Level level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(HopperBlock.FACING);
		return getInventoryAtPosition(level, pos.relative(direction), direction.getOpposite());
	}

	public static boolean suckInItems(Level level, AbstractHopper hopper) {
		IItemHandler sourceInven = getSourceInventory(hopper);
		if(sourceInven != null) {
			Direction direction = Direction.DOWN;
			IItemHandler hopperInven = hopper.inventory;
			return isInventoryEmpty(sourceInven) ? false : getSlotStream(sourceInven, direction).anyMatch((slot) -> {
				return pullItemFromSlot(hopperInven, sourceInven, slot);
			});
		}
		else {
			if (suckItems(hopper.level, hopper.worldPosition, hopper.getCapability(ForgeCapabilities.ITEM_HANDLER,Direction.DOWN).orElseThrow(() -> new IllegalArgumentException("Invalid LazyOptional, must not be empty")))) {
				return true;
			}

			return false;
		}
	}
	
	protected static boolean pullItemFromSlot(IItemHandler hopperInven, IItemHandler sourceInven, int slot) {
		ItemStack stack = sourceInven.extractItem(slot, 1, false);
		if(stack.isEmpty()) return false;
		return insertIntoAnySlot(hopperInven, stack).isEmpty();
	}
	
	protected static ItemStack insertIntoAnySlot(IItemHandler destination, ItemStack stack) {
		ItemStack stack2 = stack.copy();
		for(int i = 0; i < destination.getSlots() && !stack2.isEmpty(); i++) {
			if(destination.isItemValid(i, stack2) && destination.insertItem(i, stack2, true).getCount() < stack2.getCount()) {
				stack2 = destination.insertItem(i, stack2, false);
			}
		}
		return stack2;
	}
	
	@Nullable
	public static IItemHandler getSourceInventory(AbstractHopper hopper) {
		return getInventoryAtPosition(hopper.level, hopper.worldPosition.above(), Direction.DOWN);
	}

	@Nullable
	public static IItemHandler getInventoryAtPosition(Level world, BlockPos pos, Direction direction) {
		BlockEntity tileentity = world.getBlockEntity(pos);
		IItemHandler handler = null;
		if (tileentity != null) {
			handler = tileentity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).orElse(null);
		}

		/*List<Entity> list = world.getEntitiesInAABBexcluding((Entity)null, getAABB(pos), EntityPredicates.HAS_INVENTORY);
		if (!list.isEmpty()) {
			handler = list.get(world.random.nextInt(list.size())).getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(() -> new IllegalArgumentException("Invalid LazyOptional, must not be empty"));
		}*/
		List<Entity> list = world.getEntities((Entity)null, getAABB(pos), EntitySelector.CONTAINER_ENTITY_SELECTOR);
        if (!list.isEmpty()) {
           return list.get(world.random.nextInt(list.size())).getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        }

		return handler;
	}
	
	private static boolean suckItems(Level world, BlockPos pos, IItemHandler inventory) {
		List<ItemEntity> ents = world.getEntitiesOfClass(ItemEntity.class, getAABB(pos));
		if(ents.size() > 0) {
			ItemStack stack;
			ItemEntity ent;
			for(int e = ents.size()-1; e >= 0; e--) {
				ent = (ItemEntity) ents.get(e);
				stack = ent.getItem().copy();
				ItemStack stackn = insertIntoAnySlot(inventory, stack);
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
	
	protected static IntStream getSlotStream(IItemHandler sourceInven, Direction direction) {
		return IntStream.range(0, sourceInven.getSlots());
	}

	protected static boolean isInventoryEmpty(IItemHandler inven) {
		for(int i = 0; i < inven.getSlots() ; i++) {
			if(!inven.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

	protected static boolean isInventoryFull(IItemHandler inven) {
		for(int i = 0; i < inven.getSlots() ; i++) {
			ItemStack stack = inven.getStackInSlot(i);
			if(stack.getCount() < Math.min(stack.getMaxStackSize(), inven.getSlotLimit(i)))
				return false;
		}
		return true;
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
		return inventory.getSlots();
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.unpackLootTable((Player)null);
		/*this.getItems().set(p_59616_, p_59617_);
		if (p_59617_.getCount() > this.getMaxStackSize()) {
			p_59617_.setCount(this.getMaxStackSize());
		}*/

		inventory.insertItem(slot, stack, false);
		this.setChanged();
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		NonNullList<ItemStack> nlist = NonNullList.create();
		for(int i=0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			nlist.add(stack);
		}
		return nlist;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> p_59625_) {

	}

	public IItemHandler getInventory() {
		return inventory;
	}
	
	@Override
	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		modSave(nbt);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		modLoad(nbt);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    CompoundTag nbtTag = new CompoundTag();
	    modSave(nbtTag);
	    ClientboundBlockEntityDataPacket packet = ClientboundBlockEntityDataPacket.create(this, (e) -> nbtTag);
	    return packet;
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt){
	    modLoad(pkt.getTag());
	}
	
	protected void modSave(CompoundTag nbt) {
		nbt.put("%s:inventory".formatted(ExpandedIndustry.MODID), inventory.serializeNBT());
	}

	public void modLoad(CompoundTag nbt) {
		inventory.deserializeNBT(nbt.getCompound("%s:inventory".formatted(ExpandedIndustry.MODID)));
	}
}
