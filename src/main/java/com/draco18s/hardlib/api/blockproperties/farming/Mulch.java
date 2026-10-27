package com.draco18s.hardlib.api.blockproperties.farming;

import com.draco18s.hardlib.HardLib;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** What farmland is mulched with: a carpet (any wool, moss or pale moss), or leaf litter. */
public enum Mulch implements StringRepresentable {
	WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, LIGHT_GRAY, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK,
	MOSS, PALE_MOSS,
	LEAVES;

	private final String name;

	private Mulch() {
		this.name = toString().toLowerCase();
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	/** The item it's laid with (and, for carpets, what comes back off). */
	public Item item() {
		return BuiltInRegistries.ITEM.getValue(HardLib.getRL("minecraft", this == LEAVES ? "leaf_litter" : name + "_carpet"));
	}

	/** Carpets come back off with shears; leaves are spent. */
	public boolean retrievable() {
		return this != LEAVES;
	}

	/** The mulch an item lays, or null. */
	public static Mulch fromItem(ItemStack stack) {
		for(Mulch m : values()) {
			if(stack.is(m.item())) return m;
		}
		return null;
	}
}
