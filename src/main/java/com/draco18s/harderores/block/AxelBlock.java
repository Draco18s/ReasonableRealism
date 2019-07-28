package com.draco18s.harderores.block;

import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.AxelTileEntity;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class AxelBlock extends Block{
	private static boolean debug = false;
	private static Direction[] checkDirs = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST};

	public AxelBlock() {
		super(Properties.create(Material.ROCK, MaterialColor.STONE).hardnessAndResistance(2).harvestTool(ToolType.AXE).harvestLevel(1).sound(SoundType.WOOD));
		this.setDefaultState(this.stateContainer.getBaseState().with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.NONE).with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction dir = getFacingFromEntity(context.getPos(), context.getPlayer());
		BlockState state = this.getDefaultState();
		if(dir == Direction.UP || dir == Direction.DOWN) {
			dir = Direction.NORTH;
			state = state.with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.UP);
		}
		context.getWorld().getPendingBlockTicks().scheduleTick(context.getPos(), this, 10);
		state = state.with(BlockStateProperties.HORIZONTAL_FACING, dir);
		return state;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BlockProperties.AXEL_ORIENTATION);
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.HUB;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if(state.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.HUB) {
			return new AxelTileEntity();
		}
		return null;
	}

	public static Direction getFacingFromEntity(BlockPos pos, PlayerEntity p_185647_1_) {
		return p_185647_1_.getHorizontalFacing().getOpposite();
	}

	@Override
	public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
		checkPlacement(worldIn, pos, state);
	}

	public boolean checkPlacement(World worldIn, BlockPos pos, BlockState stateIn) {
		BlockState state = stateIn;
		Direction facing = stateIn.get(BlockStateProperties.HORIZONTAL_FACING);
		logMessage("Checking, is [" + facing + "]");
		if(worldIn.getBlockState(pos.up()).getBlock() == this) {
			logMessage("Should point up");
			if(stateIn.get(BlockProperties.AXEL_ORIENTATION) != AxelOrientation.UP) {
				worldIn.getPendingBlockTicks().scheduleTick(pos.up(), this, 10);
			}
			state = state.with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.UP);
		}
		else {
			if(worldIn.getBlockState(pos.down()).getBlock() == this) {
				logMessage("Should be gears; " + facing);

				logMessage(worldIn.getBlockState(pos.offset(facing)).getBlock() + ":" + worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock());
				if(worldIn.getBlockState(pos.offset(facing)).getBlock() != this && worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() == this) {
					logMessage("Flopping");
					state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
					worldIn.getPendingBlockTicks().scheduleTick(pos.offset(facing.getOpposite()), this, 10);
				}
				if(stateIn.get(BlockProperties.AXEL_ORIENTATION) != AxelOrientation.GEARS) {
					worldIn.getPendingBlockTicks().scheduleTick(pos.down(), this, 10);
				}
				state = state.with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.GEARS);
			}
			else if(worldIn.getTileEntity(pos.down()) != null && worldIn.getTileEntity(pos.down()).getCapability(CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY, Direction.DOWN).isPresent()) {
				logMessage("Should be gears (power user); " + facing);
				logMessage(worldIn.getBlockState(pos.offset(facing)).getBlock() + ":" + worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock());
				if(worldIn.getBlockState(pos.offset(facing)).getBlock() != this && worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() == this) {
					logMessage("Flopping");
					state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
					worldIn.getPendingBlockTicks().scheduleTick(pos.offset(facing.getOpposite()), this, 10);
				}
				state = state.with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.GEARS);
				worldIn.getPendingBlockTicks().scheduleTick(pos.offset(facing,1), this, 10);
			}
			else {
				logMessage("Hub?");

				int numMatching = 0;
				for(Direction dir : checkDirs) {
					if(worldIn.getBlockState(pos.offset(dir,1)).getBlock() == HarderOres.ModBlocks.windvane &&
							worldIn.getBlockState(pos.offset(dir,2)).getBlock() == HarderOres.ModBlocks.windvane &&
							worldIn.getBlockState(pos.offset(dir.getOpposite(), 1)).getBlock() == HarderOres.ModBlocks.windvane &&
							worldIn.getBlockState(pos.offset(dir.getOpposite(), 2)).getBlock() == HarderOres.ModBlocks.windvane
							) {
						numMatching++;
					}
				}
				if(numMatching == 2) {
					logMessage("	Yes");
					state = state.with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.HUB);
					for(Direction dir : checkDirs) {
						BlockState newstate = HarderOres.ModBlocks.windvane.getDefaultState();
						if(worldIn.getBlockState(pos.offset(dir,1)).getBlock() == HarderOres.ModBlocks.windvane) {
							worldIn.setBlockState(pos.offset(dir,1), newstate.with(BlockStateProperties.FACING, dir));
							worldIn.setBlockState(pos.offset(dir,2), newstate.with(BlockStateProperties.FACING, dir));
							worldIn.setBlockState(pos.offset(dir.getOpposite(),1), newstate.with(BlockStateProperties.FACING, dir.getOpposite()));
							worldIn.setBlockState(pos.offset(dir.getOpposite(),2), newstate.with(BlockStateProperties.FACING, dir.getOpposite()));
						}
					}
				}
				else {
					logMessage("	No");
					state = state.with(BlockProperties.AXEL_ORIENTATION, AxelOrientation.NONE);
				}
			}
			if(worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() != this) {
				logMessage("Rotating because not coming from axel");
				Direction check = facing;
				do {
					check = check.rotateY();
					logMessage("   " + check.getOpposite() + " is " + worldIn.getBlockState(pos.offset(check.getOpposite())).getBlock());
					BlockState checkState = worldIn.getBlockState(pos.offset(check.getOpposite()));
					if(checkState.getBlock() == this) {
						if(checkState.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.GEARS) {
							logMessage("Neighbor is gears, adopting neighbor's facing");
							state = state.with(BlockStateProperties.HORIZONTAL_FACING, check);
							worldIn.getPendingBlockTicks().scheduleTick(pos.offset(check.getOpposite()), this, 10);
						}
						else if(state.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.GEARS) {
							logMessage("I am gears, forcing my facing on neighbor");
							state = state.with(BlockStateProperties.HORIZONTAL_FACING, check.getOpposite());
							worldIn.getPendingBlockTicks().scheduleTick(pos.offset(check.getOpposite()), this, 10);
						}
						else if(checkState.get(BlockStateProperties.HORIZONTAL_FACING) == check.getOpposite()) {
							logMessage("Adopting neighbor's facing");
							state = state.with(BlockStateProperties.HORIZONTAL_FACING, check.getOpposite());
						}
						else {
							logMessage("Forcing my facing on neighbor");
							state = state.with(BlockStateProperties.HORIZONTAL_FACING, check);
							worldIn.getPendingBlockTicks().scheduleTick(pos.offset(check), this, 10);
						}
						break;
					}
				} while(facing != check);
			}
			else {
				if(worldIn.getBlockState(pos.offset(facing.getOpposite())).get(BlockStateProperties.HORIZONTAL_FACING) != facing) {
					worldIn.getPendingBlockTicks().scheduleTick(pos.offset(facing.getOpposite()), this, 10);
				}
				if(worldIn.getBlockState(pos.offset(facing)).getBlock() == this && worldIn.getBlockState(pos.offset(facing)).get(BlockStateProperties.HORIZONTAL_FACING) == facing.getOpposite()) {
					state = state.with(BlockStateProperties.HORIZONTAL_FACING, facing.getOpposite());
					worldIn.getPendingBlockTicks().scheduleTick(pos.offset(facing.getOpposite()), this, 10);
				}
			}
		}
		facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
		logMessage("Setting to [" + facing + "]");
		worldIn.setBlockState(pos, state, 3);
		return false;
	}

	private static void logMessage(String message) {
		if(debug)
			HarderOres.LOGGER.log(Level.DEBUG, message);
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state,world,pos,block,fromPos,isMoving);
		if(block != this) {
			world.getPendingBlockTicks().scheduleTick(pos, this, 10);
		}
		else if(state.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.GEARS) {
			world.getPendingBlockTicks().scheduleTick(pos, this, 10);
		}
	}
}