package com.draco18s.ores.block;

import javax.annotation.Nullable;

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

import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.util.BlockTileEntityUtils;
import com.draco18s.ores.OreGuiHandler;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.TileEntityPackager;
import com.draco18s.ores.entities.TileEntitySifter;

public class BlockPackager extends Block {

	public BlockPackager() {
		super(Material.IRON);
		setHardness(1.0f);
		setHarvestLevel("pickaxe", 1);
		setResistance(1.0f);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.DECORATIONS);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPackager();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(OresBase.instance, OreGuiHandler.PACKAGER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		BlockTileEntityUtils.dropItems(worldIn, pos);
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}
}
