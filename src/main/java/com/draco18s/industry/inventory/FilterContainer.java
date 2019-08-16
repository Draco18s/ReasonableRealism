package com.draco18s.industry.inventory;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.entity.FilterTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class FilterContainer extends ExtHopperContainer {

	@OnlyIn(Dist.CLIENT)
	public FilterContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
		this(windowID, playerInventory, null, (FilterTileEntity)Minecraft.getInstance().world.getTileEntity(extraData.readBlockPos()));
	}

	public FilterContainer(int windowID, PlayerInventory playerInventory, IItemHandler inven, FilterTileEntity te) {
		super(ExpandedIndustry.ModContainerTypes.machine_filter, windowID, 3);
		if(inven == null) {
			inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
		}
		for (int i = 0; i < 5; ++i) {
			this.addSlot(new SlotValidInsert(inven, i, 8 + i * 18, 17));
		}

		for(int j = 5; j < 11; ++j) {
			this.addSlot(new SlotIInventory(inven, j, 8 + (j-5) * 18 + (int)((j-5)/2)*9, 48));
		}

		for(int l = 0; l < 3; ++l) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + 78));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 136));
		}

		//bindPlayerInventory(playerInventory);
		tileEntity = te;
	}

	/*@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
		
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}*/
}
