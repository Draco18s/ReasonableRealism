package com.draco18s.harderores.entity;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.inventory.OreCartMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

/**
 * A minecart that holds 20 stacks and empties itself out its chute, onto one side of the track, while on a powered
 * activator rail. It also waits under a hopper pointing down into it (one that isn't locked and has something the
 * cart has room for), then carries on as it was going, so loading needs no redstone.
 */
public class OreMinecart extends AbstractMinecartContainer {
	private static final EntityDataAccessor<Boolean> DUMP_RIGHT = SynchedEntityData.defineId(OreMinecart.class, EntityDataSerializers.BOOLEAN);
	/** The way the cart last went, which the chute's side is relative to. */
	private static final EntityDataAccessor<Direction> TRAVEL = SynchedEntityData.defineId(OreMinecart.class, EntityDataSerializers.DIRECTION);
	/** The cart's comparator signal (0-15), for the height of the pile inside. */
	private static final EntityDataAccessor<Integer> FULLNESS = SynchedEntityData.defineId(OreMinecart.class, EntityDataSerializers.INT);

	/** How the cart was moving when a hopper stopped it; null when it isn't waiting. */
	private Vec3 heldMotion = null;
	private int timeOnActivator = 0;

	public OreMinecart(EntityType<? extends OreMinecart> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder entityData) {
		super.defineSynchedData(entityData);
		entityData.define(DUMP_RIGHT, true);
		entityData.define(TRAVEL, Direction.NORTH);
		entityData.define(FULLNESS, 0);
	}

	@Override
	public int getContainerSize() {
		return 20;
	}

	@Override
	protected Item getDropItem() {
		return HarderOres.ModItems.ore_cart;
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(HarderOres.ModItems.ore_cart);
	}

	@Override
	protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
		return new OreCartMenu(containerId, inventory, this);
	}

	public boolean dumpsRight() {
		return entityData.get(DUMP_RIGHT);
	}

	public void setDumpsRight(boolean right) {
		entityData.set(DUMP_RIGHT, right);
	}

	public Direction getTravel() {
		return entityData.get(TRAVEL);
	}

	/** The side of the track the chute empties onto. */
	public Direction getDumpSide() {
		return dumpsRight() ? getTravel().getClockWise() : getTravel().getCounterClockWise();
	}

	public int getFullness() {
		return entityData.get(FULLNESS);
	}

	@Override
	public void tick() {
		if(level() instanceof ServerLevel) {
			holdUnderHopper();
		}
		super.tick();
		if(level() instanceof ServerLevel) {
			Vec3 motion = getDeltaMovement();
			if(motion.horizontalDistanceSqr() > 1.0E-4) {
				entityData.set(TRAVEL, Direction.getApproximateNearest(motion.x, 0, motion.z));
			}
			entityData.set(FULLNESS, AbstractContainerMenu.getRedstoneSignalFromContainer(this));
		}
		BlockPos pos = getCurrentBlockPosOrRailBelow();
		if(!level().getBlockState(pos).is(Blocks.ACTIVATOR_RAIL)) {
			timeOnActivator = 0;
		}
	}

	/** Stops (keeping the motion) under a hopper that's loading the cart; restores the motion once it's done. */
	private void holdUnderHopper() {
		if(isLoading()) {
			if(heldMotion == null) {
				heldMotion = getDeltaMovement().multiply(1, 0, 1);
			}
			setDeltaMovement(Vec3.ZERO);
		}
		else if(heldMotion != null) {
			setDeltaMovement(heldMotion);
			heldMotion = null;
		}
	}

	/** An unlocked hopper right above, pointing down, with something in it the cart can take. */
	private boolean isLoading() {
		BlockPos above = getCurrentBlockPosOrRailBelow().above();
		BlockState state = level().getBlockState(above);
		if(!(state.getBlock() instanceof HopperBlock) || state.getValue(HopperBlock.FACING) != Direction.DOWN || !state.getValue(HopperBlock.ENABLED)) {
			return false;
		}
		if(!(level().getBlockEntity(above) instanceof Container hopper)) {
			return false;
		}
		for(int i = 0; i < hopper.getContainerSize(); i++) {
			ItemStack stack = hopper.getItem(i);
			if(!stack.isEmpty() && hasRoomFor(stack)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasRoomFor(ItemStack stack) {
		for(int i = 0; i < getContainerSize(); i++) {
			ItemStack here = getItem(i);
			if(here.isEmpty() || (ItemStack.isSameItemSameComponents(here, stack) && here.getCount() < here.getMaxStackSize())) {
				return true;
			}
		}
		return false;
	}

	/** On a powered activator rail: empty up to five stacks out the chute every half second. */
	@Override
	public void activateMinecart(ServerLevel level, int xt, int yt, int zt, boolean state) {
		if(!state) return;
		if(timeOnActivator % 10 == 0) {
			BlockPos side = new BlockPos(xt, yt, zt).relative(getDumpSide());
			int dropped = 0;
			for(int i = 0; dropped < 5 && i < getContainerSize(); i++) {
				ItemStack stack = removeItemNoUpdate(i);
				if(!stack.isEmpty()) {
					ItemEntity item = new ItemEntity(level, side.getX() + 0.5, side.getY() + 0.4, side.getZ() + 0.5, stack, 0, 0, 0);
					level.addFreshEntity(item);
					dropped++;
				}
			}
		}
		timeOnActivator++;
	}

	@Override
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putBoolean("DumpRight", dumpsRight());
		output.store("Travel", Direction.CODEC, getTravel());
		if(heldMotion != null) {
			output.store("HeldMotion", Vec3.CODEC, heldMotion);
		}
	}

	@Override
	protected void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		setDumpsRight(input.getBooleanOr("DumpRight", true));
		entityData.set(TRAVEL, input.read("Travel", Direction.CODEC).orElse(Direction.NORTH));
		heldMotion = input.read("HeldMotion", Vec3.CODEC).orElse(null);
	}
}
