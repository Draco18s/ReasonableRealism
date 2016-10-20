package com.draco18s.farming;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FarmingEventHandler {

	@SubscribeEvent
	public void onCropGrow(BlockEvent.CropGrowEvent event) {
		World world = event.getWorld();
		IBlockState state = event.getState();
		BlockPos pos = event.getPos();
		if(state.getBlock() == Blocks.WHEAT) {
			Biome biomeAtPos = world.getBiomeForCoordsBody(pos);
			if(biomeAtPos.getTemperature() < 0.5f) {
				//if it is cold, deny crop growth 75% of the time
				if(world.rand.nextInt(4) == 0) {
					event.setResult(Result.DEFAULT);
				}
				else {
					event.setResult(Result.DENY);
				}
			}
			else if(biomeAtPos.getRainfall() > 0.5f) {
				//if it is warm and wet, force crop growth 25% of the time
				if(world.rand.nextInt(4) == 0) {
					event.setResult(Result.ALLOW);
				}
				else {
					event.setResult(Result.DEFAULT);
				}
			}
		}
	}
}
