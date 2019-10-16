package com.draco18s.flowers.proxy;

import java.util.function.BiFunction;

import com.draco18s.flowers.OreFlowers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;

public class ClientProxy implements IProxy {

	@Override
	public void registerConfigGui(ModLoadingContext modLoadingContext) {
		BiFunction<Minecraft, Screen, Screen> guifact = (mc, screen) -> { return new ConfigScreen(); };
		modLoadingContext.registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> guifact);
	}
	
	public static class ConfigScreen extends Screen {
		//BIG TODO: requires Forge update
		protected ConfigScreen() {
			super(new TranslationTextComponent(OreFlowers.MODID+".config.screen.title"));
		}
		
	}
}
