package com.draco18s.hardlib.api.interfaces;

import java.awt.Color;

import com.draco18s.hardlib.api.blockproperties.Props;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * Used to denote a block that needs to be broken multiple times.  Implemented by BlockHardOreBase.<br/>
 * The intended use of this interface is to mark a block class as a Hard Ore so that other mods recognize the ore
 * as one that needs to be treated specially for auto-miners, etc.<br/>
 * Must have {@link Props#ORE_DENSITY} as a block property or things will go very bad.
 * @author Draco18s
 *
 */
public interface IBlockMultiBreak {
	/**
	 * Highly unlikely that the world or blockstate will ever be utilized
	 * @param worldIn
	 * @param pos
	 * @param state
	 * @return the density change for breaking the ore block once
	 */
	int getDensityChangeOnBreak(IBlockAccess worldIn, BlockPos pos, IBlockState state);
	
	/**
	 * Determines the color of the prospector enchantment particle effect
	 * @param worldIn
	 * @param pos
	 * @param state
	 * @return
	 */
	Color getProspectorParticleColor(IBlockAccess worldIn, BlockPos pos, IBlockState state);
}
