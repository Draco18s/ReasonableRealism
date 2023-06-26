package com.draco18s.industry.inventory;

import com.draco18s.hardlib.api.internal.inventory.MaxSizeItemStackHandler;
import com.draco18s.industry.ExpandedIndustry;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class WoodHopperContainerMenu extends ExtHopperContainerMenu {

	public WoodHopperContainerMenu(int windowID, Inventory p_39641_) {
		this(windowID, p_39641_, new MaxSizeItemStackHandler(5,16), new SimpleContainer(5));
	}

	public WoodHopperContainerMenu(int windowID, int size) {
		super(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, windowID, size);
	}

	public WoodHopperContainerMenu(int windowID, Inventory playerInventory, IItemHandler inven, Container container) {
		super(ExpandedIndustry.ModContainerTypes.machine_wood_hopper, windowID, playerInventory, container);
		for(int j = 0; j < 5; ++j) {
			this.addSlot(new SlotItemHandler(inven, j, 44 + j * 18, 20));
		}
	}

	/*@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		return null;
	}

	@Override
	public boolean stillValid(Player p_38874_) {
		return false;
	}

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
	}*/

}
