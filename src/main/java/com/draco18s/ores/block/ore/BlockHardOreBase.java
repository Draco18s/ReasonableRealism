package com.draco18s.ores.block.ore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.interfaces.IBlockMultiBreak;
import com.draco18s.ores.OresBase;

public abstract class BlockHardOreBase extends Block implements IBlockMultiBreak {
	public static final EnumFacing[] DROP_SEARCH_DIRECTIONS = {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN};

	public final int metaChange;
	public final EnumOreType oreType;
	private boolean avoidGeneration = false;

	public BlockHardOreBase(EnumOreType type, int metaDecrement) {
		super(Material.ROCK, MapColor.STONE);
		setResistance(5.0f);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		this.setDefaultState(this.blockState.getBaseState().withProperty(Props.ORE_DENSITY, 0));
		oreType = type;
		metaChange = metaDecrement;
	}

	@Override
	public int getDensityChangeOnBreak(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		return metaChange;
	}

	@Override
	public Color getProspectorParticleColor(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		return Color.WHITE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 15));
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
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return OresBase.rawOre;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.oreType.meta;
	}

	/*@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		return quantityDroppedWithBonus(fortune, random);
	}*/

	@Override
	public abstract int quantityDropped(IBlockState state, int fortune, Random random);

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return createStackedBlock(state);
	}

	@Override
	@Nullable
	protected ItemStack createStackedBlock(IBlockState state) {
		//return super.createStackedBlock(state);
		Item item = Item.getItemFromBlock(this);
		int i = this.getMetaFromState(state);
		return new ItemStack(item, 1, i);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		if(playerIn != null && playerIn.capabilities.isCreativeMode && playerIn.getHeldItem(EnumHand.MAIN_HAND) == null) {
			if(!worldIn.isRemote) {
				//avoidGeneration = true;
				int m = state.getValue(Props.ORE_DENSITY);
				m = m - (playerIn.isSneaking()?1:4);
				System.out.println(m);
				if(m < 0)
					m += 16;
				worldIn.setBlockState(pos, state.withProperty(Props.ORE_DENSITY, m), 3);
			}
			return true;
		}
		return false;
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		Random rand = world instanceof World ? ((World)world).rand : RANDOM;
		int count = quantityDropped(state, fortune, rand);
		Item item = getItemDropped(state, rand, fortune);
		ret.add(new ItemStack(item, count, damageDropped(state)));
		int metadata = state.getValue(Props.ORE_DENSITY);
		for(int m = metadata-metaChange; m >= 0; m-=metaChange) {
			ArrayList<ItemStack> extra = getDropsStandard(world, pos, state.withProperty(Props.ORE_DENSITY, metadata), fortune, rand);
			//25% lost if not using API methods
			//player mining will avoid this, as only the first stack (above) is usex by dropBlockAsItemWithChance
			for(ItemStack ex : extra) {
				float f = ex.stackSize * 0.75f;
				int c = (int) Math.floor(f);
				f -= c;
				if(f > 0 && rand.nextFloat() < f) {
					c++;
				}
				ex.stackSize = c;
			}
			ret.addAll(extra);
		}

		return ret;
	}

	protected ArrayList<ItemStack> getDropsStandard(IBlockAccess world, BlockPos pos, IBlockState state, int fortune, Random rand) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		Item item = getItemDropped(state, rand, fortune);
		int count = quantityDropped(state, fortune, rand);
		ret.add(new ItemStack(item, count, damageDropped(state)));
		return ret;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		/*if(player != null && player.capabilities.isCreativeMode) {
			avoidGeneration = true;
			world.setBlockToAir(pos);
			return true;
		}*/
		if(willHarvest) {
			this.onBlockHarvested(world, pos, state, player);
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
		return true;
	}

	@Override
	public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!worldIn.isRemote && !worldIn.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
			java.util.List<ItemStack> items = getDropsStandard(worldIn, pos, state, fortune, worldIn.rand);
			chance = net.minecraftforge.event.ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, fortune, chance, false, harvesters.get());

			for (ItemStack item : items) {
				if (worldIn.rand.nextFloat() <= chance) {
					spawnAsEntity(worldIn, pos, item);
				}
			}
		}
	}

	public static void spawnAsEntity(World worldIn, BlockPos pos, ItemStack stack) {
		if (!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops") && !worldIn.restoringBlockSnapshots) { // do not drop items while restoring blockstates, prevents item dupe
			if (captureDrops.get()) {
				capturedDrops.get().add(stack);
				return;
			}
			float f = 0.5F;
			double d0 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			double d1 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			double d2 = (double)(worldIn.rand.nextFloat() * 0.5F) + 0.25D;
			if(!worldIn.getBlockState(pos).isNormalCube()) {
				EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
				entityitem.setDefaultPickupDelay();
				worldIn.spawnEntityInWorld(entityitem);
				return;
			}
			else {
				for(EnumFacing dir : DROP_SEARCH_DIRECTIONS) {
					if(!worldIn.getBlockState(pos.offset(dir)).isNormalCube() || dir == EnumFacing.DOWN) {
						EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0+dir.getFrontOffsetX(), (double)pos.getY() + d1+dir.getFrontOffsetY(), (double)pos.getZ() + d2+dir.getFrontOffsetZ(), stack);
						entityitem.setDefaultPickupDelay();
						worldIn.spawnEntityInWorld(entityitem);
						return;
					}
				}
			}
		}
	}
}
