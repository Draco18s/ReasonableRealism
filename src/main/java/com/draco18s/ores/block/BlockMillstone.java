package com.draco18s.ores.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.util.BlockTileEntityUtils;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockMillstone extends Block {

	public BlockMillstone() {
		super(Material.ROCK, MapColor.STONE);
		setHardness(2.0f);
		setHarvestLevel("pickaxe", 1);
		setResistance(2.0f);
		setCreativeTab(CreativeTabs.DECORATIONS);
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if(!heldItem.isEmpty()) {
			TileEntityMillstone te = (TileEntityMillstone)world.getTileEntity(pos);
			IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
			if(inventory == null) return false;
			ItemStack stack = heldItem.copy();
			stack.setCount(1);
			stack = inventory.insertItem(0, stack, true);
			if(stack.isEmpty()) {
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
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		BlockTileEntityUtils.dropItems(world, pos);
		
		MillstoneOrientation millpos = world.getBlockState(pos).getValue(Props.MILL_ORIENTATION);
			if(millpos != MillstoneOrientation.NONE) {
			Iterable<BlockPos> list = pos.getAllInBox(pos.add(-1+millpos.offset.getX(),0,-1+millpos.offset.getZ()), pos.add(1+millpos.offset.getX(),0,1+millpos.offset.getZ()));
			for(BlockPos p : list) {
				if(world.getBlockState(p).getBlock() == this) {
					world.setBlockState(p, state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
				}
			}
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		world.scheduleBlockUpdate(pos, this, 1, 10);//low priority
		return this.getStateFromMeta(meta);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		//checkPlacement(worldIn, pos);
		if(!checkPlacement(worldIn, pos, false)) {
			Iterable<BlockPos> list = pos.getAllInBox(pos.add(-1,0,-1), pos.add(1,0,1));
			for(BlockPos p : list) {
				if(worldIn.getBlockState(p).getBlock() == this) {
					if(checkPlacement(worldIn, p, false))
						break;
				}
			}
		}
	}
	
	public boolean checkPlacement(World world, BlockPos pos, boolean destructive) {
		IBlockState state;// = this.getDefaultState();
		
		Iterable<BlockPos> list = pos.getAllInBox(pos.add(-1,0,-1), pos.add(1,0,1));
		int count = 0;
		for(BlockPos p : list) {
			state = world.getBlockState(p);
			if(state.getBlock() == this && state.getValue(Props.MILL_ORIENTATION) == MillstoneOrientation.NONE) {
				count++;
			}
		}
		if(count == 9) {
			System.out.println("Nine");
			for(BlockPos p : list) {
				Vec3i q = new Vec3i(p.getX(), p.getY(), p.getZ());
				BlockPos off = pos.subtract(q);
				System.out.println("  " + off);
				for(MillstoneOrientation orient : MillstoneOrientation.values()) {
					if(orient.offset.getX() == off.getX() && orient.offset.getZ() == off.getZ()) {
						world.setBlockState(p, getDefaultState().withProperty(Props.MILL_ORIENTATION, orient));
					}
				}
			}
		}
		
		
		/*if( world.getBlockState(pos.north().east()).getBlock()	 == this &&
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
			if( world.getBlockState(pos.east()).getBlock()	 == this && destructive)
				world.setBlockState(pos.east(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.south().east()).getBlock()	 == this && destructive)
				world.setBlockState(pos.south().east(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.south()).getBlock()	 == this && destructive)
				world.setBlockState(pos.south(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.south().west()).getBlock()	 == this && destructive)
				world.setBlockState(pos.south().west(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.west()).getBlock()	 == this && destructive)
				world.setBlockState(pos.west(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.north().west()).getBlock()	 == this && destructive)
				world.setBlockState(pos.north().west(), state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos.north()).getBlock()	 == this && destructive)
				world.setBlockState(pos.north(), 		state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
			if( world.getBlockState(pos).getBlock()	 == this && destructive)
				world.setBlockState(pos, 				state.withProperty(Props.MILL_ORIENTATION, MillstoneOrientation.NONE), 3);
		}*/
		return false;
	}
}
