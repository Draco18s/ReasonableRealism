package com.draco18s.industry.entity;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.inventory.ExtHopperContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkHooks;

public class WoodenHopperTileEntity extends AbstractHopper {

	public WoodenHopperTileEntity() {
		super(ExpandedIndustry.ModTileEntities.machine_wood_hopper);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
		return new ExtHopperContainer(windowID, playerInventory, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("expindustry:wood_hopper.name");
	}

	@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!world.isRemote) {
			NetworkHooks.openGui(player, this, getPos());
		}
	}
}
