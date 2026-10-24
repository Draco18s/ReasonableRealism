package com.draco18s.farming.entity;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.inventory.LeatherSlotHandler;
import com.draco18s.farming.inventory.SaltSlotHandler;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

/**
 * Cures a rawhide on each side into leather in 2400 ticks, or 1200 while salted. One salt lasts 4800 ticks
 * (enough for 8 hides when both sides are in use), half that in the rain.
 */
public class TannerBlockEntity extends ModBlockEntity {
	private static final int CURE_TIME = 2400;
	private static final int SALT_TIME = 4800;

	protected ItemStackHandler leftSlot = new LeatherSlotHandler();
	protected ItemStackHandler rightSlot = new LeatherSlotHandler();
	protected ItemStackHandler saltSlot = new SaltSlotHandler();
	private final LazyOptional<IItemHandler> leftSlotholder = LazyOptional.of(() -> leftSlot);
	private final LazyOptional<IItemHandler> rightSlotholder = LazyOptional.of(() -> rightSlot);
	private final LazyOptional<IItemHandler> saltSlotholder = LazyOptional.of(() -> saltSlot);
	private final LazyOptional<IItemHandler> outputSlots = LazyOptional.of(() -> new CombinedInvWrapper(leftSlot, rightSlot));
	private final LazyOptional<IItemHandler> everything = LazyOptional.of(() -> new CombinedInvWrapper(leftSlot, rightSlot, saltSlot));
	protected int[] tanningTime = new int[]{0,0};
	protected int saltTime;

	public TannerBlockEntity(BlockPos pos, BlockState state) {
		super(HarderFarming.ModBlockEntities.tanner, pos, state);
	}

	public static void tick(Level world, BlockPos pos, BlockState state, TannerBlockEntity tanner) {
		boolean changed = tanner.tickSlot(0, tanner.leftSlot);
		changed |= tanner.tickSlot(1, tanner.rightSlot);
		if(tanner.saltTime > 0) {
			tanner.saltTime -= world.isRainingAt(pos.above()) ? 2 : 1;
			if(tanner.saltTime <= 0) changed = true;
		}
		if(changed) {
			tanner.setChanged();
			tanner.updateBlockState();
		}
	}

	private boolean tickSlot(int i, ItemStackHandler slot) {
		if(slot.getStackInSlot(0).getItem() != HarderFarming.ModItems.raw_leather) {
			tanningTime[i] = 0;
			return false;
		}
		tanningTime[i] += useSalt() ? 2 : 1;
		if(tanningTime[i] >= CURE_TIME) {
			slot.setStackInSlot(0, new ItemStack(Items.LEATHER));
			tanningTime[i] = 0;
			return true;
		}
		return false;
	}

	private boolean useSalt() {
		if(saltTime > 0) {
			return true;
		}
		if(!saltSlot.getStackInSlot(0).isEmpty()) {
			saltSlot.extractItem(0, 1, false);
			saltTime = SALT_TIME - 1;
			updateBlockState();
			return true;
		}
		return false;
	}

	/** Shows the hides and the salt on the rack's model. */
	public void updateBlockState() {
		if(level == null) return;
		BlockState state = getBlockState()
				.setValue(BlockProperties.SALT_LEVEL, getSalt())
				.setValue(BlockProperties.LEFT_LEATHER_STATE, getLeather(leftSlot))
				.setValue(BlockProperties.RIGHT_LEATHER_STATE, getLeather(rightSlot));
		if(state != getBlockState()) {
			level.setBlock(worldPosition, state, Block.UPDATE_ALL);
		}
	}

	private static LeatherStatus getLeather(ItemStackHandler slot) {
		ItemStack stack = slot.getStackInSlot(0);
		if(stack.getItem() == HarderFarming.ModItems.raw_leather) return LeatherStatus.RAW;
		if(stack.getItem() == Items.LEATHER) return LeatherStatus.CURED;
		return LeatherStatus.NONE;
	}

	public int getSalt() {
		if(saltSlot.getStackInSlot(0).isEmpty()) {
			return (saltTime > 0) ? 1 : 0;
		}
		int size = saltSlot.getStackInSlot(0).getCount();
		if(size <= 2) return 2;
		if(size <= 16) return 3;
		if(size <= 32) return 4;
		if(size <= 48) return 5;
		return 6;
	}

	/**
	 * Top: the salt. Bottom: both hides (for hoppers to take leather out). Front and back: that side's hide.
	 * With no side (or once the block is gone), everything.
	 */
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		if(cap != ForgeCapabilities.ITEM_HANDLER) return super.getCapability(cap, side);
		if(side == null || level == null || level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {
			return everything.cast();
		}
		if(side == Direction.UP) return saltSlotholder.cast();
		if(side == Direction.DOWN) return outputSlots.cast();
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		if(facing == side) return leftSlotholder.cast();
		if(facing.getOpposite() == side) return rightSlotholder.cast();
		return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		super.invalidateCaps();
		leftSlotholder.invalidate();
		rightSlotholder.invalidate();
		outputSlots.invalidate();
		saltSlotholder.invalidate();
		everything.invalidate();
	}

	@Override
	protected void modSave(CompoundTag tag, HolderLookup.Provider registries) {
		tag.put("harderfarming:leftSlot", leftSlot.serializeNBT(registries));
		tag.put("harderfarming:rightSlot", rightSlot.serializeNBT(registries));
		tag.put("harderfarming:saltSlot", saltSlot.serializeNBT(registries));
		tag.putIntArray("harderfarming:tanningTime", tanningTime);
		tag.putInt("harderfarming:saltTime", saltTime);
	}

	@Override
	protected void modLoad(CompoundTag tag, HolderLookup.Provider registries) {
		leftSlot.deserializeNBT(registries, tag.getCompound("harderfarming:leftSlot"));
		rightSlot.deserializeNBT(registries, tag.getCompound("harderfarming:rightSlot"));
		saltSlot.deserializeNBT(registries, tag.getCompound("harderfarming:saltSlot"));
		tanningTime = tag.getIntArray("harderfarming:tanningTime");
		if(tanningTime.length < 2) {
			tanningTime = new int[]{0,0};
		}
		saltTime = tag.getInt("harderfarming:saltTime");
	}
}
