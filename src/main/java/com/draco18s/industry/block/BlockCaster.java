package com.draco18s.industry.block;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustryBase;
import com.draco18s.industry.IndustryGuiHandler;
import com.draco18s.industry.entities.TileEntityCaster;

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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockCaster extends Block {

	public BlockCaster() {
		super(Material.ROCK, MapColor.STONE);
		setHardness(2.0f);
		setHarvestLevel("pickaxe", 1);
		setResistance(2.0f);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setSoundType(SoundType.STONE);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityCaster();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(ExpandedIndustryBase.instance, IndustryGuiHandler.CASTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
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
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}
}
