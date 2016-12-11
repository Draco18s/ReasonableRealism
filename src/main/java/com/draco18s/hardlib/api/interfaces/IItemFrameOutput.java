package com.draco18s.hardlib.api.interfaces;

import net.minecraft.entity.item.EntityItemFrame;

public interface IItemFrameOutput {
	int getRedstoneOutput(EntityItemFrame entity, int rotation);
}
