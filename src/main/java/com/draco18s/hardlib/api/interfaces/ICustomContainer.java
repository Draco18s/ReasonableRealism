package com.draco18s.hardlib.api.interfaces;

//import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.server.level.ServerPlayer;

public interface ICustomContainer /*extends INamedContainerProvider*/ {
	void openGUI(ServerPlayer player);
}
