package com.draco18s.hardlib.api.internal;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class BlockWrapper {
	public final Block block;
	private final int oreValue;
	private final IntegerProperty valProp;

	/**
	 * Wildcard blockstate with ore value
	 * @param b
	 * @param v - Relative ore value per block; roughly "how many nuggets" (9 for an ingot)
	 */
	public BlockWrapper(Block b, int v) {
		this.block = b;
		this.oreValue = v;
		this.valProp = null;
	}
	
	/**
	 * By valid block with property (where the property defines the ore value)
	 * @param b
	 * @param prop - Relative ore value by property; roughly "how many nuggets" (9 for an ingot)
	 */
	public BlockWrapper(Block b, IntegerProperty prop) {
		this.block = b;
		this.oreValue = -1;
		this.valProp = prop;
	}
	
	/**
	 * By blockstate with property (where the property defines the ore value)<br>
	 * <b>Don't use this one.</b> There's no way to perform state-to-state matching while ignoring a given property.<br>
	 * Blockstate will be treated as wildcard, identical to {@link #BlockWrapper(Block, PropertyInteger)}
	 * @param state
	 * @param prop - Relative ore value by property; roughly "how many nuggets" (9 for an ingot)
	 */
	public BlockWrapper(BlockState state, IntegerProperty prop) {
		this.block = state.getBlock();
		this.oreValue = -1;
		this.valProp = prop;
	}
	
	@Override
	public int hashCode() {
		return block.hashCode();
	}
	
	@Override
	public boolean equals(Object aThat) {
		if(aThat instanceof BlockWrapper) {
			BlockWrapper other = (BlockWrapper)aThat;
			return other.block == this.block;
		}
		return false;
	}

	public int getOreValue(@Nonnull BlockState state) {
		if(valProp != null) {
			return state.getValue(valProp);
		}
		return oreValue > 0 ? oreValue : 0;
	}

	@Override
	public String toString() {
		return block.getDescriptionId() + " [" + (valProp != null?"*":oreValue) + "]";
	}
}
