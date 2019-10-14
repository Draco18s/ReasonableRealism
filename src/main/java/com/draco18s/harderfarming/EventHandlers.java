package com.draco18s.harderfarming;

import java.util.Iterator;

import com.draco18s.harderfarming.block.CropWeedsBlock;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.date.HardLibDate;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.draco18s.hardlib.util.LootUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HarderFarming.MODID)
public class EventHandlers {
	public static boolean doSlowCrops = true;
	public static boolean doBiomeCrops = true;
	public static int cropsWorst = 16;
	private static ResourceLocation grass = new ResourceLocation("minecraft", "blocks/grass");

    @SubscribeEvent
    public static void onLootLoad(LootTableLoadEvent event) {
        if (event.getName().equals(grass)) {
            event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(HarderFarming.MODID, "blocks/grass"))).build());
        }
        if(event.getName().getPath().contains("entities") && LootUtils.removeLootFromTable(event.getTable(), Items.LEATHER)) {
        	event.getTable().addPool(LootPool.builder().addEntry(TableLootEntry.builder(new ResourceLocation(HarderFarming.MODID, "entities/leather"))).build());
        }
    }

	@SubscribeEvent
	public static void onEntityAttack(LivingHurtEvent event) {
		Entity enthurt = event.getEntity();
		if(enthurt != null) {
			Entity _hurtby = event.getSource().getTrueSource();

			if(enthurt instanceof LivingEntity && _hurtby instanceof LivingEntity) {
				LivingEntity livinghurt = (LivingEntity)enthurt;
				LivingEntity hurtby = (LivingEntity)_hurtby;
				ItemStack item = hurtby.getHeldItem(Hand.MAIN_HAND);
				if(item != null && item.getItem() == HarderFarming.ModItems.butcher_knife && livinghurt instanceof AnimalEntity) {
					float amt = event.getAmount();

					ToolItem tool = (ToolItem)item.getItem();
					float base = (tool.getTier().getAttackDamage() / 2) - 1;
					//float base = (Item.ToolMaterial.valueOf(tool.getToolMaterialName()).getDamageVsEntity() / 2) - 1;
					//float base = (tool.getToolMaterial().getDamageVsEntity() / 2f)-1;
					float add = 10f/base*amt;

					event.setAmount(amt + add);
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
		if(doSlowCrops) {
			IWorld world = event.getWorld();
			BlockState state = event.getState();
			BlockPos pos = event.getPos();
			Biome bio = world.getBiome(pos);
			Result value = Result.DEFAULT;
			Iterator<IProperty<?>> props = state.getProperties().iterator();
			while(props.hasNext()) {
				IProperty<?> p = props.next();
				if(p instanceof IntegerProperty && p.getName().toLowerCase().equals("age")) {
					value = handleCrops(world, pos, state, bio, (IntegerProperty)p);
				}
			}
			event.setResult(value);
		}
	}
	
	private static Result handleCrops(IWorld world, BlockPos pos, BlockState state, Biome bio, IntegerProperty ageProp) {
		int maxAge = (Integer) ageProp.getAllowedValues().toArray()[ageProp.getAllowedValues().size()-1];
		int cropage = state.get(ageProp);
		if(cropage == maxAge) {
			return Result.DEFAULT;
		}
		int inc = 0;
		Block block = state.getBlock();
		if(block != Blocks.SUGAR_CANE) {
			Iterator<BlockPos> list = BlockPos.getAllInBox(pos.add(-1,0,-1), pos.add(1,0,1)).iterator();
			while(list.hasNext()) {
				BlockPos p = list.next();
				int ox = p.getX() - pos.getX();
				int oz = p.getZ() - pos.getZ();
				BlockState n = world.getBlockState(p);
				Block bl = n.getBlock();
				if(ox == 0 && oz == 0) continue;
				boolean ortho = (ox == 0 || oz == 0);
				if(block != HarderFarming.ModBlocks.crop_weeds && bl == HarderFarming.ModBlocks.crop_weeds && ortho) {
					//int a = n.get(CropsBlock.AGE);
					inc += 2;
					world.getPendingBlockTicks().scheduleTick(p, bl, 1);
					//world.scheduleBlockUpdate(p, bl, 1, 0);
				}
				else if(bl == block && ortho) {
					int a = n.get(ageProp);
					if(a+1 == cropage || a+2 == cropage) {
						inc += 2;
						world.getPendingBlockTicks().scheduleTick(p, bl, 1);
						//world.scheduleBlockUpdate(p, bl, 1, 0);
					}
				}
				else if(block != HarderFarming.ModBlocks.crop_weeds && bl == Blocks.AIR && world.getBlockState(p.down()).getBlock() == Blocks.FARMLAND) {
					if(HarderFarming.ModBlocks.crop_weeds != null && world.getRandom().nextInt(3500) == 0) {
						if(world.getRandom().nextInt(3) == 0) {
							world.setBlockState(p, block.getDefaultState(), 3);
						}
						else {
							if(world instanceof World)
								CropWeedsBlock.weedSpread((World)world, pos, world.getRandom());
							else
								world.setBlockState(p, HarderFarming.ModBlocks.crop_weeds.getDefaultState(), 3);
						}
					}
				}
				else if(bl == Blocks.GRASS || bl == Blocks.TALL_GRASS || bl instanceof CropsBlock) {
					inc += 1;
				}
			}
		}
		int rr = 0;
		if(doBiomeCrops) {			
			float t = getTemperature(bio,pos);
			float r = getRainfall(bio,pos);

			float seasonalTempMod = HardLibDate.getSeasonTemp(world, getTotalWorldTime(world));
			float seasonalRainMod = HardLibDate.getSeasonRain(world, getTotalWorldTime(world));
			
			if(BiomeDictionary.hasType(bio, Type.OCEAN) || BiomeDictionary.hasType(bio, Type.RIVER)) {
				seasonalTempMod *= 0.333f;
			}
			//System.out.println("bas: " + t + "," + r);
			t = HardLibDate.modifySeasonTemp(bio, seasonalTempMod);
			r = HardLibDate.modifySeasonRain(bio, seasonalRainMod);
			//System.out.println("mod: " + seasonalTempMod + "," + seasonalRainMod);
			//System.out.println("val: " + t + "," + r);
			if(BiomeDictionary.hasType(bio, Type.NETHER) != ((World)world).isRainingAt(pos)) {
				//if the crop is inside, halve the effects of climate.
				//nether is treated in reverse
				t = (t + 0.8f)/2f;
				r = (r + 1)/2f;
			}
			boolean isNetherPlant = false;
			if(block instanceof IPlantable) {
				isNetherPlant = ((IPlantable)block).getPlantType(world, pos) == PlantType.Nether;
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
					t = t - HardLibDate.getSeasonTemp(world, getTotalWorldTime(world)) + HardLibDate.getSeasonTemp(world, getTotalWorldTime(world) + offset);
				}
				t += o.temperatureFlat;
				if(o.rainfallTimeOffset != 0) {
					//if(doYearCycle)
					int offset = (int) (HardLibDate.getYearLength(world) * o.temperatureTimeOffset);
					r = r - HardLibDate.getSeasonRain(world, getTotalWorldTime(world)) + HardLibDate.getSeasonRain(world, getTotalWorldTime(world) + offset);
				}
				r += o.rainfallFlat;
				if(block == Blocks.NETHER_WART || isNetherPlant) {
					//handle alterations to the nether's temperature
					//t -= biomeTemps.get(Biomes.NETHER) - 2;
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
		if(block == HarderFarming.ModBlocks.crop_weeds) {
			//reeds always grow well
			if(rr >= 0) rr /= 2;
			else rr -= 4;
			if(rr < -9) rr = -9;
		}
		if(block == Blocks.SUGAR_CANE) {
			rr -= 3;
			if(rr < -8) rr = -8;
		}

		if(world.getRandom().nextInt(10+rr) != 0) {
			return Result.DENY;
		}
		return Result.DEFAULT;
	}

	public static long getTotalWorldTime(IWorld world) {
		return ((World)world).getGameTime();
	}

	private static float getTemperature(Biome biome, BlockPos pos) {
		return biome.getTemperature(pos);
	}

	private static float getRainfall(Biome biome, BlockPos pos) {
		return biome.getDownfall();
	}
}



