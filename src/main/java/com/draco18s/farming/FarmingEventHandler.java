package com.draco18s.farming;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.entities.ai.EntityAIAging;
import com.draco18s.farming.entities.ai.EntityAIHarvestFarmlandSmart;
import com.draco18s.farming.entities.ai.EntityAIMakeRoomForSeeds;
import com.draco18s.farming.entities.ai.EntityAIMilking;
import com.draco18s.farming.entities.ai.EntityAIWeedFarmland;
import com.draco18s.farming.entities.ai.EntityAgeTracker;
import com.draco18s.farming.entities.capabilities.CowStats;
import com.draco18s.farming.entities.capabilities.IMilking;
import com.draco18s.farming.loot.KilledByWither;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.capability.SimpleCapabilityProvider;
import com.draco18s.hardlib.api.date.HardLibDate;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.draco18s.hardlib.util.LootUtils;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.hardlib.util.LootUtils.ICondition;
import com.draco18s.hardlib.util.LootUtils.IMethod;
import com.google.gson.Gson;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootingEnchantBonus;
import net.minecraft.world.storage.loot.functions.Smelt;
import net.minecraft.world.storage.loot.properties.EntityOnFire;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
//import stellarapi.api.lib.math.Vector3;
//import stellarium.stellars.StellarManager;
//import stellarium.world.StellarDimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class FarmingEventHandler {
	public static boolean doSlowCrops;
	public static boolean doRawLeather;
	public static boolean doBiomeCrops;
	private Random rand = new Random();
	public static int cropsWorst;

	private boolean checkTAN = true;
	private boolean tanInstalled = false;
	private static Class class_SeasonASMHelper = null;
	private static Method method_getFloatTemperature = null;
	
	private HashMap<Biome,Float> biomeTemps = new HashMap<Biome,Float>();
	private Item woolItem;
	
	public FarmingEventHandler() {
	}
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void registerBiomes(RegistryEvent.Register<Biome> event) {
		Iterator<ResourceLocation> it = event.getRegistry().getKeys().iterator();
		while(it.hasNext()) {
			ResourceLocation bioName = it.next();
			Biome bio = Biome.REGISTRY.getObject(bioName);
			biomeTemps.put(bio, bio.getTemperature());
		}
	}
	
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:leather"), "harderfarming");
	}
	
	@SubscribeEvent
	public void onLivingUpdateEvent(LivingUpdateEvent event) {
		if(event.getEntityLiving() instanceof EntityVillager) {
			EntityVillager villager = (EntityVillager)event.getEntityLiving();
			if(villager.getProfession() == 0) {
				villager.world.profiler.startSection("looting");

		        if (!villager.world.isRemote && villager.canPickUpLoot() && !villager.isDead && villager.world.getGameRules().getBoolean("mobGriefing")) {
		            for (EntityItem entityitem : villager.world.getEntitiesWithinAABB(EntityItem.class, villager.getEntityBoundingBox().grow(1.0D, 0.0D, 1.0D))) {
		                if (!entityitem.isDead && !entityitem.getItem().isEmpty() && !entityitem.cannotPickup()) {
		                	ItemStack itemstack = entityitem.getItem();
		                	if (itemstack.getItem() == Items.DYE && itemstack.getItemDamage() == EnumDyeColor.WHITE.getDyeDamage()) {
		                        ItemStack itemstack1 = villager.getVillagerInventory().addItem(itemstack);

		                        if (itemstack1.isEmpty()) {
		                        	entityitem.setDead();
		                        }
		                        else {
		                            //itemstack.setCount(itemstack1.getCount());
		                        }
		                    }
		                }
		            }
		        }

		        villager.world.profiler.endSection();
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event) {
		if(woolItem == null) {
			woolItem = Item.getItemFromBlock(Blocks.WOOL);
		}
		if(event.getEntity() instanceof IShearable) {
			List<EntityItem> list = event.getDrops();
			for(EntityItem ent : list) {
				if(ent.getItem().getItem() == woolItem) {
					ent.getItem().grow(1 + rand.nextInt(2));
				}
			}
		}
	}

	@SubscribeEvent
	public void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
		if(doSlowCrops) {
			World world = event.getWorld();
			IBlockState state = event.getState();
			BlockPos pos = event.getPos();
			Biome bio = world.getBiomeForCoordsBody(pos);
			Result value = Result.DEFAULT;
			Iterator<IProperty<?>> props = state.getPropertyKeys().iterator();
			while(props.hasNext()) {
				IProperty<?> p = props.next();
				if(p instanceof PropertyInteger && p.getName().toLowerCase().equals("age")) {
					value = handleCrops(world, pos, state, bio, (PropertyInteger)p);
				}
			}
			event.setResult(value);
		}
	}
	
	private Result handleCrops(World world, BlockPos pos, IBlockState state, Biome bio, PropertyInteger ageProp) {
		int maxAge = (Integer) ageProp.getAllowedValues().toArray()[ageProp.getAllowedValues().size()-1];
		int cropage = state.getValue(ageProp);
		if(cropage == maxAge) {
			return Result.DEFAULT;
		}
		int inc = 0;
		Block block = state.getBlock();
		if(block != Blocks.REEDS) {
			//grow nearby crops instead of this, if nearby crops are younger
			Iterator<BlockPos> list = BlockPos.getAllInBox(pos.add(-1,0,-1), pos.add(1,0,1)).iterator();
			while(list.hasNext()) {
				BlockPos p = list.next();
				int ox = p.getX() - pos.getX();
				int oz = p.getZ() - pos.getZ();
				IBlockState n = world.getBlockState(p);
				Block bl = n.getBlock();
				boolean ortho = (ox == 0 || oz == 0) && ox != oz;
				if(block != FarmingBase.weeds && bl == FarmingBase.weeds && ortho) {
					int a = n.getValue(BlockCrops.AGE);
					inc += 2;
					world.scheduleBlockUpdate(p, bl, 1, 0);
				}
				else if(bl == block && ortho) {
					int a = n.getValue(ageProp);
					if(a+1 == cropage || a+2 == cropage) {
						inc += 2;
						world.scheduleBlockUpdate(p, bl, 1, 0);
					}
				}
				else if(block != FarmingBase.weeds && bl == Blocks.AIR && world.getBlockState(p.down()).getBlock() == Blocks.FARMLAND) {
					if(FarmingBase.weeds != null && rand.nextInt(1500) == 0) {
						world.setBlockState(p, FarmingBase.weeds.getDefaultState(), 3);
					}
				}
				else if(bl == Blocks.TALLGRASS || bl instanceof BlockCrops) {
					inc += 1;
				}
			}
			//end [grow nearby crops instead of this, if nearby crops are younger]
		}
		int rr = 0;
		if(doBiomeCrops) {			
			float t = getTemperature(bio,pos);
			float r = bio.getRainfall();

			float seasonalTempMod = HardLibDate.getSeasonTemp(world, world.getTotalWorldTime());
			float seasonalRainMod = HardLibDate.getSeasonRain(world, world.getTotalWorldTime());
			
			if(BiomeDictionary.hasType(bio, Type.OCEAN) || BiomeDictionary.hasType(bio, Type.RIVER)) {
				seasonalTempMod *= 0.333f;
			}
			//System.out.println("bas: " + t + "," + r);
			t = HardLibDate.modifySeasonTemp(bio, seasonalTempMod);
			r = HardLibDate.modifySeasonRain(bio, seasonalRainMod);
			//System.out.println("mod: " + seasonalTempMod + "," + seasonalRainMod);
			//System.out.println("val: " + t + "," + r);
			if(BiomeDictionary.hasType(bio, Type.NETHER) != world.getPrecipitationHeight(pos).getY() > pos.getY()) {
				//if the crop is inside, halve the effects of climate.
				//nether is treated in reverse
				t = (t + 0.8f)/2f;
				r = (r + 1)/2f;
			}
			if(block instanceof IPlantable) {
				Block block2 = ((IPlantable)block).getPlant(world, pos).getBlock();
				if(block != block2)
					block = block2;
			}
			CropWeatherOffsets o = HardLibAPI.hardCrops.getCropOffsets(block);
			
			if(o != null) {
				//TODO: Figure out time offsets without a clear idea of "year"
				//Stellar Sky?	https://minecraft.curseforge.com/projects/stellar-api
				//				https://minecraft.curseforge.com/projects/stellar-sky
				if(o.temperatureTimeOffset != 0) {
					//if(doYearCycle)
					int offset = (int) (HardLibDate.getYearLength(world) * o.temperatureTimeOffset);
					t = t - HardLibDate.getSeasonTemp(world, world.getTotalWorldTime()) + HardLibDate.getSeasonTemp(world, world.getTotalWorldTime() + offset);
				}
				t += o.temperatureFlat;
				if(o.rainfallTimeOffset != 0) {
					//if(doYearCycle)
					int offset = (int) (HardLibDate.getYearLength(world) * o.temperatureTimeOffset);
					r = r - HardLibDate.getSeasonRain(world, world.getTotalWorldTime()) + HardLibDate.getSeasonRain(world, world.getTotalWorldTime() + offset);
				}
				r += o.rainfallFlat;
				if(block == Blocks.NETHER_WART) {
					//handle alterations to the nether's temperature
					t -= biomeTemps.get(Biomes.HELL) - 2;
				}
			}
			//simplifies the following equation
			t -= 0.4f;
			r -= 1.75;

			rr = (int) Math.round((Math.pow(t-1.5f,4)*0.6f)+(t*t*2)-((r*r*-2.2)-0.3f*Math.pow(r+2f,4)+7));
			if(rr < 0) rr--;
			if(rr < -8) rr = -8;
			if(rr > cropsWorst) rr = cropsWorst;
			//end [if(doBiomeCrops)]
		}
		rr += 2*inc;
		if(block == FarmingBase.weeds) {
			//reeds always grow well
			if(rr >= 0) rr /= 2;
			else rr -= 4;
			if(rr < -9) rr = -9;
		}
		if(block == Blocks.REEDS) {
			rr -= 3;
			if(rr < -8) rr = -8;
		}

		if(rand.nextInt(10+rr) != 0) {
			return Result.DENY;
		}
		return Result.DEFAULT;
	}

	private float getTemperature(Biome biome, BlockPos pos) {
		if (isTANInstalled()) {
			try {
				if (method_getFloatTemperature == null) {
					method_getFloatTemperature = class_SeasonASMHelper.getDeclaredMethod("getFloatTemperature", Biome.class, BlockPos.class);
				}
				return (float)method_getFloatTemperature.invoke(null, biome, pos);
			} catch (Exception ex) {
				tanInstalled = false;
				//ex.printStackTrace();
				return biome.getFloatTemperature(pos);
			}
		} else {
			return biome.getFloatTemperature(pos);
		}
	}

	private boolean isTANInstalled() {
		if (checkTAN) {
            try {
                checkTAN = false;
                class_SeasonASMHelper = Class.forName("toughasnails.season.SeasonASMHelper");
                if (class_SeasonASMHelper != null) {
                    tanInstalled = true;
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        }

		return tanInstalled;
	}

	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event) {
		Entity enthurt = event.getEntity();
		if(enthurt != null) {
			Entity _hurtby = event.getSource().getTrueSource();
			
			if(enthurt instanceof EntityLivingBase && _hurtby instanceof EntityLivingBase) {
				EntityLivingBase livinghurt = (EntityLivingBase)enthurt;
				EntityLivingBase hurtby = (EntityLivingBase)_hurtby;
				ItemStack item = hurtby.getHeldItem(EnumHand.MAIN_HAND);
				if(item != null && item.getItem() == FarmingBase.butcherKnife && livinghurt instanceof EntityAnimal) {
					float amt = event.getAmount();
					
					ItemTool tool = (ItemTool)item.getItem();
					float base = (Item.ToolMaterial.valueOf(tool.getToolMaterialName()).getDamageVsEntity() / 2) - 1;
					//float base = (tool.getToolMaterial().getDamageVsEntity() / 2f)-1;
					float add = 10f/base*amt;
					
					event.setAmount(amt + add);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityAdded(EntityJoinWorldEvent event) {
		if(!event.getEntity().world.isRemote) {
			if(event.getEntity() instanceof EntityAnimal && !(event.getEntity() instanceof EntityWolf) && !(event.getEntity() instanceof IMob)) {
				EntityAnimal animal = (EntityAnimal)event.getEntity();
				if(!HardLibAPI.animalManager.isUnaging(animal.getClass()) && animal.getClass().getName().toLowerCase().contains("zombie")) {
					HardLibAPI.animalManager.addUnaging(animal.getClass());
				}
				EntityAgeTracker t = new EntityAgeTracker();
				animal.tasks.addTask(8, new EntityAIAging(new Random(), animal, t));
				if(animal instanceof EntityCow) {
					animal.tasks.addTask(100, new EntityAIMilking(animal));
				}
			}
			else if(event.getEntity() instanceof EntityVillager) {
				EntityVillager villager = (EntityVillager)event.getEntity();
				if (villager.getProfession() == 0) {
					villager.tasks.addTask(6, new EntityAIWeedFarmland(villager, 0.6D));
					for(EntityAITaskEntry task : villager.tasks.taskEntries) {
						if(task.action instanceof EntityAIHarvestFarmland) {
							villager.tasks.removeTask(task.action);
							break;
						}
					}
					villager.tasks.addTask(6, new EntityAIHarvestFarmlandSmart(villager, 0.6D));
					villager.tasks.addTask(5, new EntityAIMakeRoomForSeeds(villager));
	            }
			}
		}
	}
	
	@SubscribeEvent
	public void lootTableLoad(LootTableLoadEvent event) {
		//FamingBase.logger.log(Level.INFO, event.getName());
		LootCondition[] chance;
		LootCondition[] lootingEnchant;
		LootFunction[] count;
		LootEntryItem[] item;
		LootPool newPool;
		LootTable loot = event.getTable();
		if(event.getName().getResourcePath().equals("entities/cow")) {
			LootUtils.removeLootFromTable(loot, Items.BEEF);
			LootUtils.addItemToTable(loot, Items.BEEF, 1, 2, 1, 2, 5, 0, 1, "minecraft:beef",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
						LootFunction cooked =  new Smelt(condition);
						lootfuncs.add(cooked);
						LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
						lootfuncs.add(looting);
					}
				},
				new ICondition() {
					@Override
					public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
						lootconds.add(new KilledByWither(true));
					}
				});
		}
		
		if(event.getName().getResourcePath().equals("entities/mushroom_cow")) {
			
			LootUtils.removeLootFromTable(loot, Items.BEEF);
			LootUtils.addItemToTable(loot, Items.BEEF, 1, 2, 1, 2, 5, 0, 1, "minecraft:beef",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
						LootFunction cooked =  new Smelt(condition);
						lootfuncs.add(cooked);
						LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
						lootfuncs.add(looting);
					}
				},
				new ICondition() {
					@Override
					public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
						lootconds.add(new KilledByWither(true));
					}
				});
		}
		if(event.getName().getResourcePath().equals("entities/pig")) {
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new IMethod() {
						@Override
						public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
							LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
							lootfuncs.add(looting);
						}
					},
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 1, 1, 2, 4, 0, 1, "minecraft:leather",
					new IMethod() {
						@Override
						public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
							LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
							lootfuncs.add(looting);
						}
					},
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			LootUtils.removeLootFromTable(loot, Items.PORKCHOP);
			LootUtils.addItemToTable(loot, Items.PORKCHOP, 1, 1, 1, 3, 5, 0, 1, "minecraft:porkchop",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
						LootFunction cooked =  new Smelt(condition);
						lootfuncs.add(cooked);
						LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
						lootfuncs.add(looting);
					}
				},
				new ICondition() {
					@Override
					public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
						lootconds.add(new KilledByWither(true));
					}
				} );
			
			Gson gson = ReflectionHelper.getPrivateValue(LootTableManager.class, null, "GSON_INSTANCE","field_186526_b");
			String table = gson.toJson(loot);
		}
		if(event.getName().getResourcePath().equals("entities/sheep")) {
			LootUtils.removeLootFromTable(loot, Items.MUTTON);
			LootUtils.addItemToTable(loot, Items.MUTTON, 1, 1, 1, 2, 5, 0, 1, "minecraft:mutton", new IMethod() {
				@Override
				public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
					LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
					LootFunction cooked =  new Smelt(condition);
					lootfuncs.add(cooked);
					LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
					lootfuncs.add(looting);
				}
			},
			new ICondition() {
				@Override
				public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
					lootconds.add(new KilledByWither(true));
				}
			} );
		}
		if(event.getName().getResourcePath().equals("entities/spider")) {
			LootUtils.removeLootFromTable(loot, Items.STRING);
			LootUtils.addItemToTable(loot, Items.STRING, 1, 1, 1, 2, 4, 0, 1, "minecraft:string",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(0,1), 0);
						lootfuncs.add(looting);
					}
				}			
			);
		}
		if(event.getName().getResourcePath().equals("entities/skeleton")) {
			LootUtils.removeLootFromTable(loot, Items.BONE);
			LootUtils.addItemToTable(loot, Items.BONE, 1, 1, 1, 1, 3, 0, 1, "minecraft:bone",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(0,1), 0);
						lootfuncs.add(looting);
					}
				}
			);
			LootUtils.removeLootFromTable(loot, Items.ARROW);
			LootUtils.addItemToTable(loot, Items.ARROW, 1, 1, 1, 2, 5, 0, 1, "minecraft:arrow",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
						lootfuncs.add(looting);
					}
				}		
			);
		}
		
		if(LootUtils.removeLootFromTable(loot, Items.LEATHER)) {
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new IMethod() {
						@Override
						public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
							LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
							lootfuncs.add(looting);
						}
					},
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new IMethod() {
						@Override
						public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
							LootFunction looting = new LootingEnchantBonus(null, new RandomValueRange(1,3), 0);
							lootfuncs.add(looting);
						}
					},
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
		}
	}
	
	@SubscribeEvent
	public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
		if ((event.getState().getBlock() instanceof BlockCrops || event.getState().getBlock() instanceof BlockStem) &&  event.getState().getBlock() != FarmingBase.weeds) {
			ItemStack s = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (s != null && s.getItem() instanceof ItemHoe) {
				event.setNewSpeed(0);
			}
		}
	}
	
	//TODO: advancements
	/*@SubscribeEvent
	public void onPickup(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent event) {
		Item item = event.pickedUp.getItem().getItem();
		if(item == FarmingBase.rawLeather) {
			event.player.addStat(FarmingAchievements.collectRawhide, 1);
		}
		if(item == FarmingBase.winterWheatSeeds) {
			event.player.addStat(FarmingAchievements.collectWinterWheat, 1);
		}
	}
	
	@SubscribeEvent
	public void onCrafting(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
		Item item = event.crafting.getItem();
		if(item == Item.getItemFromBlock(FarmingBase.tanningRack)) {
			event.player.addStat(FarmingAchievements.craftTanner, 1);
		}
		if(item == FarmingBase.thermometer || item == FarmingBase.rainmeter) {
			event.player.addStat(FarmingAchievements.craftThermometer, 1);
		}
	}*/
	
	@SubscribeEvent
	public void harvestGrass(BlockEvent.HarvestDropsEvent event) {
		Block bl = event.getState().getBlock();
		if(bl instanceof BlockTallGrass) {
			Biome bio = event.getWorld().getBiome(event.getPos());
			if(biomeTemps.get(bio) <= 0.3f) {
				ArrayList<ItemStack> drps = new ArrayList<ItemStack>();
				for(ItemStack is:event.getDrops()) {
					if(is.getItem() == Items.WHEAT_SEEDS) {
						drps.add(new ItemStack(FarmingBase.winterWheatSeeds, is.getCount()));
					}
					else {
						drps.add(is);
					}
				}
				event.getDrops().clear();
				event.getDrops().addAll(drps);
			}
		}
		if(bl instanceof BlockCrops) {
			World world = event.getWorld();
			Iterator<BlockPos> list = BlockPos.getAllInBox(event.getPos().add(-1,0,-1), event.getPos().add(1,0,1)).iterator();
			boolean anyWeeds = false;
			boolean anyCarpet = false;
			while(list.hasNext()) {
				BlockPos p = list.next();
				IBlockState state = world.getBlockState(p);
				Block b = state.getBlock();
				if(b == Blocks.CARPET) {
					anyCarpet = true;
				}
				if((b == FarmingBase.weeds || b == Blocks.AIR) && world.getBlockState(p.down()).getBlock() == Blocks.FARMLAND) {
					anyWeeds = true;
				}
			}
			if(!anyWeeds && !anyCarpet) {
				boolean flag1 = bl == world.getBlockState(event.getPos().north()).getBlock() && bl == world.getBlockState(event.getPos().south()).getBlock();
				boolean flag2 = bl == world.getBlockState(event.getPos().east()).getBlock() && bl == world.getBlockState(event.getPos().west()).getBlock();
				if(flag1 ^ flag2) {
					EntityPlayer p = world.getClosestPlayer(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 5, false);
					//TODO: advancements
					//if(p != null)
					//	p.addStat(FarmingAchievements.cropRotation, 1);
				}
			}
			else if(anyCarpet) {
				EntityPlayer p = world.getClosestPlayer(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 5, false);
				//if(p != null)
				//	p.addStat(FarmingAchievements.weedSuppressor, 1);
			}
		}
	}

	@SubscribeEvent
	public void onEntityInteract(EntityInteract event) {
		if (event.getTarget() instanceof EntityAnimal) {
			EntityAnimal animal = (EntityAnimal) event.getTarget();
			ItemStack stack = event.getItemStack();
			if(stack != null) {
				if (animal.isBreedingItem(stack) && animal.getGrowingAge() == 0 && !animal.isInLove()) {
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
				if((stack.getItem() == Items.BUCKET && animal instanceof EntityCow) || (stack.getItem() == Items.BOWL && animal instanceof EntityMooshroom)) {
					IMilking data = FarmingBase.getMilkData(animal);
					if(data.getIsMilkable()) {
						data.doMilking();
					}
					else {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	private void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
		}
	}
	
	@SubscribeEvent
	public void entityCaps(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityCow) {
			final IMilking milkCap = new CowStats((EntityCow) event.getObject());
			event.addCapability(FarmingBase.MILK_ID, createProvider(milkCap));
		}
	}
	
	public static ICapabilityProvider createProvider(IMilking milkCap) {
		return new SimpleCapabilityProvider<IMilking>(FarmingBase.MILKING_CAPABILITY, null, milkCap);
	}
}
