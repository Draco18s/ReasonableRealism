package com.draco18s.harderores;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.harderores.network.PacketHandler;
import com.draco18s.harderores.network.ToClientMessageOreParticles;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.interfaces.IBlockMultiBreak;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * The mining enchantments (1.12's behavior). Pulverize works in the hard ores' loot function (HarderSetCount).
 */
@EventBusSubscriber(modid = HarderOres.MODID)
public class EventHandler {
	/** Ore Breaker (shatter) mines hard ore at half speed. */
	@SubscribeEvent
	public static void breakSpeed(PlayerEvent.BreakSpeed event) {
		if(!event.getState().hasProperty(BlockProperties.ORE_DENSITY)) return;
		if(EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.shatter, event.getEntity().getMainHandItem()) > 0) {
			event.setNewSpeed(event.getNewSpeed() / 2f);
		}
	}

	@SubscribeEvent
	public static void blockBreak(BlockEvent.BreakEvent event) {
		Player harvester = event.getPlayer();
		if(harvester == null || harvester.isCreative() || !(event.getLevel() instanceof ServerLevel world)) return;
		BlockState state = event.getState();
		BlockPos pos = event.getPos();
		if(!state.hasProperty(BlockProperties.ORE_DENSITY)) {
			if(state.getMaterial() == Material.STONE) {
				prospect(world, pos, (ServerPlayer)harvester);
			}
			return;
		}
		ItemStack mainStack = harvester.getMainHandItem();
		ItemStack originalStack = mainStack.copy();
		int exp = event.getExpToDrop();
		float rollover = 0;

		// Ore Breaker: extra layers off this block. The last layer is left for the break itself, which drops loot for
		// the state from before this event (taking the block away here would make that loot a duplicate).
		int level = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.shatter, mainStack);
		for(level += level > 0 ? 1 : 0; level > 0; level--) {
			BlockState current = world.getBlockState(pos);
			if(!(current.getBlock() instanceof IBlockMultiBreak ore)) break;
			if(current.getValue(BlockProperties.ORE_DENSITY) <= ore.getDensityChangeOnBreak(world, pos, current)) break;
			rollover = mineOnce(event, world, pos, current, harvester, mainStack, originalStack, exp, rollover);
		}

		// Vein Cracker: a layer off random neighbors of the same ore (up to 12 tries).
		level = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.cracker, mainStack) * 2;
		for(int tries = 0; tries < 12 && level > 0; tries++) {
			BlockPos neighbor = pos.relative(Direction.values()[world.getRandom().nextInt(6)]);
			BlockState current = world.getBlockState(neighbor);
			if(current.getBlock() != state.getBlock()) continue;
			level--;
			rollover = mineOnce(event, world, neighbor, current, harvester, mainStack, originalStack, exp, rollover);
		}
	}

	/** Breaks one layer of a hard ore block as the player would, with its drops; the tool wears 3 times in 4. */
	private static float mineOnce(BlockEvent.BreakEvent event, ServerLevel world, BlockPos pos, BlockState state, Player harvester,
			ItemStack mainStack, ItemStack originalStack, int exp, float rollover) {
		boolean canHarvest = state.canHarvestBlock(world, pos, harvester);
		boolean removed = state.onDestroyedByPlayer(world, pos, harvester, canHarvest, world.getFluidState(pos));
		if(removed) {
			state.getBlock().destroy(world, pos, state);
		}

		rollover += 0.75f;
		if(rollover >= 0.75f) {
			mainStack.mineBlock(world, state, pos, harvester);
			if(mainStack.isEmpty() && !originalStack.isEmpty())
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(harvester, mainStack, InteractionHand.MAIN_HAND);
			rollover -= 1;
		}
		if(removed && canHarvest) {
			state.getBlock().playerDestroy(world, harvester, pos, state, world.getBlockEntity(pos), originalStack);
		}
		if(removed && exp > 0) {
			event.setExpToDrop(event.getExpToDrop() + exp);
		}
		return rollover;
	}

	/**
	 * Prospector, on a pickaxe or a compass held in the off hand: breaking stone shows the hard ore within
	 * 2*level+1 blocks (unless ore is right next to it). At level 3 each ore found has a 1 in 20 chance of turning up
	 * a little of its dust.
	 */
	private static void prospect(ServerLevel world, BlockPos pos, ServerPlayer harvester) {
		int level = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.prospector, harvester.getOffhandItem());
		if(!harvester.getMainHandItem().is(Items.COMPASS)) {
			level = Math.max(level, EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.prospector, harvester.getMainHandItem()));
		}
		if(level <= 0) return;
		for(Direction dir : Direction.values()) {
			if(world.getBlockState(pos.relative(dir)).hasProperty(BlockProperties.ORE_DENSITY)) return;
		}
		int range = level * 2 + 1;
		List<BlockPos> locations = new ArrayList<BlockPos>();
		for(BlockPos p : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range))) {
			BlockState st = world.getBlockState(p);
			if(!st.hasProperty(BlockProperties.ORE_DENSITY)) continue;
			locations.add(p.immutable());
			if(level >= 3 && world.getRandom().nextInt(20) <= level - 3) {
				dropProspectedDust(world, pos, p, st);
			}
		}
		PacketHandler.sendTo(new ToClientMessageOreParticles(PacketHandler.EffectsIDs.PROSPECTING, locations, pos), harvester);
	}

	/** One dust from the ore at orePos (which isn't mined), falling out of the stone that was broken at pos. */
	private static void dropProspectedDust(ServerLevel world, BlockPos pos, BlockPos orePos, BlockState ore) {
		List<ItemStack> drops = HardLibAPI.hardOres.getHardOreDropsOnce(world, orePos, ItemStack.EMPTY);
		if(drops == null) return;
		for(ItemStack drop : drops) {
			ItemStack dust = HardLibAPI.oreMachines.getMillResult(drop);
			if(dust.isEmpty()) continue;
			ItemEntity ent = new ItemEntity(world,
					pos.getX() + world.getRandom().nextFloat() * 0.6F + 0.2F,
					pos.getY() + world.getRandom().nextFloat() * 0.2F + 0.6F - 1,
					pos.getZ() + world.getRandom().nextFloat() * 0.6F + 0.2F,
					dust.copyWithCount(1));
			ent.setDeltaMovement(0, -0.2F, 0);
			world.addFreshEntity(ent);
		}
	}
}
