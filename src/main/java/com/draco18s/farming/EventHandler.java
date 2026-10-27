package com.draco18s.farming;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.draco18s.farming.block.CropWeedsBlock;
import com.draco18s.farming.block.MulchedFarmlandBlock;
import com.draco18s.farming.block.WeedyFarmlandBlock;
import com.draco18s.farming.item.ButcherKnife;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.advancement.BreakBlockTrigger;
import com.draco18s.hardlib.api.blockproperties.farming.Mulch;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;

@EventBusSubscriber(modid = HarderFarming.MODID)
public class EventHandler {

	@SubscribeEvent
	public static void onEntityAttack(LivingIncomingDamageEvent event) {
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
	public static void onCropHarvest(BreakBlockEvent event) {
		if(!(event.getPlayer() instanceof ServerPlayer player) || HarderFarming.ModTriggers.crop_rotation == null) return;
		Block crop = event.getState().getBlock();
		if(!(crop instanceof CropBlock) || crop instanceof CropWeedsBlock) return;
		LevelAccessor world = event.getLevel();
		BlockPos pos = event.getPos();
		for(BlockPos p : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
			BlockState state = world.getBlockState(p);
			if((state.getBlock() instanceof CropWeedsBlock || state.isAir()) && isBareFarmland(world.getBlockState(p.below()))) return;
			if(world.getBlockState(p.below()).getBlock() instanceof WeedyFarmlandBlock) return;
		}
		boolean rowNS = world.getBlockState(pos.north()).is(crop) && world.getBlockState(pos.south()).is(crop);
		boolean rowEW = world.getBlockState(pos.east()).is(crop) && world.getBlockState(pos.west()).is(crop);
		if(rowNS ^ rowEW) {
			HarderFarming.ModTriggers.crop_rotation.trigger(player);
		}
	}

	/**
	 * Where each player last pulled weeds, and when: holding the button down mustn't go on to break the crop. One
	 * map per side, since a single player game runs both in one process (and each side sees the same click).
	 */
	private static final Map<UUID, Pair<BlockPos, Long>> lastWeededClient = new HashMap<UUID, Pair<BlockPos, Long>>();
	private static final Map<UUID, Pair<BlockPos, Long>> lastWeededServer = new HashMap<UUID, Pair<BlockPos, Long>>();

	/**
	 * A hoe pulls weeds out from among a crop (or from bare farmland) without disturbing it. Runs on both sides:
	 * the client must not predict the crop breaking, and the server does the weeding.
	 */
	@SubscribeEvent
	public static void onHoeWeeds(PlayerInteractEvent.LeftClickBlock event) {
		if(event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) return;
		Player player = event.getEntity();
		ItemStack hoe = player.getMainHandItem();
		if(!hoe.is(ItemTags.HOES)) return;
		Level world = event.getLevel();
		BlockPos pos = event.getPos();
		BlockPos farmland = world.getBlockState(pos).getBlock() instanceof WeedyFarmlandBlock ? pos : pos.below();
		BlockState soil = world.getBlockState(farmland);
		long now = world.getGameTime();
		Map<UUID, Pair<BlockPos, Long>> lastWeeded = world.isClientSide() ? lastWeededClient : lastWeededServer;
		Pair<BlockPos, Long> last = lastWeeded.get(player.getUUID());
		if(last != null && last.getFirst().equals(pos) && now - last.getSecond() < 10) {
			// Just weeded here: still swinging.
			event.setCanceled(true);
			return;
		}
		if(!(soil.getBlock() instanceof WeedyFarmlandBlock)) return;
		event.setCanceled(true);
		lastWeeded.put(player.getUUID(), Pair.of(pos.immutable(), now));
		if(world instanceof ServerLevel level && player instanceof ServerPlayer splayer) {
			WeedyFarmlandBlock.clearWeeds(level, farmland, soil);
			level.playSound(null, farmland.above(), SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1, 1);
			level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, farmland.above(), Block.getId(HarderFarming.ModBlocks.crop_weeds.defaultBlockState()));
			if(!player.isCreative()) {
				hoe.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
			}
			// Gardening: pulling weeds counts as breaking the old weeds block.
			if(HardLibAPI.Advancements.BLOCK_BREAK != null) {
				LootParams ctx = new LootParams.Builder(level)
						.withParameter(LootContextParams.BLOCK_STATE, HarderFarming.ModBlocks.crop_weeds.defaultBlockState())
						.create(BreakBlockTrigger.requiredParams);
				HardLibAPI.Advancements.BLOCK_BREAK.trigger(splayer, ctx);
			}
		}
	}

