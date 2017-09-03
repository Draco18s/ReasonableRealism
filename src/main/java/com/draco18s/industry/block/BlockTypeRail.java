package com.draco18s.industry.block;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class BlockTypeRail extends BlockRailDetector {

	public BlockTypeRail() {
		super();
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World world, BlockPos pos) {
		int best = 0;
		if (((Boolean)blockState.getValue(POWERED)).booleanValue()) {
			List<EntityMinecart> carts = this.findMinecarts(world, pos, EntityMinecart.class);
			if (!carts.isEmpty()) {
				int curr = 0;
				Iterator<EntityMinecart> it = carts.iterator();
				while(it.hasNext()) {
					EntityMinecart cart = it.next();
					String n = cart.getCartItem().getDisplayName().toLowerCase();
					//System.out.println("Cart: " + n);
					TileEntitySign te = (TileEntitySign)world.getTileEntity(pos.up());
					if(te != null) {
						curr = parseSign(te.signText, n, cart);
						if(curr > best)
							best = curr;
					}
					te = (TileEntitySign)world.getTileEntity(pos.down(2));
					if(te != null) {
						curr = parseSign(te.signText, n, cart);
						if(curr > best)
							best = curr;
					}
				}
			}
		}
		return best;
	}
	
	private int parseSign(ITextComponent[] signText, final String cartName, EntityMinecart cart) {
		int curr = 0;
		int best = 0;
		for(ITextComponent line : signText) {
			String s = line.getUnformattedComponentText();
			//System.out.println("Sign: " + s);
			if(s.length() > 0 && s.indexOf('=') > 0) {
				curr = parse(s, "=", cartName, cart);
			}
			else if(s.length() > 0 && s.indexOf(':') > 0) {
				curr = parse(s, ":", cartName, cart);
			}
			if(curr > best)
				best = curr;
		}
		return best;
	}

	private int parse(String input, String delimeter, String name, EntityMinecart cart) {
		String[] s2 = input.toLowerCase().split(delimeter);
		List<Entity> ridingEnt = cart.getPassengers();
		EntityType t = EntityType.asEntityType(s2[0]);
		
		if(s2[0].equals("empty")) { s2[0] = "minecart"; }
		if(s2[0].equals("command")) { s2[0] = "minecart with command block"; }
		if(t != null) {
			if(name.endsWith("minecart")) {
				for(Entity e : ridingEnt) {
					if(isOfType(e,t)) {
						return Integer.parseInt(s2[1]);
					}
				}
			}
			return 0;
		}
		else if(ridingEnt.size() > 0) {
			for(Entity e : ridingEnt) {
				String name2 = e.getName().toLowerCase();
				if(s2[0].equals(name2)) {
					return Integer.parseInt(s2[1]);
				}
			}
		}
		if(name.endsWith(s2[0])) {
			return Integer.parseInt(s2[1]);
		}
		return 0;
	}
	
	private enum EntityType {
		ENTITY,
		MOB,
		HOSTILE,
		PASSIVE,
		PLAYER;
		
		public static EntityType asEntityType(String str) {
		    for (EntityType me : EntityType.values()) {
		        if (me.name().equalsIgnoreCase(str))
		            return me;
		    }
		    return null;
		}
	}
	
	private boolean isOfType(Entity ent, EntityType type) {
		switch(type) {
			case ENTITY:
				return true;
			case MOB:
			case HOSTILE:
				return ent instanceof EntityLiving &&!isOfType(ent, EntityType.PLAYER) && !isOfType(ent, EntityType.PASSIVE);
			case PASSIVE:
				return (ent instanceof EntityCreature && !(ent instanceof EntityMob)) || ent instanceof EntityAmbientCreature;
			case PLAYER:
				return ent instanceof EntityPlayer;
			default:
				break;
		}
		return false;
	}
}
