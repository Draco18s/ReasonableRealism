package com.draco18s.harderores.client.gui;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.inventory.PackagerContainerMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

/** Uses the sifter's GUI, as 1.12 did: the packager has the same slots-over-output layout. */
public class PackagerScreen extends TimedMachineScreen<PackagerContainerMenu> {
	public PackagerScreen(PackagerContainerMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, HarderOres.getRL("textures/gui/sifter.png"), 100);
	}

	@Override
	protected int getTime() {
		return menu.getTime();
	}
}
