package com.draco18s.farming.block;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.farming.entities.TileEntityTanner;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.farming.LeatherStatus;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockTanner extends Block {
	private static IProperty LEATHER1 = Props.LEFT_LEATHER_STATE;
	private static IProperty LEATHER2 = Props.RIGHT_LEATHER_STATE;
	private static IProperty SALT = Props.HAS_SALT;

	public BlockTanner() {
		super(Material.WOOD);
		setHardness(2.0f);
		setHarvestLevel("axe", 1);
		setResistance(0.0f);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.DECORATIONS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING,EnumFacing.NORTH).withProperty(LEATHER1, LeatherStatus.NONE).withProperty(LEATHER2, LeatherStatus.NONE).withProperty(SALT, false));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {BlockHorizontal.FACING,LEATHER1,LEATHER2,SALT});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BlockHorizontal.FACING).getHorizontalIndex()&1;
	}

	@Override
	@Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntityTanner te = (TileEntityTanner)world.getTileEntity(pos);
		int slot1 = te.getLeather(0);
		int slot2 = te.getLeather(1);
        return state.withProperty(SALT, te.getSalt()).withProperty(LEATHER1, LeatherStatus.values()[slot1]).withProperty(LEATHER2, LeatherStatus.values()[slot2]);
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
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityTanner();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		return true;
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing face = placer.getHorizontalFacing();
		if(face == EnumFacing.NORTH || face == EnumFacing.EAST) face = face.getOpposite();
        return this.getDefaultState().withProperty(BlockHorizontal.FACING, face);
    }
	
	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		TileEntity tileentity = worldIn.getTileEntity(pos);

		IItemHandler inventory = worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			EntityItem entityIn;
			if (stack != null && !worldIn.isRemote) {
				entityIn = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				entityIn.setDefaultPickupDelay();
				worldIn.spawnEntityInWorld(entityIn);
			}
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}
}
