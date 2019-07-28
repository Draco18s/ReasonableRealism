package com.draco18s.hardlib.api.interfaces;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;

public interface ICustomContainer extends INamedContainerProvider {
	void openGUI(ServerPlayerEntity player);
}
