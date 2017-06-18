package com.draco18s.industry.block;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.util.BlockTileEntityUtils;
import com.draco18s.industry.ExpandedIndustryBase;
import com.draco18s.industry.IndustryGuiHandler;
import com.draco18s.industry.entities.TileEntityFoundry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockFoundry extends Block {
	private static final AxisAlignedBB BOUNDS = new AxisAlignedBB(0,0,0,1,12f/16f,1);

	public BlockFoundry() {
		super(Material.ROCK, MapColor.STONE);
		setHardness(2.0f);
		setHarvestLevel("pickaxe", 1);
		setResistance(2.0f);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setSoundType(SoundType.STONE);
		setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BOUNDS;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {BlockHorizontal.FACING,Props.FOUNDRY_LIT});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.HORIZONTALS[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntityFoundry foundry = (TileEntityFoundry)world.getTileEntity(pos);
		if(foundry.getTime() != 0) {
			state = state.withProperty(Props.FOUNDRY_LIT, true);
		}
		else {
			state = state.withProperty(Props.FOUNDRY_LIT, false);
		}
		return state;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityFoundry();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		for(EnumFacing dir : EnumFacing.HORIZONTALS) {
			IBlockState state = world.getBlockState(pos.offset(dir));
			if(state.getBlock() == Blocks.FURNACE || state.getBlock() == Blocks.LIT_FURNACE || state.getBlock() == Blocks.FLOWING_LAVA) {
				return this.getDefaultState().withProperty(BlockHorizontal.FACING, dir.getOpposite());
			}
		}
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		for(EnumFacing dir : EnumFacing.HORIZONTALS) {
			IBlockState neighborState = world.getBlockState(pos.offset(dir));
			if(neighborState.getBlock() == Blocks.FURNACE || neighborState.getBlock() == Blocks.LIT_FURNACE || neighborState.getBlock() == Blocks.LAVA) {
				((World)world).setBlockState(pos, getDefaultState().withProperty(BlockHorizontal.FACING, dir.getOpposite()));
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		playerIn.openGui(ExpandedIndustryBase.instance, IndustryGuiHandler.FOUNDRY, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		BlockTileEntityUtils.dropItems(worldIn, pos);
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}
}
