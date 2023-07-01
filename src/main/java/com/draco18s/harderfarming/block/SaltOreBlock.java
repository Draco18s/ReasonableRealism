package com.draco18s.harderfarming.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class SaltOreBlock extends Block {

	public SaltOreBlock() {
		super(Properties.of(Material.STONE).strength(1, 2.5f));
	}
}
