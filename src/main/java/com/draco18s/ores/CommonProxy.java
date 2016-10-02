package com.draco18s.ores;

import java.lang.reflect.InvocationTargetException;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.blockproperties.EnumOreType;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {
	public void registerRenderers() {
		
	}
	
	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new OreEventHandler());
	}
}
