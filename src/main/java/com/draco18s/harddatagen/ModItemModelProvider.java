package com.draco18s.harddatagen;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.hardlib.HardLib;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class ModItemModelProvider extends ItemModelProvider {
	/** One provider per mod; block items are made by ModBlockStateProvider. */
	public ModItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
		super(output, modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		switch(modid) {
			case HarderFarming.MODID -> farming();
			case HarderOres.MODID -> harderOres();
			case OreFlowers.MODID -> simpleItem(OreFlowers.ModItems.sticky_goo);
			default -> { }
		}
	}

	private void farming() {
		handheldItem(HarderFarming.ModItems.butcher_knife);
		simpleItem(HarderFarming.ModItems.tinydust_sugar, getRL("sugar_dust_sm"));
		simpleItem(HarderFarming.ModItems.tinydust_flour, getRL("flour_dust_sm"));
		simpleItem(HarderFarming.ModItems.largedust_flour, getRL("flour_dust_lg"));
		simpleItem(HarderFarming.ModItems.winter_wheat_seeds);
		simpleItem(HarderFarming.ModItems.salt);
		simpleItem(HarderFarming.ModItems.salt_chunk);
		simpleItem(HarderFarming.ModItems.weed_seeds, getRL("seeds_weeds"));
		simpleItem(HarderFarming.ModItems.raw_leather, getRL("leather"));
	}

	private void harderOres() {
		simpleItem(HarderOres.ModItems.diamond_studded_pick, getRL("diamond_stud_pickaxe"));
		simpleItem(HarderOres.ModItems.diamond_studded_axe, getRL("diamond_stud_axe"));
		simpleItem(HarderOres.ModItems.diamond_studded_shovel, getRL("diamond_stud_shovel"));
		simpleItem(HarderOres.ModItems.diamond_studded_hoe, getRL("diamond_stud_hoe"));

		simpleItem(HarderOres.ModItems.copper_nugget);
		simpleItem(HarderOres.ModItems.orechunk_limonite, getRL("limonite_chunk"));
		simpleItem(HarderOres.ModItems.orechunk_diamond, getRL("diamond_chunk"));
		simpleItem(HarderOres.ModItems.tinydust_copper, getRL("copper_dust_sm"));
		simpleItem(HarderOres.ModItems.largedust_copper, getRL("copper_dust_lg"));
		simpleItem(HarderOres.ModItems.tinydust_gold, getRL("gold_dust_sm"));
		simpleItem(HarderOres.ModItems.largedust_gold, getRL("gold_dust_lg"));
		simpleItem(HarderOres.ModItems.tinydust_iron, getRL("iron_dust_sm"));
		simpleItem(HarderOres.ModItems.largedust_iron, getRL("iron_dust_lg"));
		simpleItem(HarderOres.ModItems.sluice, getRL("basic_sluice"));
	}

	private ResourceLocation getRL(String name) {
		return HardLib.getRL("unknown", name);
	}

	private ItemModelBuilder simpleItem(Item item) {
		ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
		return withExistingParent(itemName.toString(),
				HardLib.getRL("minecraft", "item/generated")).texture("layer0",
				HardLib.getRL(itemName.getNamespace(), ITEM_FOLDER + "/" +  itemName.getPath()));
	}

	private ItemModelBuilder simpleItem(Item item, ResourceLocation texture) {
		ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
		return withExistingParent(itemName.toString(),
				HardLib.getRL("minecraft", "item/generated")).texture("layer0",
				HardLib.getRL(itemName.getNamespace(), ITEM_FOLDER + "/" +  texture.getPath()));
	}

	private ItemModelBuilder handheldItem(Item item) {
		ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
		return withExistingParent(itemName.toString(),
				HardLib.parseRL("item/handheld")).texture("layer0",
				HardLib.getRL(itemName.getNamespace(), ITEM_FOLDER + "/" + itemName.getPath()));
	}

	private ItemModelBuilder handheldItem(Item item, ResourceLocation texture) {
		ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
		return withExistingParent(itemName.toString(),
				HardLib.parseRL("item/handheld")).texture("layer0",
				HardLib.getRL(itemName.getNamespace(), ITEM_FOLDER + "/" + itemName.getPath()));
	}
}
