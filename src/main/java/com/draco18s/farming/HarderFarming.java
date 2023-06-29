package com.draco18s.farming;

import java.util.function.Supplier;

import com.draco18s.farming.block.CropWinterWheatBlock;
import com.draco18s.farming.item.WinterSeedsItem;
import com.draco18s.farming.loot.GrassLootModifier;
import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.loot.TemperatureCheck;
import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

@Mod(HarderFarming.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class HarderFarming {
	public static final String MODID = "harderfarming";
	
	public HarderFarming() {
		RegistryObject<Block> wheat = EasyRegistry.registerBlock(CropWinterWheatBlock::new, getRL("crop_winter_wheat"));
		EasyRegistry.registerItem(() -> new WinterSeedsItem(wheat.get()), getRL("winter_wheat_seeds"));
		EasyRegistry.registerOther(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, 
			new Tuple<ResourceLocation,Supplier<Codec<? extends IGlobalLootModifier>>>(getRL("grass_modifier"),GrassLootModifier.CODEC::get)	
		);
		ModLootConditionTypes.can_snow = EasyRegistry.registerOther(Registries.LOOT_CONDITION_TYPE, new Tuple<ResourceLocation,Supplier<LootItemConditionType>>(
				getRL("can_snow"),() -> new LootItemConditionType(new TemperatureCheck.Serializer())));
		
		String[] itemNames = {
				"tinydust_sugar",
				"tinydust_flour", "largedust_flour",
		};
		for(String it : itemNames) {
			EasyRegistry.registerItem(() -> new Item(new Item.Properties()), getRL(it));
		}
	}
	
	public static class ModLootConditionTypes {
		public static RegistryObject<LootItemConditionType> can_snow = null;
	}
	
	public static class ModBlocks {
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"crop_winter_wheat")
		public static final Block crop_winter_wheat = null;
	}
	
	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"winter_wheat_seeds")
		public static final Item winter_wheat_seeds = null;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_sugar")
		public static final Item tinydust_sugar = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"tinydust_flour")
		public static final Item tinydust_flour = null;

		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"largedust_flour")
		public static final Item largedust_flour = null;
	}
	
	@SubscribeEvent
	public static void addItemsToCreativeTab(final CreativeModeTabEvent.BuildContents event) {
		CreativeModeTab tab = event.getTab();
		if(tab == CreativeModeTabs.FOOD_AND_DRINKS) {
			event.accept(ModItems.tinydust_sugar);
			event.accept(ModItems.tinydust_flour);
			event.accept(ModItems.largedust_flour);
		}
	}

	private static ResourceLocation getRL(String name) {
		return new ResourceLocation(MODID, name);
	}
}
