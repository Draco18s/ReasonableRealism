package com.draco18s.hardlib;

import com.draco18s.flowers.states.StateMapperFlowers;
import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.internal.IMetaLookup;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class EasyRegistry {
	
	public static void registerBlock(Block block, String registryname) {
		HardLib.proxy._registerBlock(block, registryname);
	}
	
	public static void registerBlockWithItem(Block block, String registryname) {
		HardLib.proxy._registerBlockWithItem(block, registryname);
	}
	
	public static void registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		HardLib.proxy._registerBlockWithCustomItem(block, iBlock, registryname);
	}
	
	public static void registerBlockWithCustomItemAndMapper(Block block, ItemBlock iBlock, StateMapperBase mapper, String registryname) {
		HardLib.proxy._registerBlockWithCustomItemAndMapper(block, iBlock, mapper, registryname);
	}

	public static void registerItem(Item item, String registryname) {
		HardLib.proxy._registerItem(item, registryname);
	}

	public static <T extends IMetaLookup> void registerItemWithVariants(Item item, String registryname, T variant) {
		HardLib.proxy._registerItemWithVariants(item, registryname, variant);
	}
	
	public void _registerBlock(Block block, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		GameRegistry.register(block);
	}

	public void _registerBlockWithItem(Block block, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		ItemBlock ib = new ItemBlock(block);
		ib.setRegistryName(registryname);
		GameRegistry.register(block);
		GameRegistry.register(ib);
	}

	public void _registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		iBlock.setRegistryName(registryname);
		GameRegistry.register(block);
		GameRegistry.register(iBlock);
	}

	public void _registerBlockWithCustomItemAndMapper(Block block, ItemBlock iBlock, StateMapperBase mapper, String registryname) {
		ModelLoader.setCustomStateMapper(block, mapper);
		block.setRegistryName(registryname);
		block.setUnlocalizedName(block.getRegistryName().toString());
		iBlock.setRegistryName(registryname);
		GameRegistry.register(block);
		GameRegistry.register(iBlock);
	}

	public void _registerItem(Item item, String registryname) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}

	public <T extends IMetaLookup> void _registerItemWithVariants(Item item, String registryname, T variant) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}
}