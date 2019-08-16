package com.draco18s.industry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PoweredRailBridgeBlock extends PoweredRailBlock {
	protected static final VoxelShape THICK_FLAT_AABB = Block.makeCuboidShape(0.0D, -2.0D, 0.0D, 16.0D, 2.0D, 16.0D);

	public PoweredRailBridgeBlock() {
		super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.7F).sound(SoundType.METAL));

	}

	@Override
	public boolean isFlexibleRail(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Override
	public boolean canMakeSlopes(BlockState state, IBlockReader world, BlockPos pos) {
		return false;
	}

	@Deprecated
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos blockpos = pos.offset(Direction.DOWN);
		BlockState blockstate = world.getBlockState(blockpos);
		return !blockstate.func_224755_d(world, blockpos, Direction.UP) || isRail(blockstate) || isRail( world.getBlockState(pos.offset(Direction.UP)));
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		if (!world.isRemote) {
			if(!isValidPosition(state, world, pos)) {
				//this.dropBlockAsItem(world, pos, state, 0);
				world.destroyBlock(pos, true);
			}
			else {
				this.updateState(state, world, pos, blockIn);
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return THICK_FLAT_AABB;
	}

	@Override
	@Deprecated
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return THICK_FLAT_AABB;
	}

	@Override
	@Deprecated
	public VoxelShape getCollisionShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		if(reader instanceof IWorld) {
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
		}
		return VoxelShapes.empty();
	}
	
	@Override
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
	}
}
