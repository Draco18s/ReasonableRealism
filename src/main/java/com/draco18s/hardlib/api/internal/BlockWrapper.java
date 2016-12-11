package com.draco18s.hardlib.api.internal;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;

public class BlockWrapper {
	public final Block block;
	public final int meta;
	private final int oreValue;
	private final PropertyInteger valProp;
	
	/**
	 * Wildcard blockstate
	 * @param b
	 */
	public BlockWrapper(Block b) {
		this(b, 9);
	}
	
	/**
	 * By valid blockstate
	 * @param state
	 */
	public BlockWrapper(IBlockState state) {
		this(state, 9);
	}
	
	/**
	 * Wildcard blockstate with ore value
	 * @param b
	 * @param v - Relative ore value per block; roughly "how many nuggets" (9 for an ingot)
	 */
	public BlockWrapper(Block b, int v) {
		this.block = b;
		this.meta = -1;
		this.oreValue = v;
		this.valProp = null;
	}
	
	/**
	 * By valid blockstate with ore value (where blockstate defines ore type)
	 * @param state
	 * @param v - Relative ore value per block; roughly "how many nuggets" (9 for an ingot)
	 */
	public BlockWrapper(IBlockState state, int v) {
		this.block = state.getBlock();
		this.meta = block.getMetaFromState(state);
		this.oreValue = v;
		this.valProp = null;
	}
	
	/**
	 * By valid block with property (where the property defines the ore value)
	 * @param b
	 * @param prop - Relative ore value by property; roughly "how many nuggets" (9 for an ingot)
	 */
	public BlockWrapper(Block b, PropertyInteger prop) {
		this.block = b;
		this.meta = -1;
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
	public BlockWrapper(IBlockState state, PropertyInteger prop) {
		this.block = state.getBlock();
		this.meta = -1;
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
			return other.block == this.block && (other.meta == -1 || this.meta == -1 || other.meta == this.meta);
		}
		return false;
	}

	@Deprecated
	public int getOreValue() {
		return oreValue;
	}

	public int getOreValue(@Nonnull IBlockState state) {
		if(valProp != null) {
			return state.getValue(valProp);
		}
		if(oreValue < 0) return 0;
		return oreValue;
	}

	@Override
	public String toString() {
		return block.getRegistryName() + ":" + (valProp == null?(meta>=0?meta:"*"):valProp.getName()) + " [" + (valProp != null?"*":oreValue) + "]";
	}
}
