package com.draco18s.industry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PoweredRailBridgeBlock extends PoweredRailBlock {
	protected static final VoxelShape THICK_FLAT_AABB = Block.box(0.0D, -2.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public PoweredRailBridgeBlock() {
		super(Block.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL));
	}

	@Override
	public boolean isFlexibleRail(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canMakeSlopes(BlockState state, BlockGetter world, BlockPos pos) {
		return false;
	}

	@Override
	@Deprecated
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos blockpos = pos.relative(Direction.DOWN);
		BlockState blockstate = world.getBlockState(blockpos);
		return !blockstate.isFaceSturdy(world, blockpos, Direction.UP, SupportType.RIGID) || isRail(blockstate);// || isRail( world.getBlockState(pos.relative(Direction.UP)));
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!world.isClientSide) {
			if(!canSurvive(state, world, pos)) {
				//this.dropBlockAsItem(world, pos, state, 0);
				world.destroyBlock(pos, true);
			}
			else {
				this.updateState(state, world, pos, blockIn);
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return THICK_FLAT_AABB;
	}

	@Override
	@Deprecated
	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return THICK_FLAT_AABB;
	}

	@Override
	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
		if(context instanceof EntityCollisionContext ctx) {
			if(ctx.getEntity() instanceof Player p) {
				if(p.isShiftKeyDown()) {
					return THICK_FLAT_AABB;
				}
			}
		}
		/*if(reader instanceof IWorld) {
			IWorld iworld = (IWorld)reader;
			PlayerEntity p = iworld.getClosestPlayer(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, 1.5, false);
			if(p == null) {
				p = iworld.getClosestPlayer(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 1.5, false);
			}
			if(p == null) {
				p = iworld.getClosestPlayer(pos.getX()+0.5, pos.getY()+2, pos.getZ()+0.5, 1.5, false);
			}
			if(p != null && p.isSneaking()) {
				return THICK_FLAT_AABB;
			}
		}*/
		return Shapes.empty();
	}
	
	/*@Override
	public void onMinecartPass(BlockState state, World world, BlockPos pos, net.minecraft.entity.item.minecart.AbstractMinecartEntity cart) {
		//BlockState state = world.getBlockState(pos);
		RailShape railshape = getRailDirection(state, world, pos, null);
		boolean isPowered = state.get(POWERED);
		Vec3d cartMotion = cart.getMotion();
		double d15 = Math.sqrt(cartMotion.x * cartMotion.x + cartMotion.z * cartMotion.z);

		if(isPowered) {
			if (d15 > 0.01D) {
				//cartMotion = new Vec3d(cartMotion.x / d15 * 0.06D, cartMotion.y, cartMotion.z / d15 * 0.06D);
				cartMotion = cartMotion.add(cartMotion.x / d15 * 0.06D, cartMotion.y, cartMotion.z / d15 * 0.06D);
			}
			else if (railshape == RailShape.EAST_WEST) {
				if (cart.world.getBlockState(pos.west()).isNormalCube(world, pos.west())) {
					cartMotion = new Vec3d(0.02D, cartMotion.y, cartMotion.z);
				}
				else if (world.getBlockState(pos.east()).isNormalCube(world, pos.east())) {
					cartMotion = new Vec3d(-0.02D, cartMotion.y, cartMotion.z);
				}
			}
			else if (railshape == RailShape.NORTH_SOUTH) {
				if (world.getBlockState(pos.north()).isNormalCube(world, pos.north())) {
					cartMotion = new Vec3d(cartMotion.x, cartMotion.y, 0.02D);
				}
				else if (world.getBlockState(pos.south()).isNormalCube(world, pos.south())) {
					cartMotion = new Vec3d(cartMotion.x, cartMotion.y, -0.02D);
				}
			}
		}
		else {
			cartMotion = cartMotion.mul(0, 0, 0);
		}
		cart.setMotion(cartMotion);
	}*/
}
