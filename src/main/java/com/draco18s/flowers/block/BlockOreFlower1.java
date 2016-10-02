package com.draco18s.flowers.block;

import java.util.List;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.blockproperties.Props;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreFlower1 extends Block {

	public BlockOreFlower1() {
		super(Material.PLANTS);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
        setCreativeTab(CreativeTabs.DECORATIONS);
	}
	
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
        list.add(new ItemStack(item, 1, 3));
        list.add(new ItemStack(item, 1, 4));
        list.add(new ItemStack(item, 1, 5));
        list.add(new ItemStack(item, 1, 6));
        list.add(new ItemStack(item, 1, 7));
    }
    
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
    	boolean f = world.getBlockState(pos.down()).getBlock() != this;
    	if(plantable.getPlant(world, pos).getBlock() == this) {
    		if(state.getValue(Props.ORE_TYPE) == EnumOreType.TIN) {
    			return true&f;
    		}
    		if(state.getValue(Props.ORE_TYPE) == EnumOreType.REDSTONE) {
    			return true&f;
    		}
    	}
    	return super.canSustainPlant(state, world, pos, direction, plantable);
    }
    
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = this.getStateFromMeta(meta);
        EnumOreType thisType = state.getValue(Props.ORE_TYPE);
        if(thisType == EnumOreType.TIN || thisType == EnumOreType.REDSTONE) {
			IBlockState stateBelow = worldIn.getBlockState(pos.down());
			if(stateBelow.getValue(Props.ORE_TYPE) == thisType) {
				stateBelow.withProperty(Props.FLOWER_STALK, true);
				worldIn.setBlockState(pos.down(), stateBelow, 3);
			}
		}
        return state;
    }
    
    @Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {Props.FLOWER_STALK,Props.ORE_TYPE});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int stalk = meta&8;
		int type = meta&7 + 8; //hackery
		return this.getDefaultState().withProperty(Props.ORE_TYPE, EnumOreType.values()[type]).withProperty(Props.FLOWER_STALK, stalk>0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int stalk = state.getValue(Props.FLOWER_STALK)?8:0;
		int type = state.getValue(Props.ORE_TYPE).getOrdinal()%8;
		return stalk + type;
	}
}
