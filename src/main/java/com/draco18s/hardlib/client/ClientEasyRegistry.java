package com.draco18s.hardlib.client;

import java.util.ArrayList;
import java.util.List;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.internal.IMetaLookup;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
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
import net.minecraftforge.client.model.ModelLoader;

public class ClientEasyRegistry extends EasyRegistry {

	@Override
	public void _registerBlock(Block block, String registryname) {
		this.registerBlock(block, registryname);
	}

	@Override
	public void _registerBlockWithItem(Block block, String registryname) {
		this.registerBlockWithItem(block, registryname);
		registerBlockResources(block);
	}

	@Override
	public void _registerBlockWithCustomItem(Block block, ItemBlock iBlock, String registryname) {
		this.registerBlockWithCustomItem(block, iBlock, registryname);
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
	public void _registerItem(Item item, String registryname) {
		this.registerItem(item, registryname);
		registerItemModel(item);
	}

	@Override
	public <T extends IMetaLookup> void _registerItemWithVariants(Item item, String registryname, T variant) {
		this.registerItemWithVariants(item, registryname, variant);
		String variantName = variant.getID();
		List<ItemStack> subItems = new ArrayList<ItemStack>();
		item.getSubItems(item, CreativeTabs.SEARCH, subItems);
		for (ItemStack stack : subItems) {
			registerItemModelForMeta(item, stack.getMetadata(), variantName + "=" + variant.getByOrdinal(stack.getMetadata()));
		}
	}

	private void registerItemModel(Item item) {
		registerItemModel(item, item.getRegistryName().toString());
	}

	private void registerItemModel(Item item, String modelLocation) {
		final ModelResourceLocation fullModelLocation = new ModelResourceLocation(modelLocation, "inventory");
		registerItemModel(item, fullModelLocation);
	}

	private void registerItemModel(Item item, final ModelResourceLocation fullModelLocation) {
		ModelBakery.registerItemVariants(item, fullModelLocation); // Ensure the custom model is loaded and prevent the default model from being loaded
		//registerItemModel(item, MeshDefinitionFix.create(stack -> fullModelLocation));
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
	    {
	        public ModelResourceLocation getModelLocation(ItemStack stack)
	        {
	            return fullModelLocation;
	        }
	    });
	}/**/

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