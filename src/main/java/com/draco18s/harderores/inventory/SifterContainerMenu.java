package com.draco18s.harderores.inventory;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.internal.CommonContainerMenu;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.hardlib.api.internal.inventory.SlotOutput;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SifterContainerMenu extends CommonContainerMenu {
	private ContainerData data;

	public SifterContainerMenu(int p_39640_, Inventory p_39641_) {
		this(p_39640_, p_39641_, new ItemStackHandler(2), new OutputItemStackHandler(new ItemStackHandler(1)), new SimpleContainer(3), new SimpleContainerData(1));
	}

	public SifterContainerMenu(int p_38852_, int size) {
		super(HarderOres.ModContainerTypes.machine_sifter, p_38852_, size, 166);
	}

	public SifterContainerMenu(int p_39640_, Inventory p_39641_, IItemHandler inputs, IItemHandler outputs, Container container, ContainerData containerdata) {
		super(HarderOres.ModContainerTypes.machine_sifter, p_39640_, 166, p_39641_, container);
		
		addSlot(new SlotDust(inputs, 0, 71, 13));
		addSlot(new SlotDust(inputs, 1, 89, 13));
		addSlot(new SlotOutput(outputs, 0, 80, 58));

		this.data = containerdata;
		this.addDataSlots(containerdata);
	}

	public int getTime() {
		return data.get(0);
	}
}
