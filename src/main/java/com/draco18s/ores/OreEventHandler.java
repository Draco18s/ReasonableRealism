package com.draco18s.ores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.ores.EnumOreType;
import com.draco18s.ores.block.ore.BlockHardOreBase;
import com.draco18s.ores.networking.Packets;
import com.draco18s.ores.networking.ToClientMessageOreParticles;
import com.draco18s.ores.util.OresAchievements;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;

public class OreEventHandler {
	@SubscribeEvent
	public void harvest(HarvestDropsEvent event) {
		//System.out.println("Event");
		if(event.getHarvester() != null) {
			//System.out.println("Done by player");
			EntityPlayer harvester = event.getHarvester();
			IBlockState state = event.getState();
			World world = event.getWorld();
			BlockPos pos = event.getPos();
			//System.out.println("active: " + harvester.getActiveItemStack());
			if(state.getProperties().containsKey(Props.ORE_DENSITY)) {
				int level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchCracker, harvester.getHeldItemMainhand());
				int max = 0;
				float rollover = 0;
				for(level *= 2;max < 12 && level > 0;max++) {
					EnumFacing dir = EnumFacing.VALUES[world.rand.nextInt(6)];
					if(state.getBlock() == world.getBlockState(pos.offset(dir, 1)).getBlock()) {
						level--;
						//ArrayList<ItemStack> drps = null;
						List<ItemStack> drps = HardLibAPI.hardOres.mineHardOreOnce(world, pos.offset(dir, 1), 0);
						//System.out.println(dir + ":" + drps);
						if(drps != null) {
							rollover += 0.75f;
							if(rollover >= 0.75f) {
								harvester.getHeldItemMainhand().damageItem(1, harvester);
								rollover -= 1;
							}
							//for(ItemStack stack : drps) {
								//dropStack(world, event.getPos(), stack);
							//}
							event.getDrops().addAll(drps);
						}
					}
				}
				level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchPulverize, harvester.getHeldItemMainhand());

				if(level > 0 && state.getValue(Props.ORE_DENSITY) <= level*2+3) {
					//System.out.println("level " + level +", " + event.getDrops().size());
					Iterator<ItemStack> it = event.getDrops().iterator();
					ArrayList<ItemStack> newItems = new ArrayList<ItemStack>();
					max = 2+level;
					while(it.hasNext()) {
						ItemStack stk = it.next();
						ItemStack out = HardLibAPI.oreMachines.getMillResult(stk);
						if(out != null) {
							int s = Math.min(stk.stackSize,max);
							ItemStack dustStack = out.copy();
							//System.out.println("Num dust: " + (dustStack.stackSize*s));
							int n = dustStack.stackSize;
							dustStack.stackSize = 0;
							for(; s > 0; s--) {
								dustStack.stackSize += n;
								max--;
								stk.stackSize--;
							}
							newItems.add(dustStack);
							if(stk.stackSize == 0) {
								it.remove();
							}
						}
					}
					//System.out.println("Max: " + max);
					event.getDrops().addAll(newItems);
				}
			}

