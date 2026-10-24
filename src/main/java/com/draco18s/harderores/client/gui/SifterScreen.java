package com.draco18s.harderores.client.gui;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.inventory.SifterContainerMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class SifterScreen extends TimedMachineScreen<SifterContainerMenu> {
	public SifterScreen(SifterContainerMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, HarderOres.getRL("textures/gui/sifter.png"), 40);
	}

	@Override
	protected int getTime() {
		return menu.getTime();
	}
}
