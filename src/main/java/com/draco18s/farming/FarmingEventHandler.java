package com.draco18s.farming;

import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.loot.KilledByWither;
import com.draco18s.hardlib.util.LootUtils;
import com.draco18s.hardlib.util.LootUtils.IMethod;
import com.draco18s.hardlib.util.RecipesUtils;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.conditions.RandomChanceWithLooting;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.Smelt;
import net.minecraft.world.storage.loot.properties.EntityOnFire;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FarmingEventHandler {
	public static boolean doRawLeather;
	private Random rand = new Random();

	/*@SubscribeEvent
	public void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
		World world = event.getWorld();
		IBlockState state = event.getState();
		BlockPos pos = event.getPos();
		if(state.getBlock() == Blocks.WHEAT) {
			Biome biomeAtPos = world.getBiomeForCoordsBody(pos);
			if(biomeAtPos.getTemperature() < 0.5f) {
				//if it is cold, deny crop growth 75% of the time
				if(world.rand.nextInt(4) == 0) {
					event.setResult(Result.DEFAULT);
				}
				else {
					event.setResult(Result.DENY);
				}
			}
			else if(biomeAtPos.getRainfall() > 0.5f) {
				//if it is warm and wet, force crop growth 25% of the time
				if(world.rand.nextInt(4) == 0) {
					event.setResult(Result.ALLOW);
				}
				else {
					event.setResult(Result.DEFAULT);
				}
			}
		}
	}*/
	
	@SubscribeEvent
	public void lootTableLoad(LootTableLoadEvent event) {
		//FamingBase.logger.log(Level.INFO, event.getName());
		LootCondition[] chance;
		LootCondition[] lootingEnchant;
		LootFunction[] count;
		LootEntryItem[] item;
		LootPool newPool;
		if(event.getName().getResourcePath().equals("entities/cow")) {
			LootTable loot = event.getTable();
			LootUtils.removeLootFromTable(loot, Items.LEATHER);
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather");
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather");
			}
			LootUtils.removeLootFromTable(loot, Items.BEEF);
			LootUtils.addItemToTable(loot, Items.BEEF, 1, 2, 1, 2, 5, 0, 1, "minecraft:beef", new IMethod() {
				@Override
				public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
					LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS),new KilledByWither(true)};
					LootFunction cooked =  new Smelt(condition);
					lootfuncs.add(cooked);
				}
			} );
		}
		if(event.getName().getResourcePath().equals("entities/pig")) {
			LootTable loot = event.getTable();
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather");
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 1, 1, 2, 4, 0, 1, "minecraft:leather");
			}
			LootUtils.removeLootFromTable(loot, Items.PORKCHOP);
			LootUtils.addItemToTable(loot, Items.PORKCHOP, 1, 1, 1, 3, 5, 0, 1, "minecraft:porkchop", new IMethod() {
				@Override
				public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
					LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS),new KilledByWither(true)};
					LootFunction cooked =  new Smelt(condition);
					lootfuncs.add(cooked);
				}
			} );
		}
		if(event.getName().getResourcePath().equals("entities/sheep")) {
			LootTable loot = event.getTable();
			
			LootUtils.removeLootFromTable(loot, Items.MUTTON);
			LootUtils.addItemToTable(loot, Items.MUTTON, 1, 1, 1, 2, 5, 0, 1, "minecraft:mutton", new IMethod() {
				@Override
				public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
					LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS),new KilledByWither(true)};
					LootFunction cooked =  new Smelt(condition);
					lootfuncs.add(cooked);
				}
			} );
		}
		if(event.getName().getResourcePath().equals("entities/spider")) {
			LootTable loot = event.getTable();
			
			LootUtils.removeLootFromTable(loot, Items.STRING);
			LootUtils.addItemToTable(loot, Items.STRING, 1, 1, 1, 2, 4, 0, 1, "minecraft:string");
		}
		if(event.getName().getResourcePath().equals("entities/skeleton")) {
			LootTable loot = event.getTable();
			
			LootUtils.removeLootFromTable(loot, Items.BONE);
			LootUtils.addItemToTable(loot, Items.BONE, 1, 1, 1, 1, 3, 0, 1, "minecraft:bone");
			LootUtils.removeLootFromTable(loot, Items.ARROW);
			LootUtils.addItemToTable(loot, Items.ARROW, 1, 1, 1, 2, 5, 0, 1, "minecraft:arrow");
		}
	}

	@SubscribeEvent
	public void onBreakCrops(BreakEvent event) {
		if ((event.getState().getBlock() instanceof BlockCrops || event.getState().getBlock() instanceof BlockStem) &&  event.getState().getBlock() != FarmingBase.weeds) {
			ItemStack s = event.getPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (s != null && s.getItem() instanceof ItemHoe) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteract event) {
		if (event.getTarget() instanceof EntityAnimal) {
			EntityAnimal animal = (EntityAnimal) event.getTarget();
			ItemStack stack = event.getItemStack();
			if (stack != null && animal.isBreedingItem(stack) && animal.getGrowingAge() == 0 && !animal.isInLove()) {
				if (animal instanceof EntityCow) {
					if (rand.nextInt(8) != 0) {
						this.consumeItemFromStack(event.getEntityPlayer(), stack);
						event.setCanceled(true);
					}
				}
				else if (animal instanceof EntityPig) {
					if (rand.nextInt(4) != 0) {
						this.consumeItemFromStack(event.getEntityPlayer(), stack);
						event.setCanceled(true);
					}
				}
				else if (animal instanceof EntitySheep) {
					if (rand.nextInt(8) != 0) {
						this.consumeItemFromStack(event.getEntityPlayer(), stack);
						event.setCanceled(true);
					}
				}
				else if (animal instanceof EntityChicken) {
					if (rand.nextInt(12) != 0) {
						this.consumeItemFromStack(event.getEntityPlayer(), stack);
						event.setCanceled(true);
					}
				}
				else if(animal instanceof EntityRabbit) {
					if (rand.nextInt(8) != 0) {
						this.consumeItemFromStack(event.getEntityPlayer(), stack);
						event.setCanceled(true);
					}
				}
				else {
					if (rand.nextInt(6) != 0) {
						this.consumeItemFromStack(event.getEntityPlayer(), stack);
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
		if (!player.capabilities.isCreativeMode) {
			--stack.stackSize;
		}
	}
}
