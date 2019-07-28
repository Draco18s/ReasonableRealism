package com.draco18s.harderores.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class HardOreItem extends BlockItem {
	private final int density;

	public HardOreItem(Block blockIn, int densityValue, Properties builder) {
		super(blockIn, builder);
		density = densityValue;
	}

	public int getDensity() {
		return density;
	}
}
