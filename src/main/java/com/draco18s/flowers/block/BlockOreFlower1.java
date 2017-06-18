package com.draco18s.flowers.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.flowers.states.StateMapperFlowers;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.flowers.EnumOreFlower1;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.interfaces.IBlockWithMapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreFlower1 extends BlockBush implements IBlockWithMapper, IShearable {

	protected static final AxisAlignedBB FLOWER_AABB = new AxisAlignedBB(0.30000001192092896D, 0.0D, 0.30000001192092896D, 0.699999988079071D, 1.0D, 0.699999988079071D);

	public BlockOreFlower1() {
		super(Material.PLANTS);
		setHardness(0.0F);
		setSoundType(SoundType.PLANT);
		setCreativeTab(CreativeTabs.DECORATIONS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(Props.FLOWER_TYPE, EnumOreFlower1._1POORJOE).withProperty(Props.FLOWER_STALK, false).withProperty(Props.HAS_2D_ITEM, false));
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return FLOWER_AABB;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return getSilkTouchDrop(state);
	}

	@Override
	@Nullable
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		//return super.createStackedBlock(state);
		Item item = Item.getItemFromBlock(this);
		int i = state.getValue(Props.FLOWER_TYPE).ordinal();
		return new ItemStack(item, 1, i);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
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
			if(state.getValue(Props.FLOWER_TYPE) == EnumOreFlower1._5TANSY) {
				return true&f;
			}
			if(state.getValue(Props.FLOWER_TYPE) == EnumOreFlower1._4FLAME_LILY) {
				return true&f;
			}
		}
		return super.canSustainPlant(state, world, pos, direction, plantable);
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		IBlockState state = this.getStateFromMeta(meta);
		state = state.withProperty(Props.FLOWER_STALK, false);
		EnumOreFlower1 thisType = state.getValue(Props.FLOWER_TYPE);
		if(thisType == EnumOreFlower1._5TANSY || thisType == EnumOreFlower1._4FLAME_LILY) {
			IBlockState stateBelow = world.getBlockState(pos.down());
			if(stateBelow.getBlock() == this && stateBelow.getValue(Props.FLOWER_TYPE) == thisType) {
				stateBelow = stateBelow.withProperty(Props.FLOWER_STALK, true);
				world.setBlockState(pos.down(), stateBelow, 3);
			}
		}
		return state;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {Props.FLOWER_TYPE,Props.FLOWER_STALK,Props.HAS_2D_ITEM});
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		int stalk = meta&8;
		int type = meta&7; //+8 hackery
		return this.getDefaultState().withProperty(Props.FLOWER_TYPE, EnumOreFlower1.values()[type]).withProperty(Props.FLOWER_STALK, stalk>0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int stalk = state.getValue(Props.FLOWER_STALK)?8:0;
		int type = state.getValue(Props.FLOWER_TYPE).getOrdinal()%8;
		return stalk + type;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		int v = state.getValue(Props.FLOWER_TYPE).getOrdinal();
		OreFlowersBase.logger.log(Level.INFO,"meta from pick: " + v);
		return v;
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(world, pos, state, rand);
		if(state.getValue(Props.FLOWER_TYPE) == EnumOreFlower1._2HORSETAIL && rand.nextInt(100) == 0) {
			world.setBlockState(pos, state.withProperty(Props.FLOWER_STALK, !state.getValue(Props.FLOWER_STALK)), 3);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public StateMapperBase getStateMapper() {
		return new StateMapperFlowers(Props.FLOWER_TYPE);
	}

	@Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.025F);

        if (player.isSneaking()) {
            List<ItemStack> items = new ArrayList<ItemStack>();
            ItemStack itemstack = this.getSilkTouchDrop(state);

            if (itemstack != null)
            {
                items.add(itemstack);
            }

            ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player);
            for (ItemStack item : items)
            {
                spawnAsEntity(worldIn, pos, item);
            }
        }
        else {
            harvesters.set(player);
            int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            this.dropBlockAsItem(worldIn, pos, state, i);
            harvesters.set(null);
        }
    }

	@Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return 0;
    }

    @Override public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos){
    	return true;
    }
    
    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        ret.add(new ItemStack(this, 1, world.getBlockState(pos).getValue(Props.FLOWER_TYPE).getOrdinal()));
        return ret;
    }
}
