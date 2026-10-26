package com.draco18s.harderores.inventory;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.recipe.PackingRecipe;
import com.draco18s.hardlib.api.internal.inventory.HandlerSlot;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.hardlib.api.internal.inventory.SlotOutput;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class PackagerContainerMenu extends MachineRecipeBookMenu<PackingRecipe> {
	private ContainerData data;

	public PackagerContainerMenu(int windowID, Inventory inventory) {
		this(windowID, inventory, new ItemStacksResourceHandler(2), new OutputItemStackHandler(new ItemStacksResourceHandler(1)), new SimpleContainer(3), new SimpleContainerData(1));
	}

	public PackagerContainerMenu(int windowID, Inventory inventory, ItemStacksResourceHandler inputs, OutputItemStackHandler outputs, Container container, ContainerData containerdata) {
		super(HarderOres.ModContainerTypes.packager, windowID, 166, inventory, container, HarderOres.RecipeTypes.PACKING);

		addSlot(new HandlerSlot(inputs, 0, 71, 13));
		addSlot(new HandlerSlot(inputs, 1, 89, 13));
		addSlot(new SlotOutput(outputs, 0, 80, 58));

		this.data = containerdata;
		this.addDataSlots(containerdata);
	}

	/** Ticks left on the current pack, out of 100. */
	public int getTime() {
		return data.get(0);
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return HarderOres.RecipeBookTypes.PACKAGER;
	}
}
