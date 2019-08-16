package com.draco18s.harderores.inventory;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.PackagerTileEntity;
import com.draco18s.hardlib.api.internal.CommonContainer;
import com.draco18s.hardlib.api.internal.inventory.SlotOutput;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class PackagerContainer extends CommonContainer {
	public PackagerTileEntity tileEntity;

	@OnlyIn(Dist.CLIENT)
	public PackagerContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowID, playerInventory, new ItemStackHandler(3), (PackagerTileEntity)Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public PackagerContainer(int windowID, PlayerInventory playerInventory, IItemHandler inven, PackagerTileEntity te) {
		super(HarderOres.ModContainerTypes.packager, windowID, 3);
		tileEntity = te;
		//IItemHandler inven = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
		
		addSlot(new SlotPackaged(inven, 0, 71, 13));
		addSlot(new SlotPackaged(inven, 1, 89, 13));
		addSlot(new SlotOutput(inven, 2, 80, 58));
		bindPlayerInventory(playerInventory);
	}
}