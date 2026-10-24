package com.draco18s.farming;

import com.draco18s.farming.block.CropWeedsBlock;
import com.draco18s.farming.item.ButcherKnife;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = HarderFarming.MODID)
public class EventHandler {

	@SubscribeEvent
	public static void onEntityAttack(LivingHurtEvent event) {
		LivingEntity enthurt = event.getEntity();
		if(enthurt == null) return;
		if(enthurt instanceof Animal animal && event.getSource().getEntity() instanceof LivingEntity hurtby) {
			ItemStack item = hurtby.getMainHandItem();
			if(item.getItem() != HarderFarming.ModItems.butcher_knife) return;

			float amt = event.getAmount();
			ButcherKnife tool = (ButcherKnife)item.getItem();
			float base = tool.getAttackDamage();
			float add = 10f*amt/base; //add 10 damage scaled by damage being dealt

			event.setAmount(amt + add);
		}
	}

	/**
	 * Slows every plant with an age property (crops, stems, sugar cane, cocoa, nether wart, berry bushes...).
	 * Growth is helped by the right neighbors and by a climate that suits the plant.
	 */
	/**
	 * Crop Rotation: harvesting a crop whose row (the same crop on both sides one way, not the other) runs between
	 * other crops, with no bare farmland or weeds around it. (1.11 granted it to the nearest player; the break event
	 * knows who harvested.)
	 */
	@SubscribeEvent
	public static void onCropHarvest(BlockEvent.BreakEvent event) {
		if(!(event.getPlayer() instanceof ServerPlayer player) || HarderFarming.ModTriggers.crop_rotation == null) return;
		Block crop = event.getState().getBlock();
		if(!(crop instanceof CropBlock) || crop instanceof CropWeedsBlock) return;
		LevelAccessor world = event.getLevel();
		BlockPos pos = event.getPos();
		for(BlockPos p : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
			BlockState state = world.getBlockState(p);
			if((state.getBlock() instanceof CropWeedsBlock || state.isAir()) && world.getBlockState(p.below()).is(Blocks.FARMLAND)) return;
		}
		boolean rowNS = world.getBlockState(pos.north()).is(crop) && world.getBlockState(pos.south()).is(crop);
		boolean rowEW = world.getBlockState(pos.east()).is(crop) && world.getBlockState(pos.west()).is(crop);
		if(rowNS ^ rowEW) {
			HarderFarming.ModTriggers.crop_rotation.trigger(player);
		}
	}

	@SubscribeEvent
	public static void onCropGrowPre(BlockEvent.CropGrowEvent.Pre event) {
		if(!FarmingConfig.doSlowCrops.get()) return;
		LevelAccessor world = event.getLevel();
		BlockState state = event.getState();
		IntegerProperty ageProp = getAgeProperty(state);
		if(ageProp != null) {
			event.setResult(handleCrops(world, event.getPos(), state, world.getBiome(event.getPos()).value(), ageProp));
		}
	}

	private static IntegerProperty getAgeProperty(BlockState state) {
		for(Property<?> p : state.getProperties()) {
			if(p instanceof IntegerProperty ip && p.getName().equals("age")) {
				return ip;
			}
		}
		// A crop's own age property (CropBlock.getAgeProperty is protected now): the one that ends at its max age.
		if(state.getBlock() instanceof CropBlock crop) {
			for(Property<?> p : state.getProperties()) {
				if(p instanceof IntegerProperty ip && ip.getPossibleValues().contains(crop.getMaxAge())
						&& ip.getPossibleValues().stream().allMatch(v -> v <= crop.getMaxAge())) {
					return ip;
				}
			}
		}
		return null;
	}

	private static Result handleCrops(LevelAccessor world, BlockPos pos, BlockState state, Biome bio, IntegerProperty ageProp) {
		int maxAge = ageProp.getPossibleValues().stream().max(Integer::compare).orElse(0);
		int cropage = state.getValue(ageProp);
		if(cropage >= maxAge) {
			return Result.DEFAULT;
		}
		int inc = 0;
		Block block = state.getBlock();
		Block weeds = HarderFarming.ModBlocks.crop_weeds;
		if(block != Blocks.SUGAR_CANE) {
			for(BlockPos p : BlockPos.betweenClosed(pos.offset(-1,0,-1), pos.offset(1,0,1))) {
				int ox = p.getX() - pos.getX();
				int oz = p.getZ() - pos.getZ();
				if(ox == 0 && oz == 0) continue;
				BlockState n = world.getBlockState(p);
				Block bl = n.getBlock();
				boolean ortho = (ox == 0 || oz == 0);
				if(block != weeds && bl == weeds && ortho) {
					// Weeds next to a crop crowd it out, and grow along with it.
					inc += 2;
					world.scheduleTick(p.immutable(), bl, 1);
				}
				else if(bl == block && ortho) {
					// Rows of the same crop a stage or two apart (planted in turn) grow better together.
					int a = n.getValue(ageProp);
					if(a+1 == cropage || a+2 == cropage) {
						inc += 2;
						world.scheduleTick(p.immutable(), bl, 1);
					}
				}
				else if(block != weeds && n.isAir() && world.getBlockState(p.below()).is(Blocks.FARMLAND)) {
					// Bare farmland beside a crop sometimes sprouts: usually weeds, sometimes the crop itself.
					if(world.getRandom().nextInt(3500) == 0) {
						if(world.getRandom().nextInt(3) == 0) {
							world.setBlock(p, block.defaultBlockState(), 3);
						}
						else if(world instanceof Level level) {
							CropWeedsBlock.weedSpread(level, pos, world.getRandom());
						}
						else {
							world.setBlock(p, weeds.defaultBlockState(), 3);
						}
					}
				}
				else if(bl == Blocks.SHORT_GRASS || bl == Blocks.TALL_GRASS || bl instanceof CropBlock) {
					inc += 1;
				}
			}
		}
		int rr = 0;
		if(FarmingConfig.doBiomeCrops.get()) {
			rr = climatePenalty(world, pos, bio, block);
		}
		rr += 2*inc;
		if(block == weeds) {
			// weeds always grow well
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

	/**
	 * How much the biome's temperature and rainfall (shifted by the plant's preferences, see
	 * HarderFarming.registerCropData) slow it down: -8 (ideal) to cropsWorst. A plant without a view of the sky
	 * feels the climate half as much. (1.12 also varied these with the season; that's not ported.)
	 */
	private static int climatePenalty(LevelAccessor world, BlockPos pos, Biome bio, Block block) {
		float t = bio.getModifiedClimateSettings().temperature();
		float r = bio.getModifiedClimateSettings().downfall();
		if(!world.canSeeSky(pos.above())) {
			t = (t + 0.8f)/2f;
			r = (r + 1)/2f;
		}
		if(block instanceof IPlantable plantable) {
			Block plant = plantable.getPlant(world, pos).getBlock();
			if(plant != Blocks.AIR) {
				block = plant;
			}
		}
		CropWeatherOffsets o = HardLibAPI.hardCrops.getCropOffsets(block);
		if(o != null) {
			t += o.temperatureFlat;
			r += o.rainfallFlat;
		}
		//simplifies the following equation
		t -= 0.4f;
		r -= 1.75;

		int rr = (int) Math.round((Math.pow(t-1.5f,4)*0.6f)+(t*t*2)-((r*r*-2.2)-0.3f*Math.pow(r+2f,4)+7));
		if(rr < 0) rr--;
		if(rr < -8) rr = -8;
		if(rr > FarmingConfig.cropsWorst.get()) rr = FarmingConfig.cropsWorst.get();
		return rr;
	}
}
