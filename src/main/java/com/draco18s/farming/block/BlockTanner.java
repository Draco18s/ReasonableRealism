package com.draco18s.farming.block;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.farming.entities.TileEntityTanner;
import com.draco18s.farming.util.FarmingAchievements;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;
import com.draco18s.hardlib.util.BlockTileEntityUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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

public class BlockTanner extends Block {
	public static final AxisAlignedBB TANNER_AABB_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.2D, 1.0D, 1.0D, 0.8D);
	public static final AxisAlignedBB TANNER_AABB_WEST = new AxisAlignedBB(0.2D, 0.0D, 0.0D, 0.8D, 1.0D, 1.0D);
	
	private static IProperty LEATHER1 = Props.LEFT_LEATHER_STATE;
	private static IProperty LEATHER2 = Props.RIGHT_LEATHER_STATE;
	private static IProperty SALT = Props.SALT_LEVEL;

	public BlockTanner() {
		super(Material.WOOD);
		setHardness(2.0f);
		setResistance(0.0f);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.DECORATIONS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING,EnumFacing.NORTH).withProperty(LEATHER1, LeatherStatus.NONE).withProperty(LEATHER2, LeatherStatus.NONE).withProperty(SALT, 0));
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if(state.getValue(BlockHorizontal.FACING) == EnumFacing.SOUTH) {
			return TANNER_AABB_SOUTH;
		}
		return TANNER_AABB_WEST;
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityTanner te = (TileEntityTanner)world.getTileEntity(pos);
		IItemHandler inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
		ItemStack heldItem = player.getHeldItem(hand);
		if(inven != null) {
			if(inven.getStackInSlot(0).isEmpty()) {
				if(!heldItem.isEmpty()) {
					ItemStack remain = inven.insertItem(0, heldItem.copy(), false);
					if(side == EnumFacing.UP && (remain.isEmpty() || remain.getCount() < heldItem.getCount())) {
						//TODO: advancements
						//player.addStat(FarmingAchievements.saltedHide, 1);
					}
					
					if(remain.isEmpty()) {
						heldItem.splitStack(64);
					}
					else {
						heldItem.setCount(remain.getCount());
					}
				}
			}
			else {
				if((heldItem.isEmpty() || heldItem.getItem() == Items.LEATHER) && side != EnumFacing.UP) {
					ItemStack item = inven.extractItem(0, 1, false);
					player.inventory.addItemStackToInventory(item);
					
					if(!item.isEmpty() && item.getItem() == Items.LEATHER) {
						//TODO: advancements
						//player.addStat(FarmingAchievements.getLeather, 1);
						//player.addStat(AchievementList.KILL_COW, 1);
					}
				}
			}
		}
		te.markDirty();
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		return true;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing face = placer.getHorizontalFacing();
		if(face == EnumFacing.NORTH || face == EnumFacing.EAST) face = face.getOpposite();
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, face);
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		BlockTileEntityUtils.dropItems(worldIn, pos);
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP)) {
			dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
		}
	}

	@Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
    	return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }
}
