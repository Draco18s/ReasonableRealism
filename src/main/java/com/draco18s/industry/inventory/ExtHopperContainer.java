package com.draco18s.industry.inventory;

import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.api.internal.CommonContainer;
import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.entity.AbstractHopper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ExtHopperContainer extends CommonContainer {

	public AbstractHopper tileEntity;

	public ExtHopperContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowID, playerInventory, new ItemStackHandler(5), (AbstractHopper)Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public ExtHopperContainer(int windowID, PlayerInventory playerInventory, IItemHandler inven, AbstractHopper te) {
		super(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, windowID, 3);

		for(int j = 0; j < 5; ++j) {
			this.addSlot(new SlotItemHandler(inven, j, 44 + j * 18, 20));
		}

		bindPlayerInventory(playerInventory);
		tileEntity = te;
	}

}