			if(state.getBlock() == Blocks.STONE) {
				//System.out.println("Stone broken");
				int level = EnchantmentHelper.getEnchantmentLevel(OresBase.enchProspector, harvester.getHeldItemOffhand());

				//System.out.println("Level: " + level);
				if(level > 0) {
					int llevel = level*2+1;
					boolean anyOre = false;
					for(EnumFacing dir : EnumFacing.VALUES) {
						if(world.getBlockState(pos).getProperties().containsKey(Props.ORE_DENSITY)) {
							anyOre = true;
						}
					}
					if(!anyOre) {
						//System.out.println("No ore adjacent");
						Iterable<BlockPos> cube = pos.getAllInBox(pos.add(-llevel, -llevel, -llevel), pos.add(llevel, llevel, llevel));
						for(BlockPos p : cube) {
							IBlockState st = world.getBlockState(p);
							if(HardLibAPI.hardOres.isHardOre(st)) {
								//System.out.println("Sending packet");
								ToClientMessageOreParticles packet = new ToClientMessageOreParticles(Packets.PROSPECTING, pos, p);
								OresBase.networkWrapper.sendTo(packet, (EntityPlayerMP) event.getHarvester());
								if(level >= 3) {
									if(world.rand.nextInt(20) <= (level-3)) {
										List<ItemStack> list = HardLibAPI.hardOres.getHardOreDropsOnce(world, p, 0);
										ArrayList<ItemStack> toDrop = new ArrayList();
										for(ItemStack s:list) {
											toDrop.add(HardLibAPI.oreMachines.getMillResult(s).copy());
										}
										for(ItemStack s:toDrop) {
											s.stackSize = 1;
											float rx = world.rand.nextFloat() * 0.6F + 0.2F;
					    					float ry = world.rand.nextFloat() * 0.2F + 0.6F - 1;
					    					float rz = world.rand.nextFloat() * 0.6F + 0.2F;
					    					EntityItem ent = new EntityItem(world, pos.getX()+rx, pos.getY()+ry, pos.getZ()+rz, s);
					    					world.spawnEntityInWorld(ent);
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
	
	@SubscribeEvent
	public void onPickup(PlayerEvent.ItemPickupEvent event) {
		Item item = event.pickedUp.getEntityItem().getItem();
		int meta = event.pickedUp.getEntityItem().getItemDamage();
		if(item == OresBase.rawOre && meta == EnumOreType.LIMONITE.meta) {
			event.player.addStat(OresAchievements.mineLimonite, 1);
		}
		if(item == OresBase.rawOre && meta == EnumOreType.IRON.meta) {
			event.player.addStat(OresAchievements.acquireIronChunk, 1);
		}
		if(item == Item.getItemFromBlock(OresBase.millstone)) {
			event.player.addStat(OresAchievements.craftMill, 1);
		}
		if(item == Item.getItemFromBlock(Blocks.STONE) && meta == BlockStone.EnumType.DIORITE.getMetadata()) {
			event.player.addStat(OresAchievements.mineDiorite, 1);
		}
		if(item == OresBase.rawOre && meta == EnumOreType.DIAMOND.meta){
			event.player.addStat(AchievementList.DIAMONDS, 1);
		}
		ItemStack s = HardLibAPI.oreMachines.getSiftResult(event.pickedUp.getEntityItem(), false);
		if(s != null && item != Items.DYE) {
			event.player.addStat(OresAchievements.grindOre, 1);
		}
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		Item item = event.crafting.getItem();
		if(item == Items.IRON_INGOT){
			if(event.player instanceof EntityPlayerMP && ((EntityPlayerMP)event.player).getStatFile().canUnlockAchievement(AchievementList.ACQUIRE_IRON)) {
				event.player.addStat(OresAchievements.fakeIronBar, 1);
				event.player.addStat(AchievementList.ACQUIRE_IRON, 1);
			}
		}
		if(item == Item.getItemFromBlock(OresBase.sluice)){
			event.player.addStat(OresAchievements.craftSluice, 1);
		}
		if(item == Item.getItemFromBlock(OresBase.sifter)){
			event.player.addStat(OresAchievements.craftSifter, 1);
		}
		if(item == Item.getItemFromBlock(OresBase.millstone)){
			event.player.addStat(OresAchievements.craftMill, 1);
		}
		if(item instanceof ItemTool){
			ItemTool tool = (ItemTool)item;
			if(tool.getToolMaterialName().equals(OresBase.toolMaterialDiamondStud.name())) {
				event.player.addStat(OresAchievements.craftDiamondStud, 1);
			}
		}
		if(item instanceof ItemHoe){
			ItemHoe tool = (ItemHoe)item;
			if(tool.getMaterialName().equals(OresBase.toolMaterialDiamondStud.name())) {
				event.player.addStat(OresAchievements.craftDiamondStud, 1);
			}
		}
		List<ItemStack> items = OreDictionary.getOres("nuggetIron");
		boolean isNugget = false;
		for(ItemStack s : items) {
			isNugget = isNugget || OreDictionary.itemMatches(s, event.crafting, false);
		}
		if(isNugget){
			event.player.addStat(OresAchievements.acquireNuggets, 1);
		}
	}
	
	@SubscribeEvent
	public void onSmelting(PlayerEvent.ItemSmeltedEvent event) {
		Item item = event.smelting.getItem();
		if(item == OresBase.rawOre && event.smelting.getItemDamage() == EnumOreType.IRON.meta) {
			event.player.addStat(OresAchievements.acquireIronChunk, 1);
		}
		if(item == Items.IRON_INGOT){
			if(event.player instanceof EntityPlayerMP && ((EntityPlayerMP)event.player).getStatFile().canUnlockAchievement(AchievementList.ACQUIRE_IRON)) {
				event.player.addStat(OresAchievements.fakeIronBar, 1);
				event.player.addStat(AchievementList.ACQUIRE_IRON, 1);
			}
		}
		List<ItemStack> items = OreDictionary.getOres("nuggetIron");
		boolean isNugget = false;
		for(ItemStack s : items) {
			isNugget = isNugget | OreDictionary.itemMatches(s, event.smelting, false);
		}
		if(isNugget){
			event.player.addStat(OresAchievements.acquireNuggets, 1);
		}
	}
	
	@SubscribeEvent
	public void onAchievement(AchievementEvent event) {
		if(event.getAchievement() == AchievementList.ACQUIRE_IRON){
			if(event.getEntityPlayer() instanceof EntityPlayerMP && ((EntityPlayerMP)event.getEntityPlayer()).getStatFile().canUnlockAchievement(OresAchievements.fakeIronBar)) {
				event.getEntityPlayer().addStat(OresAchievements.fakeIronBar, 1);
			}
			else {
				event.setCanceled(true);
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
				worldIn.spawnEntityInWorld(entityitem);
				return;
			}
		}
	}
}
