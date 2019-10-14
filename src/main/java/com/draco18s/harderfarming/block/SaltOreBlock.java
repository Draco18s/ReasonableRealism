package com.draco18s.harderfarming.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class SaltOreBlock extends Block {

	public SaltOreBlock() {
		super(Properties.create(Material.ROCK).hardnessAndResistance(1, 2.5f).harvestTool(ToolType.PICKAXE).harvestLevel(0));
	}
}
