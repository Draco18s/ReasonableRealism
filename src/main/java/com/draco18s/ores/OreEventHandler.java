package com.draco18s.ores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.ores.block.ore.BlockHardOreBase;
import com.draco18s.ores.networking.Packets;
import com.draco18s.ores.networking.ToClientMessageOreParticles;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public class OreEventHandler {

	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();
		boolean hardOption = OresBase.config.getBoolean("RequireMillingFlour", "MILLING", false, "");
		if(hardOption) {
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:bread"), "Hard Ores");
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:cookie"), "Hard Ores");
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:cake"), "Hard Ores");
		}
		boolean stoneTools = OresBase.config.getBoolean("useDioriteStoneTools", "GENERAL", true, "If true, cobblestone cannot be used to create stone tools,\ninstead diorite is used. This prolongs the life of wood tools so it isn't \"make a wood pickaxe to\nmine 3 stone and upgrade.\"");
		if(stoneTools) {
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:stone_axe"), "Harder Ores");
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:stone_pickaxe"), "Harder Ores");
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:stone_shovel"), "Harder Ores");
			RecipesUtils.RemoveRecipe(modRegistry, new ResourceLocation("minecraft:stone_hoe"), "Harder Ores");
		}
	}
	
	@SubscribeEvent
	public void breakSpeed(BreakSpeed event) {
		if(event.getEntityPlayer() != null) {
			EntityPlayer harvester = event.getEntityPlayer();
			IBlockState state = event.getState();
			//World world = harvester.worldObj;
			BlockPos pos = event.getPos();
			if(state.getProperties().containsKey(Props.ORE_DENSITY)) {
				int level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchShatter, harvester.getHeldItemMainhand());
				if(level > 0) {
					event.setNewSpeed(event.getNewSpeed() / 2f);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void harvest(HarvestDropsEvent event) {
		if(event.getHarvester() != null) {
			EntityPlayer harvester = event.getHarvester();
			IBlockState state = event.getState();
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			if(state.getProperties().containsKey(Props.ORE_DENSITY)) {
				int level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchShatter, harvester.getHeldItemMainhand());
				if(level > 0) {
					float rollover = 0;
					for(level += 1;level > 0;level--) {
						int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, harvester.getHeldItemMainhand());
						List<ItemStack> drps = HardLibAPI.hardOres.mineHardOreOnce(world, pos, fortune);
						if(drps != null) {
							rollover += 0.75f;
							if(rollover >= 0.75f) {
								harvester.getHeldItemMainhand().damageItem(1, harvester);
								rollover -= 1;
							}
							event.getDrops().addAll(drps);
						}
					}
				}
			}
			if(state.getProperties().containsKey(Props.ORE_DENSITY)) {
				int level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchCracker, harvester.getHeldItemMainhand());
				int max = 0;
				float rollover = 0;
				for(level *= 2;max < 12 && level > 0;max++) {
					EnumFacing dir = EnumFacing.VALUES[world.rand.nextInt(6)];
					if(state.getBlock() == world.getBlockState(pos.offset(dir, 1)).getBlock()) {
						level--;
						List<ItemStack> drps = HardLibAPI.hardOres.mineHardOreOnce(world, pos.offset(dir, 1), 0);
						if(drps != null) {
							rollover += 0.75f;
							if(rollover >= 0.75f) {
								harvester.getHeldItemMainhand().damageItem(1, harvester);
								rollover -= 1;
							}
							event.getDrops().addAll(drps);
						}
					}
				}
				level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchPulverize, harvester.getHeldItemMainhand());

				if(level > 0 && state.getValue(Props.ORE_DENSITY) <= level*2+3) {
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
				}
			}

			if(state.getMaterial() == Material.ROCK) {//state.getMaterial() == Material.ROCK?
				int level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchProspector, harvester.getHeldItemOffhand());
				if(harvester.getHeldItemMainhand() != null && harvester.getHeldItemMainhand().getItem() != Items.COMPASS)
					level = Math.max(level, EnchantmentHelper.getEnchantmentLevel(OresBase.enchProspector, harvester.getHeldItemMainhand())); 
				if(level > 0) {
					int llevel = level*2+1;
					boolean anyOre = state.getProperties().containsKey(Props.ORE_DENSITY);
					for(EnumFacing dir : EnumFacing.VALUES) {
						if(world.getBlockState(pos).getProperties().containsKey(Props.ORE_DENSITY)) {
							anyOre = true;
						}
					}
					if(!anyOre) {
						Iterable<BlockPos> cube = pos.getAllInBox(pos.add(-llevel, -llevel, -llevel), pos.add(llevel, llevel, llevel));
						for(BlockPos p : cube) {
							IBlockState st = world.getBlockState(p);
							if(HardLibAPI.hardOres.isHardOre(st)) {
								ToClientMessageOreParticles packet = new ToClientMessageOreParticles(Packets.PROSPECTING, pos, p);
								OresBase.networkWrapper.sendTo(packet, (EntityPlayerMP) event.getHarvester());
								if(level >= 3) {
									if(world.rand.nextInt(20) <= (level-3)) {
										List<ItemStack> list = HardLibAPI.hardOres.getHardOreDropsOnce(world, p, 0);
										ArrayList<ItemStack> toDrop = new ArrayList();
										for(ItemStack s:list) {
											ItemStack milled = HardLibAPI.oreMachines.getMillResult(s);
											if(milled != null)
												toDrop.add(milled.copy());
										}
										for(ItemStack s:toDrop) {
											s.setCount(1);
											float rx = world.rand.nextFloat() * 0.6F + 0.2F;
											float ry = world.rand.nextFloat() * 0.2F + 0.6F - 1;
											float rz = world.rand.nextFloat() * 0.6F + 0.2F;
											EntityItem ent = new EntityItem(world, pos.getX()+rx, pos.getY()+ry, pos.getZ()+rz, s);
											world.spawnEntity(ent);
											ent.motionX = 0;
											ent.motionY = -0.2F;
											ent.motionZ = 0;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void dropStack(World worldIn, BlockPos pos, ItemStack stack) {
		float f = 0.7F;
		double d0 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		double d1 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		double d2 = (double)(worldIn.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
		for(EnumFacing dir : BlockHardOreBase.DROP_SEARCH_DIRECTIONS) {
			if(!worldIn.getBlockState(pos.offset(dir)).isNormalCube() || dir == EnumFacing.DOWN) {
				EntityItem entityitem = new EntityItem(worldIn, (double)pos.getX() + d0+dir.getFrontOffsetX(), (double)pos.getY() + d1+dir.getFrontOffsetY(), (double)pos.getZ() + d2+dir.getFrontOffsetZ(), stack);
				entityitem.setDefaultPickupDelay();
				worldIn.spawnEntity(entityitem);
				return;
			}
		}
	}
}
