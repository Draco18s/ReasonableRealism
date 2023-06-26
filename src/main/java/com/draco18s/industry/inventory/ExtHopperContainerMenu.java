package com.draco18s.industry.inventory;

import com.draco18s.hardlib.api.internal.CommonContainerMenu;
import com.draco18s.industry.entity.AbstractHopper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public abstract class ExtHopperContainerMenu extends CommonContainerMenu {
	public AbstractHopper tileEntity;

	public ExtHopperContainerMenu(MenuType<?> p_38851_, int windowID, int size) {
		super(p_38851_, windowID, size, 133);
	}

	public ExtHopperContainerMenu(MenuType<?> menuType, int windowID, Inventory p_39641_, Container container) {
		super(menuType, windowID, 133, p_39641_, container);
	}
}
