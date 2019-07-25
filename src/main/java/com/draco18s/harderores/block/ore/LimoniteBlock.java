package com.draco18s.harderores.block.ore;

import net.minecraft.block.Block;

public class LimoniteBlock extends Block {

	public LimoniteBlock(Properties properties) {
		super(properties);
	}
	
	/*@Override
	public int quantityDropped(BlockState state, int fortune, Random random) {
		int r = 1;
		if(fortune > 0) r += random.nextInt(1);
		r += random.nextInt(1);
		return r;
	}*/
	
	/*@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return OresBase.rawOre;
	}*/

	/*@Override
	public int damageDropped(BlockState state) {
		return EnumOreType.LIMONITE.meta;
	}*/
}