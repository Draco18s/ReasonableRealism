package com.draco18s.hardlib.internal;

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
		this(b, -1);
	}
	
	/**
	 * By valid blockstate
	 * @param state
	 */
	public BlockWrapper(IBlockState state) {
		this(state, -1);
	}
	
	/**
	 * Wildcard blockstate with ore value
	 * @param b
	 * @param v - Relative ore value per block
	 */
	public BlockWrapper(Block b, int v) {
		this.block = b;
		this.meta = -1;
		this.oreValue = v;
		this.valProp = null;
	}
	
	/**
	 * By valid blockstate with ore value
	 * @param state
	 * @param v - Relative ore value per block
	 */
	public BlockWrapper(IBlockState state, int v) {
		this.block = state.getBlock();
		this.meta = block.getMetaFromState(state);
		this.oreValue = v;
		this.valProp = null;
	}
	
	/**
	 * By valid block with property
	 * @param b
	 * @param v - Relative ore value by property
	 */
	public BlockWrapper(Block b, PropertyInteger prop) {
		this.block = b;
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
}
