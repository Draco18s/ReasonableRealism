package com.draco18s.ores.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.blockproperties.Props;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockHardOreBase extends Block {
	
	public final int metaChange;
	
	public BlockHardOreBase() {
		super(Material.ROCK, MapColor.STONE);
		setResistance(5.0f);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(Props.ORE_DENSITY, 0));
		metaChange = 1;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {Props.ORE_DENSITY});
	}
	
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(Props.ORE_DENSITY, Integer.valueOf(meta));
    }
    
    public int getMetaFromState(IBlockState state) {
        return state.getValue(Props.ORE_DENSITY).intValue();
    }
	
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
	
	@Override
	@Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return super.getItemDropped(state, rand, fortune);
		//TODO: return chunk item
        //return OresBase.oreChunks;
    }

	@Override
    public int damageDropped(IBlockState state) {
    	//TODO: type dropped
        return getMetaFromState(state);
    }

	@Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
    	//TODO: quanity based on state
        return quantityDroppedWithBonus(fortune, random);
    }

	@Override
    @Nullable
    protected ItemStack createStackedBlock(IBlockState state) {
		//TODO: creative middle click
    	return super.createStackedBlock(state);
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(!worldIn.isRemote && playerIn != null && playerIn.capabilities.isCreativeMode && playerIn.getHeldItem(EnumHand.MAIN_HAND) == null) {
        	//avoidGeneration = true;
        	int m = state.getValue(Props.ORE_DENSITY);
        	m = m - (playerIn.isSneaking()?1:4);
        	if(m < 0)
        		m += 16;
        	worldIn.setBlockState(pos, state.withProperty(Props.ORE_DENSITY, m), 3);
		}
        return true;
    }
	
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        this.onBlockHarvested(world, pos, state, player);
        //return world.setBlockState(pos, net.minecraft.init.Blocks.AIR.getDefaultState(), world.isRemote ? 11 : 3);
        int m = state.getValue(Props.ORE_DENSITY);
        m -= metaChange;
        if(m < 0)
        	return world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		
		world.setBlockState(pos, state.withProperty(Props.ORE_DENSITY, m), 3);
		ItemStack itemstack1 = player.getHeldItemMainhand();
		ItemStack itemstack2 = itemstack1 == null ? null : itemstack1.copy();
		this.harvestBlock(world, player, pos, state, null, itemstack2);
        
        return false;
    }
}
