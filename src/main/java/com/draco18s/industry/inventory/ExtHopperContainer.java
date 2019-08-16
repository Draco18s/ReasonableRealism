package com.draco18s.industry.inventory;

import com.draco18s.hardlib.api.internal.CommonContainer;
import com.draco18s.industry.entity.AbstractHopper;

import net.minecraft.inventory.container.ContainerType;

public class ExtHopperContainer extends CommonContainer {

	public AbstractHopper tileEntity;

	protected ExtHopperContainer(ContainerType<?> type, int id, int size) {
		super(type, id, size);
	}
}
