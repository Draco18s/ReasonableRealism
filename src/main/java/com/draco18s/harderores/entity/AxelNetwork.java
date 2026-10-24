package com.draco18s.harderores.entity;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.interfaces.IMechanicalPower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** How machines (the millstone, the packager) reach the windvane that powers them. */
public final class AxelNetwork {
	private static final int MAX_AXELS = 8;

	private AxelNetwork() { }

	/**
	 * Follows axels up from a machine (turning to follow a horizontal axel) for up to 8 blocks, to a windvane hub.
	 * @return the hub's mechanical power, or null if the axels don't end at a hub
	 */
	@Nullable
	public static IMechanicalPower findPowerSource(Level world, BlockPos machine) {
		int numBlocksOut = 0;
		BlockPos p = machine;
		Direction searchDir = Direction.UP;
		do {
			p = p.relative(searchDir, 1);
			BlockState axel = world.getBlockState(p);
			if(axel.getBlock() == HarderOres.ModBlocks.machine_axel) {
				if(axel.getValue(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.UP) {
					searchDir = Direction.UP;
				}
				else if(searchDir == Direction.UP) {
					searchDir = axel.getValue(BlockStateProperties.HORIZONTAL_FACING);
				}
				numBlocksOut++;
			}
			else {
				p = p.relative(searchDir.getOpposite());
				break;
			}
		} while(numBlocksOut <= MAX_AXELS);
		BlockState s = world.getBlockState(p);
		if(s.getBlock() != HarderOres.ModBlocks.machine_axel || s.getValue(BlockProperties.AXEL_ORIENTATION) != AxelOrientation.HUB) {
			return null;
		}
		BlockEntity te = world.getBlockEntity(p);
		return te == null ? null : te.getCapability(IMechanicalPower.MECHANICAL_POWER_CAPABILITY, searchDir).orElse(null);
	}
}
