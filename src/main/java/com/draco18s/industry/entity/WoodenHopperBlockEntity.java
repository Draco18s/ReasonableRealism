package com.draco18s.industry.entity;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;
import com.draco18s.hardlib.api.internal.inventory.MaxSizeItemStackHandler;
import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.inventory.WoodHopperContainerMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;

public class WoodenHopperBlockEntity extends AbstractHopper implements ICustomContainer {

	public WoodenHopperBlockEntity(BlockPos worldPosition, BlockState blockState) {
		super(ExpandedIndustry.ModTileEntities.machine_wood_hopper, worldPosition, blockState);
		inventory = new MaxSizeItemStackHandler(5, 16);
	}

	@Override
	protected AbstractContainerMenu createMenu(int p_58627_, Inventory p_58628_) {
		return new WoodHopperContainerMenu(p_58627_, p_58628_, inventory, this);
	}

	@Override
	protected Component getDefaultName() {
		return MutableComponent.create(new TranslatableContents("expindustry:machine_wood_hopper.name", "Wooden Hopper", TranslatableContents.NO_ARGS));
	}
	
	@Override
	public void openGUI(ServerPlayer player) {
		if (!level.isClientSide) {
			NetworkHooks.openScreen(player, this, getBlockPos());
		}
	}

	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, AbstractHopper hopper) {
		if(world.getBlockState(pos).getValue(HopperBlock.FACING) != Direction.DOWN) {
			world.setBlock(pos, state.setValue(HopperBlock.FACING, Direction.DOWN), 3);
		}
		AbstractHopper.pushItemsTick(world, pos, state, hopper);
	}
}
