package com.draco18s.farming.entity;


import org.jetbrains.annotations.Nullable;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.inventory.LeatherSlotHandler;
import com.draco18s.farming.inventory.SaltSlotHandler;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;
import com.draco18s.hardlib.util.InventoryUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Cures a rawhide on each side into leather in 2400 ticks, or 1200 while salted. One salt lasts 4800 ticks
 * (enough for 8 hides when both sides are in use), half that in the rain.
 */
public class TannerBlockEntity extends ModBlockEntity {
	private static final int CURE_TIME = 2400;
	private static final int SALT_TIME = 4800;

	protected ItemStacksResourceHandler leftSlot = new LeatherSlotHandler();
	protected ItemStacksResourceHandler rightSlot = new LeatherSlotHandler();
	protected ItemStacksResourceHandler saltSlot = new SaltSlotHandler();
	private final ResourceHandler<ItemResource> outputSlots = new CombinedResourceHandler<>(leftSlot, rightSlot);
	private final ResourceHandler<ItemResource> everything = new CombinedResourceHandler<>(leftSlot, rightSlot, saltSlot);
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

	private boolean tickSlot(int i, ItemStacksResourceHandler slot) {
		if(!slot.getResource(0).is(HarderFarming.ModItems.raw_leather)) {
			tanningTime[i] = 0;
			return false;
		}
		tanningTime[i] += useSalt() ? 2 : 1;
		if(tanningTime[i] >= CURE_TIME) {
			slot.set(0, ItemResource.of(Items.LEATHER), 1);
			tanningTime[i] = 0;
			return true;
		}
		return false;
	}

	private boolean useSalt() {
		if(saltTime > 0) {
			return true;
		}
		if(!saltSlot.getResource(0).isEmpty()) {
			try (Transaction tx = Transaction.openRoot()) {
				saltSlot.extract(0, saltSlot.getResource(0), 1, tx);
				tx.commit();
			}
			saltTime = SALT_TIME - 1;
			updateBlockState();
			return true;
		}
		return false;
	}

	/** The block is being removed: drop the hides and salt. */
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		InventoryUtils.dropItemHandlerContents(everything, level, pos);
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

	private static LeatherStatus getLeather(ItemStacksResourceHandler slot) {
		ItemResource item = slot.getResource(0);
		if(item.is(HarderFarming.ModItems.raw_leather)) return LeatherStatus.RAW;
		if(item.is(Items.LEATHER)) return LeatherStatus.CURED;
		return LeatherStatus.NONE;
	}

	public int getSalt() {
		if(saltSlot.getResource(0).isEmpty()) {
			return (saltTime > 0) ? 1 : 0;
		}
		int size = saltSlot.getAmountAsInt(0);
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
	@Nullable
	public ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side) {
		if(side == null || level == null || level.getBlockState(worldPosition).getBlock() != getBlockState().getBlock()) {
			return everything;
		}
		if(side == Direction.UP) return saltSlot;
		if(side == Direction.DOWN) return outputSlots;
		Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		if(facing == side) return leftSlot;
		if(facing.getOpposite() == side) return rightSlot;
		return null;
	}


	@Override
	protected void modSave(ValueOutput output) {
		leftSlot.serialize(output.child("harderfarming:leftSlot"));
		rightSlot.serialize(output.child("harderfarming:rightSlot"));
		saltSlot.serialize(output.child("harderfarming:saltSlot"));
		output.putIntArray("harderfarming:tanningTime", tanningTime);
		output.putInt("harderfarming:saltTime", saltTime);
	}

	@Override
	protected void modLoad(ValueInput input) {
		leftSlot.deserialize(input.childOrEmpty("harderfarming:leftSlot"));
		rightSlot.deserialize(input.childOrEmpty("harderfarming:rightSlot"));
		saltSlot.deserialize(input.childOrEmpty("harderfarming:saltSlot"));
		tanningTime = input.getIntArray("harderfarming:tanningTime").orElse(new int[0]);
		if(tanningTime.length < 2) {
			tanningTime = new int[]{0,0};
		}
		saltTime = input.getIntOr("harderfarming:saltTime", 0);
	}
}
