package com.draco18s.ores.block;

import java.util.Random;

import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.capability.CapabilityMechanicalPower;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.TileEntityAxel;
import com.draco18s.ores.entities.TileEntityMillstone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class BlockAxel extends Block{

	public BlockAxel() {
		super(Material.WOOD, MapColor.BROWN);
		setHardness(2.0f);
		setHarvestLevel("axe", 1);
		setResistance(2.0f);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(Props.AXEL_ORIENTATION, AxelOrientation.NONE).withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
	}
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing dir = getFacingFromEntity(pos, placer);
		IBlockState state = this.getDefaultState();
		if(dir == EnumFacing.UP || dir == EnumFacing.DOWN) {
			dir = EnumFacing.NORTH;
			state = state.withProperty(Props.AXEL_ORIENTATION, AxelOrientation.UP);
		}
		worldIn.scheduleBlockUpdate(pos, this, 1, 10);
		state = state.withProperty(BlockHorizontal.FACING, dir);
		return state;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {Props.AXEL_ORIENTATION, BlockHorizontal.FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int face = (meta & 3) + 2;
		return this.getDefaultState().withProperty(Props.AXEL_ORIENTATION, AxelOrientation.values()[meta>>2]).withProperty(BlockHorizontal.FACING, EnumFacing.VALUES[face]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int axel = state.getValue(Props.AXEL_ORIENTATION).getOrdinal()<<2;
		int face = state.getValue(BlockHorizontal.FACING).getIndex() - 2;
		if(face < 0) face = 0;
		return axel | face;
	}
	
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return state.getValue(Props.AXEL_ORIENTATION) == AxelOrientation.HUB;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		if(state.getValue(Props.AXEL_ORIENTATION) == AxelOrientation.HUB) {
			return new TileEntityAxel();
		}
		return null;
	}
	
	public static EnumFacing getFacingFromEntity(BlockPos pos, EntityLivingBase p_185647_1_) {
		if (MathHelper.abs((float)p_185647_1_.posX - (float)pos.getX()) < 2.0F && MathHelper.abs((float)p_185647_1_.posZ - (float)pos.getZ()) < 2.0F) {
			double d0 = p_185647_1_.posY + (double)p_185647_1_.getEyeHeight();

			/*if (d0 - (double)pos.getY() > 2.0D) {
				return EnumFacing.DOWN;
			}

			if ((double)pos.getY() - d0 > 0.0D) {
				return EnumFacing.UP;
			}*/
		}

		return p_185647_1_.getHorizontalFacing().getOpposite();
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		checkPlacement(worldIn, pos, state);
	}
	
	public boolean checkPlacement(World worldIn, BlockPos pos, IBlockState stateIn) {
		IBlockState state = stateIn;
		EnumFacing facing = stateIn.getValue(BlockHorizontal.FACING);
		System.out.println("Checking, is [" + facing + "]");
		if(worldIn.getBlockState(pos.up()).getBlock() == this) {
			System.out.println("Should point up");
			if(stateIn.getValue(Props.AXEL_ORIENTATION) != AxelOrientation.UP) {
				worldIn.scheduleBlockUpdate(pos.up(), this, 1, 10);
			}
			state = state.withProperty(Props.AXEL_ORIENTATION, AxelOrientation.UP);
		}
		else {
			if(worldIn.getBlockState(pos.down()).getBlock() == this) {
				System.out.println("Should be gears; " + facing);
				
				System.out.println(worldIn.getBlockState(pos.offset(facing)).getBlock() + ":" + worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock());
				if(worldIn.getBlockState(pos.offset(facing)).getBlock() != this && worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() == this) {
					System.out.println("Flopping");
					state = state.withProperty(BlockHorizontal.FACING, facing.getOpposite());
					worldIn.scheduleBlockUpdate(pos.offset(facing.getOpposite()), this, 1, 10);
				}
				if(stateIn.getValue(Props.AXEL_ORIENTATION) != AxelOrientation.GEARS) {
					worldIn.scheduleBlockUpdate(pos.down(), this, 1, 10);
				}
				state = state.withProperty(Props.AXEL_ORIENTATION, AxelOrientation.GEARS);
			}
			else if(worldIn.getTileEntity(pos.down()) != null && worldIn.getTileEntity(pos.down()).hasCapability(CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY, EnumFacing.DOWN)) {
				System.out.println("Should be gears (power user); " + facing);
				System.out.println(worldIn.getBlockState(pos.offset(facing)).getBlock() + ":" + worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock());
				if(worldIn.getBlockState(pos.offset(facing)).getBlock() != this && worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() == this) {
					System.out.println("Flopping");
					state = state.withProperty(BlockHorizontal.FACING, facing.getOpposite());
					worldIn.scheduleBlockUpdate(pos.offset(facing.getOpposite()), this, 1, 10);
				}
				state = state.withProperty(Props.AXEL_ORIENTATION, AxelOrientation.GEARS);
				worldIn.scheduleBlockUpdate(pos.offset(facing,1), this, 1, 10);
			}
			else {
				System.out.println("Hub?");
				EnumFacing[] checkDirs = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST};
				
				int numMatching = 0;
				for(EnumFacing dir : checkDirs) {
					if(worldIn.getBlockState(pos.offset(dir,1)).getBlock() == OresBase.windvane &&
							worldIn.getBlockState(pos.offset(dir,2)).getBlock() == OresBase.windvane &&
							worldIn.getBlockState(pos.offset(dir.getOpposite(), 1)).getBlock() == OresBase.windvane &&
							worldIn.getBlockState(pos.offset(dir.getOpposite(), 2)).getBlock() == OresBase.windvane
							) {
						numMatching++;
						IBlockState newstate = OresBase.windvane.getDefaultState();
						worldIn.setBlockState(pos.offset(dir,1), newstate.withProperty(BlockDirectional.FACING, dir));
						worldIn.setBlockState(pos.offset(dir,2), newstate.withProperty(BlockDirectional.FACING, dir));
						worldIn.setBlockState(pos.offset(dir.getOpposite(),1), newstate.withProperty(BlockDirectional.FACING, dir.getOpposite()));
						worldIn.setBlockState(pos.offset(dir.getOpposite(),2), newstate.withProperty(BlockDirectional.FACING, dir.getOpposite()));
					}
				}
				if(numMatching == 2) {
					System.out.println("	Yes");
					state = state.withProperty(Props.AXEL_ORIENTATION, AxelOrientation.HUB);
				}
				else {
					System.out.println("	No");
					state = state.withProperty(Props.AXEL_ORIENTATION, AxelOrientation.NONE);
				}
			}
			if(worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock() != this) {
				System.out.println("Rotating because not coming from axel");
				EnumFacing check = facing;
				do {
					check = check.rotateY();
					System.out.println("   " + check.getOpposite() + " is " + worldIn.getBlockState(pos.offset(check.getOpposite())).getBlock());
					IBlockState checkState = worldIn.getBlockState(pos.offset(check.getOpposite()));
					if(checkState.getBlock() == this) {
						if(checkState.getValue(Props.AXEL_ORIENTATION) == AxelOrientation.GEARS) {
							System.out.println("Neighbor is gears, adopting neighbor's facing");
							state = state.withProperty(BlockHorizontal.FACING, check);
							worldIn.scheduleBlockUpdate(pos.offset(check.getOpposite()), this, 1, 10);
						}
						else if(state.getValue(Props.AXEL_ORIENTATION) == AxelOrientation.GEARS) {
							System.out.println("I am gears, forcing my facing on neighbor");
							state = state.withProperty(BlockHorizontal.FACING, check.getOpposite());
							worldIn.scheduleBlockUpdate(pos.offset(check.getOpposite()), this, 1, 10);
						}
						else if(checkState.getValue(BlockHorizontal.FACING) == check.getOpposite()) {
							System.out.println("Adopting neighbor's facing");
							state = state.withProperty(BlockHorizontal.FACING, check.getOpposite());
						}
						else {
							System.out.println("Forcing my facing on neighbor");
							state = state.withProperty(BlockHorizontal.FACING, check);
							worldIn.scheduleBlockUpdate(pos.offset(check), this, 1, 10);
						}
						break;
					}
				} while(facing != check);
			}
			else {
				if(worldIn.getBlockState(pos.offset(facing.getOpposite())).getValue(BlockHorizontal.FACING) != facing) {
					worldIn.scheduleBlockUpdate(pos.offset(facing.getOpposite()), this, 1, 10);
				}
				if(worldIn.getBlockState(pos.offset(facing)).getBlock() == this && worldIn.getBlockState(pos.offset(facing)).getValue(BlockHorizontal.FACING) == facing.getOpposite()) {
					state = state.withProperty(BlockHorizontal.FACING, facing.getOpposite());
					worldIn.scheduleBlockUpdate(pos.offset(facing.getOpposite()), this, 1, 10);
				}
			}
		}
		facing = state.getValue(BlockHorizontal.FACING);
		System.out.println("Setting to [" + facing + "]");
		worldIn.setBlockState(pos, state, 3);
		return false;
	}
	
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if(blockIn != this)
			worldIn.scheduleBlockUpdate(pos, this, 1, 10);
		else if(state.getValue(Props.AXEL_ORIENTATION) == AxelOrientation.GEARS)
			worldIn.scheduleBlockUpdate(pos, this, 1, 10);
	}
}
