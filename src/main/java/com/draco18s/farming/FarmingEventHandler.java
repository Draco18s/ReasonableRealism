package com.draco18s.farming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.entities.EntityItemFrameReplacement;
import com.draco18s.farming.entities.ai.EntityAIAging;
import com.draco18s.farming.entities.ai.EntityAIMilking;
import com.draco18s.farming.entities.ai.EntityAgeTracker;
import com.draco18s.farming.entities.capabilities.CowStats;
import com.draco18s.farming.entities.capabilities.IMilking;
import com.draco18s.farming.entities.capabilities.MilkStorage;
import com.draco18s.farming.loot.KilledByWither;
import com.draco18s.farming.util.FarmingAchievements;
import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.flowers.util.FlowerAchievements;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.capability.SimpleCapabilityProvider;
import com.draco18s.hardlib.internal.CropWeatherOffsets;
import com.draco18s.hardlib.util.LootUtils;
import com.draco18s.hardlib.util.LootUtils.ICondition;
import com.draco18s.hardlib.util.LootUtils.IMethod;

import CustomOreGen.Util.CogOreGenEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.conditions.EntityHasProperty;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.Smelt;
import net.minecraft.world.storage.loot.properties.EntityOnFire;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FarmingEventHandler {
	public static boolean doSlowCrops;
	public static boolean doRawLeather;
	public static boolean doBiomeCrops;
	private Random rand = new Random();
	public static int cropsWorst;
	
	private HashMap<Biome,Float> biomeTemps = new HashMap<Biome,Float>();
	
	public FarmingEventHandler() {
		Iterator<ResourceLocation> it = Biome.REGISTRY.getKeys().iterator();
		while(it.hasNext()) {
			ResourceLocation bioName = it.next();
			Biome bio = Biome.REGISTRY.getObject(bioName);
			biomeTemps.put(bio, bio.getTemperature());
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
			
			Iterator<IProperty<?>> props = state.getProperties().keySet().iterator();
			while(props.hasNext()) {
				IProperty<?> p = props.next();
				if(p instanceof PropertyInteger && p.getName().toLowerCase().equals("age")) {
					value = handleCrops(world, pos, state, bio, (PropertyInteger)p);
				}
			}
			
			/*if(state.getProperties().containsKey(BlockCrops.AGE)) {
				value = handleCrops(world, pos, state, bio, BlockCrops.AGE);
			}
			if(state.getProperties().containsKey(BlockStem.AGE)) {
				value = handleCrops(world, pos, state, bio, BlockStem.AGE);
			}
			if(state.getProperties().containsKey(BlockReed.AGE)) {
				value = handleCrops(world, pos, state, bio, BlockReed.AGE);
			}
			if(state.getProperties().containsKey(BlockCocoa.AGE)) {
				value = handleCrops(world, pos, state, bio, BlockCocoa.AGE);
			}*/
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
			Iterator<BlockPos> list = BlockPos.getAllInBox(pos.south().west(), pos.north().east()).iterator();
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
			float t = bio.getTemperature();
			float r = bio.getRainfall();
			if(BiomeDictionary.isBiomeOfType(bio, Type.OCEAN) || BiomeDictionary.isBiomeOfType(bio, Type.RIVER)) {
				//TODO: figure out this
				//lesson the effects of temperature on Ocean and River biomes
				//t += getSeasonTemp(getLastWorldTime(world.provider.dimensionId)) * 0.333f;
			}
			if(BiomeDictionary.isBiomeOfType(bio, Type.NETHER) != world.getPrecipitationHeight(pos).getY() > pos.getY()) {
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
				
				/*if(o.temperatureTimeOffset != 0) {
					if(doYearCycle)
						t = t - getSeasonTemp(getLastWorldTime(world.provider.dimensionId)) + getSeasonTemp(getLastWorldTime(world.provider.dimensionId) + o.temperatureTimeOffset);
				}*/
				t += o.temperatureFlat;
				/*if(o.rainfallTimeOffset != 0) {
					if(doYearCycle)
						r = r - getSeasonRain(getLastWorldTime(world.provider.dimensionId)) + getSeasonRain(getLastWorldTime(world.provider.dimensionId) + o.rainfallTimeOffset);
				}*/
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

	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event) {
		Entity enthurt = event.getEntity();
		if(enthurt != null) {
			Entity _hurtby = event.getSource().getEntity();
			
			if(enthurt instanceof EntityLivingBase && _hurtby instanceof EntityLivingBase) {
				EntityLivingBase livinghurt = (EntityLivingBase)enthurt;
				EntityLivingBase hurtby = (EntityLivingBase)_hurtby;
				ItemStack item = hurtby.getHeldItem(EnumHand.MAIN_HAND);
				if(item != null && item.getItem() == FarmingBase.butcherKnife && livinghurt instanceof EntityAnimal) {
					float amt = event.getAmount();
					
					ItemTool tool = (ItemTool)item.getItem();
					float base = (tool.getToolMaterial().getDamageVsEntity() / 2f)-1;
					float add = 10f/base*amt;
					
					event.setAmount(amt + add);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityAdded(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof EntityAnimal && !event.getEntity().worldObj.isRemote && !(event.getEntity() instanceof EntityWolf)) {
			EntityAnimal animal = (EntityAnimal)event.getEntity();
			EntityAgeTracker t = new EntityAgeTracker();
			animal.tasks.addTask(8, new EntityAIAging(new Random(), animal, t));
			if(animal instanceof EntityCow) {
				animal.tasks.addTask(100, new EntityAIMilking(animal));
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
		if(event.getName().getResourcePath().equals("entities/cow")) {
			LootTable loot = event.getTable();
			LootUtils.removeLootFromTable(loot, Items.LEATHER);
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			LootUtils.removeLootFromTable(loot, Items.BEEF);
			LootUtils.addItemToTable(loot, Items.BEEF, 1, 2, 1, 2, 5, 0, 1, "minecraft:beef",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
						LootFunction cooked =  new Smelt(condition);
						lootfuncs.add(cooked);
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
			LootTable loot = event.getTable();
			LootUtils.removeLootFromTable(loot, Items.LEATHER);
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			LootUtils.removeLootFromTable(loot, Items.BEEF);
			LootUtils.addItemToTable(loot, Items.BEEF, 1, 2, 1, 2, 5, 0, 1, "minecraft:beef",
				new IMethod() {
					@Override
					public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
						LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
						LootFunction cooked =  new Smelt(condition);
						lootfuncs.add(cooked);
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
			LootTable loot = event.getTable();
			if(doRawLeather) {
				LootUtils.addItemToTable(loot, FarmingBase.rawLeather, 1, 2, 1, 3, 5, 0, 1, "minecraft:leather",
					new ICondition() {
						@Override
						public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
							lootconds.add(new KilledByWither(true));
						}
					});
			}
			else {
				LootUtils.addItemToTable(loot, Items.LEATHER, 1, 1, 1, 2, 4, 0, 1, "minecraft:leather",
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
					}
				},
				new ICondition() {
					@Override
					public void FunctionsCallback(ArrayList<LootCondition> lootconds) {
						lootconds.add(new KilledByWither(true));
					}
				} );
		}
		if(event.getName().getResourcePath().equals("entities/sheep")) {
			LootTable loot = event.getTable();
			
			LootUtils.removeLootFromTable(loot, Items.MUTTON);
			LootUtils.addItemToTable(loot, Items.MUTTON, 1, 1, 1, 2, 5, 0, 1, "minecraft:mutton", new IMethod() {
				@Override
				public void FunctionsCallback(ArrayList<LootFunction> lootfuncs) {
					LootCondition[] condition = {new EntityHasProperty(new EntityProperty[]{new EntityOnFire(true)}, EntityTarget.THIS)};
					LootFunction cooked =  new Smelt(condition);
					lootfuncs.add(cooked);
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
	public void getBreakSpeed(PlayerEvent.BreakSpeed event) {
		if ((event.getState().getBlock() instanceof BlockCrops || event.getState().getBlock() instanceof BlockStem) &&  event.getState().getBlock() != FarmingBase.weeds) {
			ItemStack s = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
			if (s != null && s.getItem() instanceof ItemHoe) {
				event.setNewSpeed(0);
			}
		}
	}
	
	@SubscribeEvent
	public void onPickup(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent event) {
		Item item = event.pickedUp.getEntityItem().getItem();
		if(item == FarmingBase.rawLeather) {
			event.player.addStat(FarmingAchievements.collectRawhide, 1);
		}
		if(item == FarmingBase.winterWheatSeeds) {
			event.player.addStat(FarmingAchievements.collectWinterWheat, 1);
		}
		/*if(item == Item.getItemFromBlock(WildlifeBase.rottingWood)) {
			event.player.addStat(StatsAchievements.collectCompost, 1);
		}*/
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
	}
	
	@SubscribeEvent
	public void harvestGrass(BlockEvent.HarvestDropsEvent event) {
		Block bl = event.getState().getBlock();
		if(bl instanceof BlockTallGrass) {
			Biome bio = event.getWorld().getBiomeGenForCoords(event.getPos());
			if(biomeTemps.get(bio) <= 0.3f) {
				ArrayList<ItemStack> drps = new ArrayList<ItemStack>();
				for(ItemStack is:event.getDrops()) {
					if(is.getItem() == Items.WHEAT_SEEDS) {
						drps.add(new ItemStack(FarmingBase.winterWheatSeeds, is.stackSize));
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
			Iterator<BlockPos> list = BlockPos.getAllInBox(event.getPos().south().west(), event.getPos().north().east()).iterator();
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
					if(p != null)
						p.addStat(FarmingAchievements.cropRotation, 1);
				}
			}
			else if(anyCarpet) {
				EntityPlayer p = world.getClosestPlayer(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 5, false);
				if(p != null)
					p.addStat(FarmingAchievements.weedSuppressor, 1);
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
			--stack.stackSize;
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
