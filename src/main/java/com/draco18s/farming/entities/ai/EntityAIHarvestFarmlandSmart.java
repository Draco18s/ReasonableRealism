package com.draco18s.farming.entities.ai;

import java.util.Iterator;

import com.draco18s.farming.FarmingBase;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIHarvestFarmlandSmart extends EntityAIMoveToBlock {
	private static CropPair[] crops = { new CropPair(Blocks.WHEAT, Items.WHEAT_SEEDS),
			new CropPair(Blocks.POTATOES, Items.POTATO), new CropPair(Blocks.CARROTS, Items.CARROT),
			new CropPair(Blocks.BEETROOTS, Items.BEETROOT_SEEDS),
			new CropPair(FarmingBase.winterWheat, FarmingBase.winterWheatSeeds) };
	/** Villager that is harvesting */
	private final EntityVillager villager;
	private boolean hasFarmItem;
	private boolean wantsToReapStuff;
	private boolean wantsToGrowStuff;
	/** 0 => harvest, 1 => replant, 2 => bonemeal, -1 => none */
	private CropPair cropToPlant;
	private int currentTask;

	public EntityAIHarvestFarmlandSmart(EntityVillager villagerIn, double speedIn) {
		super(villagerIn, speedIn, 16);
		this.villager = villagerIn;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.runDelay <= 0) {
			if (!this.villager.world.getGameRules().getBoolean("mobGriefing")) {
				return false;
			}

			this.currentTask = -1;
			this.hasFarmItem = this.villager.isFarmItemInInventory();
			this.wantsToReapStuff = this.villager.wantsMoreFood();
			this.wantsToGrowStuff = this.hasBonemeal();
		}

		boolean ret1 = super.shouldExecute();
		boolean ret2 = villager.getNavigator().tryMoveToXYZ(destinationBlock.getX(), destinationBlock.getY(), destinationBlock.getZ(), this.villager.getAIMoveSpeed());
		boolean ret3 = getIsAboveDestination();
		if(!ret2) {
			this.runDelay /= 5;
		}
		return ret1 && (ret2 || ret3);
	}

	private boolean hasBonemeal() {
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);

			if(!itemstack.isEmpty() && itemstack.getItem() == Items.DYE && itemstack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
				return true;
			}
		}
		return false;
	}

	private boolean isWorthBonemealing(IBlockState iblockstate) {
		BlockCrops cropBlock = (BlockCrops)iblockstate.getBlock();
		PropertyInteger AGE = null;
		Iterator<IProperty<?>> props = iblockstate.getPropertyKeys().iterator();
		while(props.hasNext()) {
			IProperty<?> p = props.next();
			if(p instanceof PropertyInteger && p.getName().toLowerCase().equals("age")) {
				AGE = (PropertyInteger)p;
			}
		}
		if(AGE == null || iblockstate.getValue(AGE) + 1 < cropBlock.getMaxAge()) {
			return true;
		}
		return false;
		//return !((BlockCrops)iblockstate.getBlock()).isMaxAge(iblockstate);
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return this.currentTask >= 0 && super.shouldContinueExecuting();
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void updateTask() {
		super.updateTask();		
		
		this.villager.getLookHelper().setLookPosition((double) this.destinationBlock.getX() + 0.5D, (double) (this.destinationBlock.getY() + 1), (double) this.destinationBlock.getZ() + 0.5D, 10.0F, (float) this.villager.getVerticalFaceSpeed());

		if (this.getIsAboveDestination()) {
			World world = this.villager.world;
			BlockPos blockpos = this.destinationBlock.up();
			IBlockState iblockstate = world.getBlockState(blockpos);
			Block block = iblockstate.getBlock();
			boolean repeat = false;
			if (this.currentTask == 0 && block instanceof BlockCrops && ((BlockCrops)block).isMaxAge(iblockstate)) {
				world.destroyBlock(blockpos, true);
			} else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR) {
				if (cropToPlant != null) {
					world.setBlockState(blockpos, cropToPlant.cropBlock.getDefaultState(), 3);
					for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
						ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);

						if (!itemstack.isEmpty() && (itemstack.getItem() == cropToPlant.cropItem)) {
							itemstack.shrink(1);
							if (itemstack.isEmpty()) {
								villager.getVillagerInventory().setInventorySlotContents(i, ItemStack.EMPTY);
							}
							break;
						}
					}
				}
			}
			else if(this.currentTask == 2 && block instanceof BlockCrops && isWorthBonemealing(iblockstate)) {
				for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
					ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
					if(!itemstack.isEmpty() && itemstack.getItem() == Items.DYE && itemstack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
						//itemstack.shrink(1);
						ItemDye.applyBonemeal(itemstack, world, blockpos);
						if (itemstack.isEmpty()) {
							villager.getVillagerInventory().setInventorySlotContents(i, ItemStack.EMPTY);
						}
						else {
							repeat = true;
						}
						break;
					}
				}
			}
			if(!repeat)
				this.currentTask = -1;
			this.runDelay = 10;
		}
	}

	/**
	 * Return true to set given position as destination
	 */
	protected boolean shouldMoveTo(World worldIn, BlockPos pos) {
		Block block = worldIn.getBlockState(pos).getBlock();

		if (block == Blocks.FARMLAND) {
			pos = pos.up();
			IBlockState iblockstate = worldIn.getBlockState(pos);
			block = iblockstate.getBlock();

			if (block instanceof BlockCrops && this.wantsToReapStuff && ((BlockCrops)block).isMaxAge(iblockstate) && (this.currentTask == 0 || this.currentTask < 0)) {
				this.currentTask = 0;
				return true;
			}

			if (iblockstate.getMaterial() == Material.AIR && this.hasFarmItem && (this.currentTask == 1 || this.currentTask < 0)) {
				cropToPlant = null;
				for (CropPair crop : crops) {
					if (doesVillagerHave(villager, crop.cropItem) && isValidForSpot(crop.cropBlock, worldIn, pos)) {
						cropToPlant = crop;
					}
				}
				if(cropToPlant != null) {
					this.currentTask = 1;
					return true;
				}
				else {
					this.currentTask = -1;
				}
			}
			
			if(block instanceof BlockCrops && wantsToGrowStuff && isWorthBonemealing(iblockstate) && (this.currentTask == 2 || this.currentTask < 0)) {
				this.currentTask = 2;
				return true;
			}
		}

		return false;
	}

	private static boolean doesVillagerHave(EntityVillager villager, Item item) {

		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);

			if (!itemstack.isEmpty() && (itemstack.getItem() == item)) {
				return true;
			}
		}

		return false;
	}

	private static class CropPair {
		public final Block cropBlock;
		public final Item cropItem;

		public CropPair(Block block, Item item) {
			cropBlock = block;
			cropItem = item;
		}
	}

	protected static boolean isValidForSpot(Block blockIn, World worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();

		BlockPos blockpos1 = pos.north();
		BlockPos blockpos2 = pos.south();
		BlockPos blockpos3 = pos.west();
		BlockPos blockpos4 = pos.east();
		boolean flag = blockIn == worldIn.getBlockState(blockpos3).getBlock()
				|| blockIn == worldIn.getBlockState(blockpos4).getBlock();
		boolean flag1 = blockIn == worldIn.getBlockState(blockpos1).getBlock()
				|| blockIn == worldIn.getBlockState(blockpos2).getBlock();

		if (flag && flag1) {
			return false;
		} else {
			boolean flag2 = blockIn == worldIn.getBlockState(blockpos3.north()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos4.north()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos4.south()).getBlock()
					|| blockIn == worldIn.getBlockState(blockpos3.south()).getBlock();

			if (flag2) {
				return false;
			}
		}

		return true;
	}
}