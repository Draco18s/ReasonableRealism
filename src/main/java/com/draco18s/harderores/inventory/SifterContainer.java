package com.draco18s.harderores.inventory;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.SifterTileEntity;
import com.draco18s.hardlib.api.internal.CommonContainer;
import com.draco18s.hardlib.api.internal.inventory.SlotOutput;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class SifterContainer extends CommonContainer {
	public SifterTileEntity tileEntity;
	
	public SifterContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowID, playerInventory, new ItemStackHandler(3), (SifterTileEntity)Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public SifterContainer(int windowID, PlayerInventory playerInventory, IItemHandler inven, SifterTileEntity te) {
		super(HarderOres.ModContainerTypes.sifter, windowID, 3);

		addSlot(new SlotDust(inven, 0, 71, 13));
		addSlot(new SlotDust(inven, 1, 89, 13));
		addSlot(new SlotOutput(inven, 2, 80, 58));
		bindPlayerInventory(playerInventory);
		tileEntity = te;
	}
}