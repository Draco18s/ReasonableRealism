package com.draco18s.industry.block;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.BlockRailDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityMinecart;
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
					System.out.println("Cart: " + n);
					TileEntitySign te = (TileEntitySign)world.getTileEntity(pos.up());
					if(te != null) {
						curr = parseSign(te.signText, n);
						if(curr > best)
							best = curr;
					}
					te = (TileEntitySign)world.getTileEntity(pos.down(2));
					if(te != null) {
						curr = parseSign(te.signText, n);
						if(curr > best)
							best = curr;
					}
				}
			}
		}
		return best;
	}
	
	private int parseSign(ITextComponent[] signText, final String cartName) {
		int curr = 0;
		int best = 0;
		for(ITextComponent line : signText) {
			String s = line.getUnformattedComponentText();
			System.out.println("Sign: " + s);
			if(s.length() > 0 && s.indexOf('=') > 0) {
				curr = parse(s, "=", cartName);
			}
			else if(s.length() > 0 && s.indexOf(':') > 0) {
				curr = parse(s, ":", cartName);
			}
			if(curr > best)
				best = curr;
		}
		return best;
	}

	private int parse(String input, String delimeter, String name) {
		String[] s2 = input.toLowerCase().split(delimeter);
		
		if(s2[0].equals("empty")) { s2[0] = "minecart"; }
		if(s2[0].equals("command")) { s2[0] = "minecart with command block"; }
		System.out.println("	name:  " + name);
		System.out.println("	s2[0]: " + s2[0]);
		if(name.endsWith(s2[0])) {
			return Integer.parseInt(s2[1]);
		}
		return 0;
	}
}
