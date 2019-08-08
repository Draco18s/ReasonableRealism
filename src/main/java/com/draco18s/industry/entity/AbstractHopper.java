package com.draco18s.industry.entity;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;

import net.minecraft.block.HopperBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public abstract class AbstractHopper extends TileEntity implements ICustomContainer, ITickableTileEntity {
	protected ItemStackHandler inventory;
	private final LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> inventory);
	private int transferCooldown = -1;

	public AbstractHopper(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		inventory = new ItemStackHandler(5);
	}

	@Override
	public void tick() {
		if (this.world != null && !this.world.isRemote) {
			--this.transferCooldown;
			//this.tickedGameTime = this.world.getGameTime();
			if (!this.isOnTransferCooldown()) {
				this.setTransferCooldown(0);
				this.updateHopper(() -> {
					return pullItems(this);
				});
			}
		}
	}

	private boolean updateHopper(Supplier<Boolean> supplier) {
		if (this.world != null && !this.world.isRemote) {
			if (!this.isOnTransferCooldown() && this.getBlockState().get(HopperBlock.ENABLED)) {
				boolean flag = false;
				if (!isInventoryEmpty(inventory)) {
					flag = this.transferItemsOut();
				}
				if (!isInventoryFull(inventory)) {
					flag |= supplier.get();
				}

				if (flag) {
					this.setTransferCooldown(8);
					this.markDirty();
					return true;
				}
			}
		}
		return false;
	}

	private boolean transferItemsOut() {

		return false;
	}

	protected static boolean pullItems(AbstractHopper hopper) {
		IItemHandler sourceInven = getSourceInventory(hopper);
		if(sourceInven != null) {
			Direction direction = Direction.DOWN;
			return isInventoryEmpty(sourceInven) ? false : getSlotStream(sourceInven, direction).anyMatch((slot) -> {
				IItemHandler hopperInven = hopper.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
				return pullItemFromSlot(hopperInven, sourceInven, slot, direction);
			});
		}
		else {
			if (suckItems(hopper.getWorld(), hopper.getPos(), hopper.inventory)) {
				return true;
			}

			return false;
		}
	}

	protected static boolean pullItemFromSlot(IItemHandler hopperInven, IItemHandler sourceInven, int slot, Direction direction) {
		ItemStack itemstack = sourceInven.extractItem(slot, 1, false);
		if (!itemstack.isEmpty()) {
			insertIntoAnySlot(hopperInven, itemstack);
		}
		return false;
	}

	protected static IntStream getSlotStream(IItemHandler sourceInven, Direction direction) {
		return IntStream.range(0, sourceInven.getSlots());
	}

	protected static boolean isInventoryEmpty(IItemHandler inven) {
		for(int i = 0; i < inven.getSlots() ; i++) {
			if(!inven.getStackInSlot(i).isEmpty())
				return true;
		}
		return false;
	}

	protected static boolean isInventoryFull(IItemHandler inven) {
		for(int i = 0; i < inven.getSlots() ; i++) {
			ItemStack stack = inven.getStackInSlot(i);
			if(stack.getCount() < Math.min(stack.getMaxStackSize(), inven.getSlotLimit(i)))
				return true;
		}
		return false;
	}

	@Nullable
	public static IItemHandler getSourceInventory(AbstractHopper hopper) {
		return getInventoryAtPosition(hopper.getWorld(), hopper.getPos().up(), Direction.DOWN);
	}

	@Nullable
	public static IItemHandler getInventoryAtPosition(World world, BlockPos pos, Direction direction) {
		TileEntity tileentity = world.getTileEntity(pos);
		IItemHandler handler = null;
		if (tileentity != null) {
			handler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).orElse(null);
		}

		List<Entity> list = world.getEntitiesInAABBexcluding((Entity)null, getAABB(pos), EntityPredicates.HAS_INVENTORY);
		if (!list.isEmpty()) {
			handler = list.get(world.rand.nextInt(list.size())).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
		}

		return handler;
	}

	public void setTransferCooldown(int ticks) {
		this.transferCooldown = ticks;
	}

	private boolean isOnTransferCooldown() {
		return this.transferCooldown > 0;
	}

	private static boolean suckItems(World world, BlockPos pos, IItemHandler inventory) {
		List<ItemEntity> ents = world.getEntitiesWithinAABB(ItemEntity.class, getAABB(pos));
		if(ents.size() > 0) {
			ItemStack stack;
			ItemEntity ent;
			for(int e = ents.size()-1; e >= 0; e--) {
				ent = (ItemEntity) ents.get(e);
				stack = ent.getItem().copy();
				ItemStack stackn = insertIntoAnySlot(inventory, stack);
				if(stackn.isEmpty()) {
					ent.remove();
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

	protected static AxisAlignedBB getAABB(BlockPos p) {
		return new AxisAlignedBB(p.getX(), p.getY(), p.getZ(), p.getX()+1, p.getY()+1.25, p.getZ()+1);
	}

	protected static ItemStack insertIntoAnySlot(IItemHandler inventory2, ItemStack stack) {
		ItemStack stack2 = stack.copy();
		for(int i = 0; i < inventory2.getSlots() && !stack2.isEmpty(); i++) {
			if(inventory2.insertItem(i, stack2, true).getCount() < stack2.getCount()) {
				stack2 = inventory2.insertItem(i, stack2, true);
			}
		}
		return stack2;
	}

	@Override
	public void remove() {
		super.remove();
		inventoryHolder.invalidate();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return inventoryHolder.cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!world.isRemote) {
			NetworkHooks.openGui(player, this, getPos());
		}
	}

	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		read(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		tag.put("expindustry:inventory", inventory.serializeNBT());
		tag.putInt("expindustry:cooldown", transferCooldown);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		inventory.deserializeNBT(tag.getCompound("expindustry:inventory"));
		transferCooldown = tag.getInt("expindustry:cooldown");
	}

	@Override
	public abstract Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player);

	@Override
	public abstract ITextComponent getDisplayName();
}
