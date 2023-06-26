package com.draco18s.industry.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RailBridgeBlock extends BaseRailBlock {
	protected static final VoxelShape THICK_FLAT_AABB = Block.box(0.0D, -2.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

	public RailBridgeBlock() {
		super(true, Block.Properties.of(Material.DECORATION).noCollission().strength(0.7F).sound(SoundType.METAL));
		this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_55408_) {
		p_55408_.add(SHAPE, WATERLOGGED);
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

	@Override
	public Property<RailShape> getShapeProperty() {
		return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
	}
}
