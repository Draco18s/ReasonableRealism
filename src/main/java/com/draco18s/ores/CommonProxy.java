package com.draco18s.ores;

import java.lang.reflect.InvocationTargetException;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy {
	public void registerRenderers() {
		
	}
	
	public void registerBlock(Block block, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		GameRegistry.register(block);
	}
	
	public void registerBlockWithItem(Block block, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		ItemBlock ib = new ItemBlock(block);
		ib.setRegistryName(registryname);
		GameRegistry.register(block);
		GameRegistry.register(ib);
	}

	public void registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		iBlock.setRegistryName(registryname);
		GameRegistry.register(block);
		GameRegistry.register(iBlock);
	}
	
	public void registerItem(Item item, String registryname) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}
	
	public <T extends IMetaLookup> void RegisterItemWithVariants(Item item, String registryname, T variant) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}

	public void registerEventHandlers() {
		MinecraftForge.EVENT_BUS.register(new OreEventHandler());
	}
}
