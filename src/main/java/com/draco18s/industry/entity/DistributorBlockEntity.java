package com.draco18s.industry.entity;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;
import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.inventory.WoodHopperContainerMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class DistributorBlockEntity extends AbstractHopper implements ICustomContainer {
	protected int delay;

	public DistributorBlockEntity(BlockPos pos, BlockState state) {
		super(ExpandedIndustry.ModTileEntities.machine_distributor, pos, state);
	}
	
	@Override
	public void openGUI(ServerPlayer player) {
		if (!level.isClientSide) {
			NetworkHooks.openScreen(player, this, getBlockPos());
		}
	}

	@Override
	protected Component getDefaultName() {
		return MutableComponent.create(new TranslatableContents("expindustry:machine_distributor.name", "Distributor", TranslatableContents.NO_ARGS));
	}

	@Override
	protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
		return new WoodHopperContainerMenu(p_58627_, p_58628_, inventory, this);
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, DistributorBlockEntity hopper) {
		Direction curFacing = world.getBlockState(pos).getValue(HopperBlock.FACING);
		if(curFacing == Direction.DOWN || curFacing == Direction.UP) {
			world.setBlock(pos, state.setValue(HopperBlock.FACING, Direction.NORTH), 3);
		}
		else {
			hopper.delay--;
			if(hopper.delay <= 0) {
				world.setBlock(pos, state.setValue(HopperBlock.FACING, curFacing.getClockWise()), 3);
				hopper.delay = 4;
				hopper.setCooldown(0);
				AbstractHopper.pushItemsTick(world, pos, state, hopper);
			}
		}
	}
}
