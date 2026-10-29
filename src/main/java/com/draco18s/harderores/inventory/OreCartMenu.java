package com.draco18s.harderores.inventory;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.OreMinecart;
import com.draco18s.hardlib.api.internal.CommonContainerMenu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;

/** The ore cart's 5x4 inventory, plus the buttons that pick which side its chute empties onto. */
public class OreCartMenu extends CommonContainerMenu {
	public static final int BUTTON_LEFT = 0;
	public static final int BUTTON_RIGHT = 1;

	private final DataSlot dumpRight;

	public OreCartMenu(int windowID, Inventory playerInventory) {
		this(windowID, playerInventory, new SimpleContainer(20), null);
	}

	public OreCartMenu(int windowID, Inventory playerInventory, OreMinecart cart) {
		this(windowID, playerInventory, cart, cart);
	}

	private OreCartMenu(int windowID, Inventory playerInventory, Container container, OreMinecart cart) {
		super(HarderOres.ModContainerTypes.ore_cart, windowID, 184, playerInventory, container);
		for(int j = 0; j < 4; ++j) {
			for(int k = 0; k < 5; ++k) {
				this.addSlot(new Slot(container, k + j * 5, 26 + k * 18, 18 + j * 18));
			}
		}
		dumpRight = cart == null ? DataSlot.standalone() : new DataSlot() {
			@Override
			public int get() {
				return cart.dumpsRight() ? 1 : 0;
			}

			@Override
			public void set(int value) {
				cart.setDumpsRight(value != 0);
			}
		};
		addDataSlot(dumpRight);
	}

	public boolean dumpsRight() {
		return dumpRight.get() != 0;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if(id != BUTTON_LEFT && id != BUTTON_RIGHT) return false;
		dumpRight.set(id == BUTTON_RIGHT ? 1 : 0);
		return true;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		container.stopOpen(player);
	}
}
