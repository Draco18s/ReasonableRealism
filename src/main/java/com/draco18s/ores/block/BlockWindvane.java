package com.draco18s.ores.block;

import java.util.Random;

import com.draco18s.hardlib.api.blockproperties.Props;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWindvane extends Block {
	public static final AxisAlignedBB NORTH = new AxisAlignedBB(0.0D, 0.4D, 0.0D, 0.75D, 0.6D, 1.0D);
	public static final AxisAlignedBB WEST = new AxisAlignedBB(0.0D, 0.4D, 0.0D, 1.0D, 0.6D, 0.75D);
	public static final AxisAlignedBB UP = new AxisAlignedBB(0.3D, 0.0D, 0.4D, 1.0D, 1.0D, 0.6D);
	public static final AxisAlignedBB DOWN = new AxisAlignedBB(0.0D, 0.0D, 0.4D, 0.7D, 1.0D, 0.6D);

	public BlockWindvane() {
		super(Material.CLOTH, MapColor.SNOW);
		setHardness(1.0f);
		setResistance(0.1f);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setSoundType(SoundType.CLOTH);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockDirectional.FACING, EnumFacing.NORTH));
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
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {BlockDirectional.FACING});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.VALUES[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockDirectional.FACING).getIndex();
	}
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		worldIn.scheduleBlockUpdate(pos, this, 1, 10);
		
		for(EnumFacing face : EnumFacing.VALUES) {
			worldIn.getBlockState(pos.offset(face, 1)).neighborChanged(worldIn, pos.offset(face, 1), this);
			worldIn.getBlockState(pos.offset(face, 2)).neighborChanged(worldIn, pos.offset(face, 2), this);
		}
		
		return getDefaultState().withProperty(BlockDirectional.FACING, EnumFacing.UP);
	}
	
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		for(EnumFacing face : EnumFacing.VALUES) {
			worldIn.getBlockState(pos.offset(face, 1)).neighborChanged(worldIn, pos.offset(face, 1), this);
			worldIn.getBlockState(pos.offset(face, 2)).neighborChanged(worldIn, pos.offset(face, 2), this);
		}
	}
	
	@Override
	    @Deprecated
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch(state.getValue(BlockDirectional.FACING)) {
			case DOWN:
				return DOWN;
			case EAST:
				return WEST;
			case NORTH:
				return NORTH;
			case SOUTH:
				return NORTH;
			case UP:
				return UP;
			case WEST:
				return WEST;
			default:
				return FULL_BLOCK_AABB;
		}
    }
}
