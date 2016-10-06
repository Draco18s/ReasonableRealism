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

import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.ores.GuiHandler;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.entities.TileEntitySifter;

public class BlockSifter extends Block {

	public BlockSifter() {
		super(Material.WOOD, MapColor.BROWN);
		setHardness(2.0f);
		setHarvestLevel("axe", 1);
		setResistance(2.0f);
		setSoundType(SoundType.WOOD);
        setCreativeTab(CreativeTabs.DECORATIONS);
	}
	
	public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

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
		playerIn.openGui(OresBase.instance, GuiHandler.SIFTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
	//public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        IItemHandler inventory = worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        for(int i=0; i < inventory.getSlots(); i++) {
        	ItemStack stack = inventory.getStackInSlot(i);
    		EntityItem entityIn;
    		if(stack != null) {
    			entityIn = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
    			entityIn.setDefaultPickupDelay();
    			worldIn.spawnEntityInWorld(entityIn);
    		}
        }
        /*Props.MillstoneOrientation millpos = state.getValue(Props.MILL_ORIENTATION);
        BlockPos p = pos.add(millpos.offset.getX(), 0, millpos.offset.getZ());
        worldIn.scheduleBlockUpdate(p, this, 1, 10);//low priority*/
        return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
    }
}
