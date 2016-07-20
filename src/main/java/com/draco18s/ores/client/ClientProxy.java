package com.draco18s.ores.client;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.draco18s.hardlib.blockproperties.EnumOreType;
import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.internal.IMetaLookup;
import com.draco18s.ores.CommonProxy;
import com.google.common.collect.ImmutableList;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		
	}

	@Override
	public void registerBlock(Block block, String registryname) {
		super.registerBlock(block, registryname);
	}
	
	@Override
	public void registerBlockWithItem(Block block, String registryname) {
		super.registerBlockWithItem(block, registryname);
		registerBlockResources(block);
	}
	
	@Override
	public void registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		super.registerBlockWithCustomItem(block, iBlock, registryname);
		StateMapperBase b = new DefaultStateMapper();
		BlockStateContainer bsc = block.getBlockState();
		ImmutableList<IBlockState> values = bsc.getValidStates();
		for(IBlockState state : values) {
			registerBlockItemModelForMeta(block, block.getMetaFromState(state), b.getPropertyString(state.getProperties()));
		}
	}
	
	private void registerBlockResources(Block block) {
		Item item = Item.getItemFromBlock(block);
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(block.getRegistryName().toString()));
	}
	
	private void registerBlockItemModelForMeta(Block block, int metadata, String variant) {
		final Item item = Item.getItemFromBlock(block);
		if (item != null) {
			registerItemModelForMeta(item, metadata, variant);
		}
	}
	
	@Override
	public void registerItem(Item item, String registryname) {
		super.registerItem(item, registryname);
	}
	
	@Override
	public <T extends IMetaLookup> void RegisterItemWithVariants(Item item, String registryname, T variant) {
		super.RegisterItemWithVariants(item, registryname, variant);
		String variantName = variant.getID();
		List<ItemStack> subItems = new ArrayList<ItemStack>();
		item.getSubItems(item, CreativeTabs.SEARCH, subItems);
		for (ItemStack stack : subItems) {
			registerItemModelForMeta(item, stack.getMetadata(), variantName + "=" + variant.getByOrdinal(stack.getMetadata()));
		}
	}
	
	/*private void registerItemModel(Item item) {
		registerItemModel(item, item.getRegistryName().toString());
	}

	private void registerItemModel(Item item, String modelLocation) {
		final ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
		registerItemModel(item, fullModelLocation);
	}

	private void registerItemModel(Item item, ModelResourceLocation fullModelLocation) {
		ModelBakery.registerItemVariants(item, fullModelLocation); // Ensure the custom model is loaded and prevent the default model from being loaded
		registerItemModel(item, MeshDefinitionFix.create(stack -> fullModelLocation));
	}*/

	private void registerItemModel(Item item, ItemMeshDefinition meshDefinition) {
		ModelLoader.setCustomMeshDefinition(item, meshDefinition);
	}

	private void registerItemModelForMeta(Item item, int metadata, String variant) {
		registerItemModelForMeta(item, metadata, new ModelResourceLocation(item.getRegistryName(), variant));
	}

	private void registerItemModelForMeta(Item item, int metadata, ModelResourceLocation modelResourceLocation) {
		ModelLoader.setCustomModelResourceLocation(item, metadata, modelResourceLocation);
	}
}
