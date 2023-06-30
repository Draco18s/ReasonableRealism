package com.draco18s.farming;

import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = HarderFarming.MODID)
public class EventHandler {
	public static boolean doSlowCrops = true;
	
	@SubscribeEvent
	public static void onEntityAttack(LivingHurtEvent event) {
		LivingEntity enthurt = event.getEntity();
		if(enthurt == null) return;
		if(enthurt instanceof Animal animal && event.getSource().getEntity() instanceof LivingEntity hurtby) {
			ItemStack item = hurtby.getMainHandItem();
			if(item.getItem() != HarderFarming.ModItems.butcher_knife) return;
			
			float amt = event.getAmount();
			DiggerItem tool = (DiggerItem)item.getItem();
			float base = tool.getAttackDamage();
			float add = 10f*amt/base; //add 10 damage scaled by damage being dealt

			event.setAmount(amt + add);
		}
	}
	
	@SubscribeEvent
	public static void onGropPre(BlockEvent.CropGrowEvent.Pre event) {
		if(doSlowCrops) {
			LevelAccessor world = event.getLevel();
			BlockState state = event.getState();
			BlockPos pos = event.getPos();
			Biome bio = world.getBiome(pos).get();
			Event.Result value = Event.Result.DEFAULT;
			if(state.getBlock() instanceof CropBlock crop) {
				IntegerProperty iprop = crop.getAgeProperty();
				value = handleCrops(world, pos, state, bio, iprop, crop.getMaxAge());
			}
			event.setResult(value);
		}
	}

	private static Result handleCrops(LevelAccessor world, BlockPos pos, BlockState state, Biome bio, IntegerProperty ageProp, int maxAge) {
		int cropage = state.getValue(ageProp);
		if(cropage >= maxAge) {
			return Result.DEFAULT;
		}
		Block block = state.getBlock();
		int inc = 0;
		Iterator<BlockPos> list = BlockPos.betweenClosed(pos.offset(-1,0,-1), pos.offset(1,0,1)).iterator();
		while(list.hasNext()) {
			BlockPos p = list.next();
			int ox = p.getX() - pos.getX();
			int oz = p.getZ() - pos.getZ();
			BlockState n = world.getBlockState(p);
			Block bl = n.getBlock();
			if(ox == 0 && oz == 0) continue;
			boolean ortho = (ox == 0 || oz == 0);
			if(bl == block && ortho) {
				int a = n.getValue(ageProp);
				if(a+1 == cropage || a+2 == cropage) {
					inc += 2;
					world.scheduleTick(p, bl, 0);
				}
			}
			else if(bl == Blocks.GRASS || bl == Blocks.TALL_GRASS || bl instanceof CropBlock) {
				inc += 1;
			}
		}
		int rr = 0;
		rr += 2*inc;
		if(world.getRandom().nextInt(10+rr) != 0) {
			return Result.DENY;
		}
		return Result.DEFAULT;
	}
}
