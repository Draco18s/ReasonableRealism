package com.draco18s.hardlib;

import com.draco18s.flowers.states.StateMapperFlowers;
import com.draco18s.hardlib.api.blockproperties.Props;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.interfaces.IBlockWithMapper;
import com.draco18s.hardlib.api.interfaces.IItemWithMeshDefinition;
import com.draco18s.hardlib.api.internal.IMetaLookup;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
	
	public static <T extends Block & IBlockWithMapper> void registerBlockWithCustomItemAndMapper(T block, ItemBlock iBlock, String registryname) {
		HardLib.proxy._registerBlockWithCustomItemAndMapper(block, iBlock, registryname);
	}

	public static void registerItem(Item item, String registryname) {
		HardLib.proxy._registerItem(item, registryname);
	}

	public static <T extends IMetaLookup> void registerItemWithVariants(Item item, String registryname, T variant) {
		HardLib.proxy._registerItemWithVariants(item, registryname, variant);
	}
	
	/**
	 * Registers an item and loads/registers models based on the item's SubItems.
	 * @param item - Must implement IItemWithMeshDefinition
	 * @param registryname
	 */
	public static <T extends Item & IItemWithMeshDefinition> void registerItemWithCustomMeshDefinition(T item, String registryname) {
		HardLib.proxy._registerItemWithCustomMeshDefinition(item, registryname);
	}
	
	/**
	 * Registers a specific model for variant item stack.
	 * @param item - Must implement IItemWithMeshDefinition
	 * @param variantStack
	 */
	public static <T extends Item & IItemWithMeshDefinition> void registerSpecificItemVariantsWithBakery(T item, ItemStack variantStack) {
		HardLib.proxy._registerSpecificItemVariantsWithBakery(item, variantStack);
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

	public <T extends Block & IBlockWithMapper> void _registerBlockWithCustomItemAndMapper(T block, ItemBlock iBlock, String registryname) {
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

	public <T extends Item & IItemWithMeshDefinition> void _registerItemWithCustomMeshDefinition(T item, String registryname) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}
	
	public <T extends IItemWithMeshDefinition> void _registerSpecificItemVariantsWithBakery(T item, ItemStack variantStack) {
		
	}

	public <T extends IMetaLookup> void _registerItemWithVariants(Item item, String registryname, T variant) {
		item.setRegistryName(registryname);
		item.setUnlocalizedName(item.getRegistryName().toString());
		GameRegistry.register(item);
	}
}