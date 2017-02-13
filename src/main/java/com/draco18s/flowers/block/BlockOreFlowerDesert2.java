package com.draco18s.flowers.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.flowers.states.StateMapperFlowers;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlowerDesert2;
import com.draco18s.hardlib.api.interfaces.IBlockWithMapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreFlowerDesert2 extends BlockBush implements IBlockWithMapper {

	protected static final AxisAlignedBB FLOWER_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 1.0D, 0.75D);

	public BlockOreFlowerDesert2() {
		super(Material.PLANTS);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.DECORATIONS);
		this.setDefaultState( this.blockState.getBaseState().withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2._1RAPESEED).withProperty(Props.FLOWER_STALK, false).withProperty(Props.HAS_2D_ITEM, false));
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FLOWER_AABB;
	}

	@Override
	protected boolean canSustainBush(IBlockState state) {
		Block block = state.getBlock();
		return block == Blocks.SAND || block == Blocks.HARDENED_CLAY || block == Blocks.STAINED_HARDENED_CLAY;
	}
	
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Desert;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return createStackedBlock(state);
	}

	@Override
	@Nullable
	protected ItemStack createStackedBlock(IBlockState state) {
		// return super.createStackedBlock(state);
		Item item = Item.getItemFromBlock(this);
		int i = state.getValue(Props.DESERT_FLOWER_TYPE2).ordinal();
		return new ItemStack(item, 1, i);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		//list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		//list.add(new ItemStack(item, 1, 5));
		list.add(new ItemStack(item, 1, 6));
		//list.add(new ItemStack(item, 1, 7));
	}

	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		boolean f = world.getBlockState(pos.down()).getBlock() != this;
		/*if (plantable.getPlant(world, pos).getBlock() == this) {
			EnumOreFlowerDesert2 thisType = state.getValue(Props.DESERT_FLOWER_TYPE2);
			if (thisType == EnumOreFlowerDesert2._3CHANDELIER_TREE || thisType == EnumOreFlowerDesert2._4AVELOZ) {
				return true & f;
			}
		}*/
		return super.canSustainPlant(state, world, pos, direction, plantable);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		IBlockState state = this.getStateFromMeta(meta);
		state = state.withProperty(Props.FLOWER_STALK, false);
		/*EnumOreFlowerDesert2 thisType = state.getValue(Props.DESERT_FLOWER_TYPE2);
		if (thisType == EnumOreFlowerDesert2._3CHANDELIER_TREE || thisType == EnumOreFlowerDesert2._4AVELOZ) {
			IBlockState stateBelow = worldIn.getBlockState(pos.down());
			if (stateBelow.getBlock() == this && stateBelow.getValue(Props.DESERT_FLOWER_TYPE2) == thisType) {
				stateBelow = stateBelow.withProperty(Props.FLOWER_STALK, true);
				worldIn.setBlockState(pos.down(), stateBelow, 3);
			}
		}*/
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { Props.DESERT_FLOWER_TYPE2, Props.FLOWER_STALK, Props.HAS_2D_ITEM });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int stalk = meta & 8;
		int type = meta & 7; // +8 hackery
		return this.getDefaultState().withProperty(Props.DESERT_FLOWER_TYPE2, EnumOreFlowerDesert2.values()[type]).withProperty(Props.FLOWER_STALK, stalk > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int stalk = state.getValue(Props.FLOWER_STALK) ? 8 : 0;
		int type = state.getValue(Props.DESERT_FLOWER_TYPE2).getOrdinal() % 8;
		return stalk + type;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(Props.DESERT_FLOWER_TYPE2).getOrdinal();
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public StateMapperBase getStateMapper() {
		return new StateMapperFlowers(Props.DESERT_FLOWER_TYPE2);
	}
}
