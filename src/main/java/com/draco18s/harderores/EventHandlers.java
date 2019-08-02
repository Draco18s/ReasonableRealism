package com.draco18s.harderores;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.draco18s.harderores.block.ore.HardOreBlock;
import com.draco18s.harderores.network.PacketHandler;
import com.draco18s.harderores.network.ToClientMessageOreParticles;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = HarderOres.MODID)
public class EventHandlers {
	@SubscribeEvent
	public static void harvest(BlockEvent.BreakEvent event) {
		BlockState state = event.getState();
		if(event.getPlayer() != null && !event.getWorld().isRemote()) {
			PlayerEntity harvester = event.getPlayer();
			IWorld iworld = event.getWorld();
			BlockPos pos = event.getPos();
			ItemStack mainStack = harvester.getHeldItemMainhand();
			if(state.getProperties().contains(BlockProperties.ORE_DENSITY)) {
				int level = EnchantmentHelper.getEnchantmentLevel(HarderOres.ModEnchantments.shatter, harvester.getHeldItemMainhand());
				if(level > 0) {
					float rollover = 0;
					for(level += 1;level > 0;level--) {
						//int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, mainStack);
						List<ItemStack> drps = HardLibAPI.hardOres.mineHardOreOnce(iworld.getWorld(), (ServerPlayerEntity) harvester, pos, mainStack);
						if(drps != null) {
							rollover += 0.75f;
							if(rollover >= 0.75f) {
								mainStack.attemptDamageItem(1, iworld.getRandom(), (ServerPlayerEntity) harvester);
								rollover -= 1;
							}
							//event.getDrops().addAll(drps);
							for(ItemStack s : drps) {
								dropStack(iworld, pos, s);
							}
						}
					}
				}
				level = EnchantmentHelper.getEnchantmentLevel(HarderOres.ModEnchantments.cracker, harvester.getHeldItemMainhand());
				int max = 0;
				float rollover = 0;
				for(level *= 2;max < 12 && level > 0;max++) {
					Direction dir = Direction.values()[iworld.getRandom().nextInt(6)];
					if(state.getBlock() == iworld.getBlockState(pos.offset(dir, 1)).getBlock()) {
						level--;
						List<ItemStack> drps = HardLibAPI.hardOres.mineHardOreOnce(iworld.getWorld(), (ServerPlayerEntity) harvester, pos.offset(dir, 1), mainStack);
						if(drps != null) {
							rollover += 0.75f;
							if(rollover >= 0.75f) {
								mainStack.attemptDamageItem(1, iworld.getRandom(), (ServerPlayerEntity) harvester);
								rollover -= 1;
							}
							//event.getDrops().addAll(drps);
							for(ItemStack s : drps) {
								dropStack(iworld, pos, s);
							}
						}
					}
				}
				level = EnchantmentHelper.getEnchantmentLevel(HarderOres.ModEnchantments.pulverize, harvester.getHeldItemMainhand());

				/*if(level > 0 && state.get(BlockProperties.ORE_DENSITY) <= level*2+3) {
					Iterator<ItemStack> it = event.getDrops().iterator();
					ArrayList<ItemStack> newItems = new ArrayList<ItemStack>();
					max = 2+level;
					while(it.hasNext()) {
						ItemStack stk = it.next();
						ItemStack out = HardLibAPI.oreMachines.getMillResult(stk);
						if(!out.isEmpty()) {
							int s = Math.min(stk.getCount(),max);
							ItemStack dustStack = out.copy();
							int n = dustStack.getCount();
							dustStack.setCount(0);
							for(; s > 0; s--) {
								dustStack.grow(n);
								max--;
								stk.shrink(1);
							}
							newItems.add(dustStack);
							if(stk.getCount() == 0) {
								it.remove();
							}
						}
					}
					event.getDrops().addAll(newItems);
				}*/
			}
			if(state.getMaterial() == Material.ROCK) {
				int level = EnchantmentHelper.getEnchantmentLevel(HarderOres.ModEnchantments.prospector, harvester.getHeldItemOffhand());
				if(mainStack != null && mainStack.getItem() != Items.COMPASS)
					level = Math.max(level, EnchantmentHelper.getEnchantmentLevel(HarderOres.ModEnchantments.prospector, mainStack)); 
				if(level > 0) {
					int llevel = level*2+1;
					boolean anyOre = state.getProperties().contains(BlockProperties.ORE_DENSITY);
					for(Direction dir : Direction.values()) {
						if(iworld.getBlockState(pos.offset(dir)).getProperties().contains(BlockProperties.ORE_DENSITY)) {
							anyOre = true;
						}
					}
					if(!anyOre) {
						Iterable<BlockPos> cube = BlockPos.getAllInBox(pos.add(-llevel, -llevel, -llevel), pos.add(llevel, llevel, llevel)).map(BlockPos::toImmutable).collect(Collectors.toList());;
						List<BlockPos> locations = new ArrayList<BlockPos>();
						for(BlockPos p : cube) {
							BlockState st = iworld.getBlockState(p);
							if(HardLibAPI.hardOres.isHardOre(st)) {
								locations.add(p);
								if(level >= 3) {
									if(iworld.getRandom().nextInt(20) <= (level-3)) {
										List<ItemStack> list = HardLibAPI.hardOres.getHardOreDropsOnce(iworld.getWorld(), p, ItemStack.EMPTY);
										ArrayList<ItemStack> toDrop = new ArrayList<ItemStack>();
										for(ItemStack s:list) {
											ItemStack milled = HardLibAPI.oreMachines.getMillResult(s);
											if(milled != null)
												toDrop.add(milled.copy());
										}
										for(ItemStack s:toDrop) {
											s.setCount(1);
											float rx = iworld.getRandom().nextFloat() * 0.6F + 0.2F;
											float ry = iworld.getRandom().nextFloat() * 0.2F + 0.6F - 1;
											float rz = iworld.getRandom().nextFloat() * 0.6F + 0.2F;
											ItemEntity ent = new ItemEntity(iworld.getWorld(), pos.getX()+rx, pos.getY()+ry, pos.getZ()+rz, s);
											iworld.addEntity(ent);
											ent.setMotion(0, -0.2F, 0);
										}
									}
								}
							}
						}
						ToClientMessageOreParticles packet = new ToClientMessageOreParticles(PacketHandler.EffectsIDs.PROSPECTING, locations, pos);
						PacketHandler.sendTo(packet, (ServerPlayerEntity)harvester);
					}
				}
			}
		}
	}
	
	private static void dropStack(IWorld worldIn, BlockPos pos, ItemStack stack) {
		float f = 0.7F;
		double d0 = (double)(worldIn.getRandom().nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		double d1 = (double)(worldIn.getRandom().nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		double d2 = (double)(worldIn.getRandom().nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		for(Direction dir : HardOreBlock.DROP_SEARCH_DIRECTIONS) {
			if(!worldIn.getBlockState(pos.offset(dir)).isSolid() || dir == Direction.DOWN) {
				ItemEntity entityitem = new ItemEntity(worldIn.getWorld(), (double)pos.getX() + d0+dir.getXOffset(), (double)pos.getY() + d1+dir.getYOffset(), (double)pos.getZ() + d2+dir.getZOffset(), stack);
				entityitem.setDefaultPickupDelay();
				worldIn.addEntity(entityitem);
				return;
			}
		}
	}
}