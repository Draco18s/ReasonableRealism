package com.draco18s.industry.inventory;

import com.draco18s.hardlib.api.internal.inventory.MaxSizeItemStackHandler;
import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.entity.AbstractHopper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WoodHopperContainer extends ExtHopperContainer {

	@OnlyIn(Dist.CLIENT)
	public WoodHopperContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowID, playerInventory, new MaxSizeItemStackHandler(5,16), (AbstractHopper)Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public WoodHopperContainer(int windowID, PlayerInventory playerInventory, IItemHandler inven, AbstractHopper te) {
		super(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, windowID, 3);

		for(int j = 0; j < 5; ++j) {
			this.addSlot(new SlotItemHandler(inven, j, 44 + j * 18, 20));
		}

		for(int l = 0; l < 3; ++l) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 51));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 109));
		}

		//bindPlayerInventory(playerInventory);
		tileEntity = te;
	}

}
