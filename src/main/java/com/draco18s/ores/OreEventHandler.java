package com.draco18s.ores;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.ores.block.ore.BlockHardOreBase;
import com.draco18s.ores.networking.Packets;
import com.draco18s.ores.networking.ToClientMessageOreParticles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
					level = level*2+1;
					boolean anyOre = false;
					for(EnumFacing dir : EnumFacing.VALUES) {
						if(world.getBlockState(pos).getProperties().containsKey(Props.ORE_DENSITY)) {
							anyOre = true;
						}
					}
					if(!anyOre) {
						//System.out.println("No ore adjacent");
						Iterable<BlockPos> cube = pos.getAllInBox(pos.add(-level, -level, -level), pos.add(level, level, level));
						for(BlockPos p : cube) {
							if(world.getBlockState(p).getProperties().containsKey(Props.ORE_DENSITY)) {
								//System.out.println("Sending packet");
								ToClientMessageOreParticles packet = new ToClientMessageOreParticles(Packets.PROSPECTING, p);
								OresBase.networkWrapper.sendTo(packet, (EntityPlayerMP) event.getHarvester());
							}
						}
					}
				}
			}
		}
	}

	/*public void postOreGen(CogOreGenEvent event) {
		
	}*/
	
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
