package com.draco18s.farming.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

public class SaltOreBlock extends Block {

	public SaltOreBlock(Properties props) {
		super(props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1, 2.5f));
	}
}
