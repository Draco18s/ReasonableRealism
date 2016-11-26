package com.draco18s.ores.block;

import javax.annotation.Nullable;

import com.draco18s.ores.OreGuiHandler;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.TileEntitySifter;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockSifter extends Block {

	public BlockSifter() {
		super(Material.WOOD, MapColor.BROWN);
		setHardness(2.0f);
		setHarvestLevel("axe", 1);
		setResistance(2.0f);
		setSoundType(SoundType.WOOD);
		setCreativeTab(CreativeTabs.DECORATIONS);
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
		return new TileEntitySifter();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(OresBase.instance, OreGuiHandler.SIFTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if(!worldIn.isRemote) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
	
			IItemHandler inventory = worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					null);
			for (int i = 0; i < inventory.getSlots(); i++) {
				ItemStack stack = inventory.getStackInSlot(i);
				EntityItem entityIn;
				if (stack != null) {
					entityIn = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
					entityIn.setDefaultPickupDelay();
					worldIn.spawnEntityInWorld(entityIn);
				}
			}
		}
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}
}
