package com.draco18s.ores.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.ores.MillstoneOrientation;
import com.draco18s.ores.entities.TileEntityMillstone;
import com.draco18s.ores.util.OresAchievements;

import net.minecraft.block.Block;
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
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockMillstone extends Block {

	public BlockMillstone() {
		super(Material.ROCK, MapColor.STONE);
		setHardness(2.0f);
		setHarvestLevel("pickaxe", 1);
		setResistance(2.0f);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setSoundType(SoundType.STONE);
		this.setDefaultState(this.blockState.getBaseState().withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {Props.MILL_ORIENTATION});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(Props.MILL_ORIENTATION).getOrdinal();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityMillstone();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(heldItem != null) {
			TileEntityMillstone te = (TileEntityMillstone)world.getTileEntity(pos);
			IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
			if(inventory == null) return false;
			ItemStack stack = heldItem.copy();
			stack.stackSize = 1;
			stack = inventory.insertItem(0, stack, true);
			if(stack == null) {
				stack = inventory.insertItem(0, heldItem.splitStack(1), false);

				MillstoneOrientation millpos = world.getBlockState(pos).getValue(Props.MILL_ORIENTATION);
				TileEntityMillstone center = (TileEntityMillstone)world.getTileEntity(te.getPos().add(millpos.offset.getX(), 0, millpos.offset.getZ()));
				
				if(center != null && center.getPower() > 0) {
					player.addStat(OresAchievements.constructMill, 1);
				}
				return true;
			}
		}
		return false;
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
        MillstoneOrientation millpos = state.getValue(Props.MILL_ORIENTATION);
        BlockPos p = pos.add(millpos.offset.getX(), 0, millpos.offset.getZ());
        worldIn.scheduleBlockUpdate(p, this, 1, 10);//low priority
        return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
    }
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        worldIn.scheduleBlockUpdate(pos, this, 1, 10);//low priority
		return this.getStateFromMeta(meta);
    }
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		checkPlacement(worldIn, pos);
		if(!checkPlacement(worldIn, pos)) {
			Iterable<BlockPos> list = pos.getAllInBox(pos.south().west(), pos.north().east());
			for(BlockPos p : list) {
				if(worldIn.getBlockState(p).getBlock() == this) {
					if(checkPlacement(worldIn, p))
						break;
				}
			}
		}
	}
	
	public boolean checkPlacement(World world, BlockPos pos) {
		IBlockState state = this.getDefaultState();
		if( world.getBlockState(pos.north().east()).getBlock()	 == this &&
			world.getBlockState(pos.east()).getBlock()			 == this &&
			world.getBlockState(pos.south().east()).getBlock()	 == this &&
			world.getBlockState(pos.south()).getBlock()			 == this &&
			world.getBlockState(pos.south().west()).getBlock()	 == this &&
			world.getBlockState(pos.west()).getBlock()			 == this &&
			world.getBlockState(pos.north().west()).getBlock()	 == this &&
			world.getBlockState(pos.north()).getBlock()			 == this) {
				world.setBlockState(pos.north().east(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NORTH_EAST), 3);
				world.setBlockState(pos.east(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.EAST), 3);
				world.setBlockState(pos.south().east(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.SOUTH_EAST), 3);
				world.setBlockState(pos.south(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.SOUTH), 3);
				world.setBlockState(pos.south().west(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.SOUTH_WEST), 3);
				world.setBlockState(pos.west(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.WEST), 3);
				world.setBlockState(pos.north().west(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NORTH_WEST), 3);
				world.setBlockState(pos.north(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NORTH), 3);
				world.setBlockState(pos, 				state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.CENTER), 3);
				return true;
		}
		else {
			if( world.getBlockState(pos.north().east()).getBlock()	 == this)
				world.setBlockState(pos.north().east(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.east()).getBlock()	 == this)
				world.setBlockState(pos.east(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.south().east()).getBlock()	 == this)
				world.setBlockState(pos.south().east(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.south()).getBlock()	 == this)
				world.setBlockState(pos.south(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.south().west()).getBlock()	 == this)
				world.setBlockState(pos.south().west(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.west()).getBlock()	 == this)
				world.setBlockState(pos.west(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.north().west()).getBlock()	 == this)
				world.setBlockState(pos.north().west(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.north()).getBlock()	 == this)
				world.setBlockState(pos.north(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos).getBlock()	 == this)
				world.setBlockState(pos, 				state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
		}
		return false;
	}
}
