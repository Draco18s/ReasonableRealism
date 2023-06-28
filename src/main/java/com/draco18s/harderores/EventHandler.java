package com.draco18s.harderores;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.harderores.network.PacketHandler;
import com.draco18s.harderores.network.ToClientMessageOreParticles;
import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = HarderOres.MODID)
public class EventHandler {
	@SubscribeEvent
	public static void blockBreak(BlockEvent.BreakEvent event) {
		if(event.getPlayer() == null || event.getLevel().isClientSide()) return;
		BlockState state = event.getState();
		Player harvester = event.getPlayer();
		LevelAccessor iworld = event.getLevel();
		BlockPos pos = event.getPos();
		ItemStack mainStack = harvester.getMainHandItem();
		ItemStack originalStack = mainStack.copy();
		int level = 0;
		if(!state.getProperties().contains(BlockProperties.ORE_DENSITY)) {
			if(state.getMaterial() == Material.STONE) {
				level = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.prospector, mainStack);
				if(level <= 0) return;
				
				int llevel = level*2+1;
				boolean anyOre = state.getProperties().contains(BlockProperties.ORE_DENSITY);
				for(Direction dir : Direction.values()) {
					if(iworld.getBlockState(pos.relative(dir)).getProperties().contains(BlockProperties.ORE_DENSITY)) {
						anyOre = true;
					}
				}
				if(anyOre) return;
				Iterable<BlockPos> cube = BlockPos.betweenClosed(pos.offset(-llevel, -llevel, -llevel), pos.offset(llevel, llevel, llevel));//.map(BlockPos::toImmutable).collect(Collectors.toList());;
				
				List<BlockPos> locations = new ArrayList<BlockPos>();
				for(BlockPos p : cube) {
					BlockState st = iworld.getBlockState(p);
					if(!st.getProperties().contains(BlockProperties.ORE_DENSITY)) continue;
					locations.add(p.immutable());
					//if(level < 3) continue;
					//if(iworld.getRandom().nextInt(20) > (level-3)) continue;
					// todo: extra drops
				}
				ToClientMessageOreParticles packet = new ToClientMessageOreParticles(PacketHandler.EffectsIDs.PROSPECTING, locations, pos);
				PacketHandler.sendTo(packet, (ServerPlayer)harvester);
			}
			return;
		}
		ServerLevel world = (ServerLevel)iworld;
		int exp = event.getExpToDrop();
		level = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.shatter, mainStack);
		if(level > 0) {
			float rollover = 0;
			for(level += 1;level > 0;level--) {
				rollover = mineBlock(event, state, harvester, world, pos, mainStack, originalStack, exp, rollover);
			}
		}
		level = EnchantmentHelper.getTagEnchantmentLevel(HarderOres.ModEnchantments.cracker, mainStack);
		int max = 0;
		float rollover = 0;
		for(level *= 2;max < 12 && level > 0;max++) {
			Direction dir = Direction.values()[iworld.getRandom().nextInt(6)];
			if(state.getBlock() != iworld.getBlockState(pos.relative(dir)).getBlock()) continue;
			level--;
			rollover = mineBlock(event, state, harvester, world, pos, mainStack, originalStack, exp, rollover);
		}
	}

	private static float mineBlock(BlockEvent.BreakEvent event, BlockState state, Player harvester, ServerLevel world, BlockPos pos, ItemStack mainStack, ItemStack originalStack,
			int exp, float rollover) {
		boolean flag1 = state.canHarvestBlock(world, pos, harvester);
		boolean flag = removeBlock(world, pos, flag1, harvester);

		rollover += 0.75f;
		if(rollover >= 0.75f) {
			mainStack.mineBlock(world, state, pos, harvester);
			if (mainStack.isEmpty() && !originalStack.isEmpty())
				net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(harvester, mainStack, InteractionHand.MAIN_HAND);
			rollover -= 1;
		}
		if (flag && flag1) {
			state.getBlock().playerDestroy(world, harvester, pos, state, world.getBlockEntity(pos), originalStack);
		}

		if (flag && exp > 0) {
			event.setExpToDrop(event.getExpToDrop() + exp);
		}
		return rollover;
	}

	private static boolean removeBlock(Level level, BlockPos pos, boolean canHarvest, Player player) {
		BlockState state = level.getBlockState(pos);
		boolean removed = state.onDestroyedByPlayer(level, pos, player, canHarvest, level.getFluidState(pos));
		if (removed)
			state.getBlock().destroy(level, pos, state);
		return removed;
	}
}