	/**
	 * Mulching: a carpet (or leaf litter, under a crop) used on farmland, or on the crop growing in it, is laid at
	 * the crop's base; weeds are smothered. Shears take a carpet back off. Runs on both sides so the client doesn't
	 * place the carpet as a block; the server does the work.
	 */
	@SubscribeEvent
	public static void onMulch(PlayerInteractEvent.RightClickBlock event) {
		Level world = event.getLevel();
		BlockPos pos = event.getPos();
		ItemStack stack = event.getItemStack();
		Player player = event.getEntity();
		BlockState clicked = world.getBlockState(pos);
		BlockPos farmland = clicked.getBlock() instanceof FarmlandBlock ? pos : pos.below();
		BlockState soil = world.getBlockState(farmland);
		if(!(soil.getBlock() instanceof FarmlandBlock)) return;
		Mulch mulch = Mulch.fromItem(stack);
		if(mulch != null && (soil.is(Blocks.FARMLAND) || soil.getBlock() instanceof WeedyFarmlandBlock)) {
			// Leaves only go down among a crop: with nothing growing they'd be gone at once.
			if(mulch == Mulch.LEAVES && world.getBlockState(farmland.above()).isAir()) return;
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
			if(!world.isClientSide()) {
				MulchedFarmlandBlock.mulch(world, farmland, mulch);
				SoundType sound = Block.byItem(mulch.item()).defaultBlockState().getSoundType();
				world.playSound(null, farmland.above(), sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1) / 2, sound.getPitch() * 0.8f);
				stack.consume(1, player);
			}
		}
		else if(stack.is(Tags.Items.TOOLS_SHEAR) && soil.getBlock() instanceof MulchedFarmlandBlock && soil.getValue(MulchedFarmlandBlock.MULCH).retrievable()) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
			if(!world.isClientSide()) {
				Block.popResource(world, farmland.above(), MulchedFarmlandBlock.retrieve(soil));
				world.setBlock(farmland, MulchedFarmlandBlock.unmulched(soil), 3);
				world.playSound(null, farmland.above(), SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1, 1);
				stack.hurtAndBreak(1, player, event.getHand().asEquipmentSlot());
			}
		}
	}

	@SubscribeEvent
	public static void onCropGrowPre(CropGrowEvent.Pre event) {
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

	/** Open farmland, for the crop-growth rules: plain or mulched (mulch only keeps weeds out). */
	private static boolean isBareFarmland(BlockState soil) {
		return soil.is(Blocks.FARMLAND) || soil.getBlock() instanceof MulchedFarmlandBlock;
	}

	private static CropGrowEvent.Pre.Result handleCrops(LevelAccessor world, BlockPos pos, BlockState state, Biome bio, IntegerProperty ageProp) {
		int maxAge = ageProp.getPossibleValues().stream().max(Integer::compare).orElse(0);
		int cropage = state.getValue(ageProp);
		if(cropage >= maxAge) {
			return CropGrowEvent.Pre.Result.DEFAULT;
		}
		BlockState soil = world.getBlockState(pos.below());
		if(soil.getBlock() instanceof WeedyFarmlandBlock && world instanceof Level level) {
			// Weeds among the crop take its growth for themselves.
			WeedyFarmlandBlock.growWeeds(level, pos.below(), soil, world.getRandom());
			return CropGrowEvent.Pre.Result.DO_NOT_GROW;
		}
		int inc = 0;
		Block block = state.getBlock();
		Block weeds = HarderFarming.ModBlocks.crop_weeds;
		List<BlockPos> younger = new ArrayList<BlockPos>();
		if(block != Blocks.SUGAR_CANE) {
			for(BlockPos p : BlockPos.betweenClosed(pos.offset(-1,0,-1), pos.offset(1,0,1))) {
				int ox = p.getX() - pos.getX();
				int oz = p.getZ() - pos.getZ();
				if(ox == 0 && oz == 0) continue;
				BlockState n = world.getBlockState(p);
				Block bl = n.getBlock();
				boolean ortho = (ox == 0 || oz == 0);
				if(block != weeds && (bl == weeds || world.getBlockState(p.below()).getBlock() instanceof WeedyFarmlandBlock) && ortho) {
					// Weeds next to a crop crowd it out.
					inc += 2;
				}
				else if(bl == block && ortho) {
					// Rows of the same crop keep pace: a plant a stage or two ahead of its neighbor hands its
					// growth over (it's slowed, and the neighbor gets a growth tick of its own).
					int a = n.getValue(ageProp);
					if(a+1 == cropage || a+2 == cropage) {
						inc += 2;
						younger.add(p.immutable());
					}
				}
				else if(block != weeds && n.isAir() && isBareFarmland(world.getBlockState(p.below()))) {
					// Bare farmland beside a crop sometimes sprouts: usually weeds (not through mulch), sometimes the crop itself.
					if(world.getRandom().nextInt(3500) == 0) {
						if(world.getRandom().nextInt(3) == 0) {
							world.setBlock(p, block.defaultBlockState(), 3);
						}
						else if(world instanceof Level level && world.getBlockState(p.below()).is(Blocks.FARMLAND)) {
							WeedyFarmlandBlock.plantWeeds(level, p.below());
						}
					}
				}
				else if(bl == Blocks.SHORT_GRASS || bl == Blocks.TALL_GRASS || bl instanceof CropBlock) {
					inc += 1;
				}
			}
		}
		// (Crops only grow on random ticks now: a scheduled tick, as 1.14 gave the neighbor, does nothing.)
		if(world instanceof ServerLevel level) {
			for(BlockPos p : younger) {
				BlockState n = level.getBlockState(p);
				if(n.is(block)) n.randomTick(level, p, level.getRandom());
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
			return CropGrowEvent.Pre.Result.DO_NOT_GROW;
		}
		return CropGrowEvent.Pre.Result.DEFAULT;
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
