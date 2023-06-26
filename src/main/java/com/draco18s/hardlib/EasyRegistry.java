package com.draco18s.hardlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Lifecycle;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

public class EasyRegistry {
	private static List<Tuple<ResourceLocation, Supplier<Block>>> blocksToReg = new ArrayList<Tuple<ResourceLocation, Supplier<Block>>>();
	private static List<Tuple<ResourceLocation, Supplier<Item>>> itemsToReg = new ArrayList<Tuple<ResourceLocation, Supplier<Item>>>();
	private static List<Tuple<ResourceLocation, Supplier<BlockEntityType<?>>>> tilesToReg = new ArrayList<Tuple<ResourceLocation,Supplier<BlockEntityType<?>>>>();
	private static List<Tuple<ResourceLocation, Supplier<MenuType<?>>>> menusToReg = new ArrayList<Tuple<ResourceLocation, Supplier<MenuType<?>>>>();
	private static HashMap<ResourceKey<Registry<?>>, List<Tuple<ResourceLocation, Supplier<?>>>> miscReg = new HashMap<ResourceKey<Registry<?>>, List<Tuple<ResourceLocation,Supplier<?>>>>();
	private static List<RegistryObject<Item>> regItems = new ArrayList<RegistryObject<Item>>();
	private static Method CriterionRegister;
	
	static void registerEventBus(IEventBus modEventBus) {
		modEventBus.register(EasyRegistry.class);
	}

	public static RegistryObject<Block> registerBlock(Supplier<Block> block, ResourceLocation registryname) {
		RegistryObject<Block> ret = RegistryObject.createOptional(registryname, ForgeRegistries.Keys.BLOCKS, registryname.getNamespace());
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		return ret;
	}
	
	public static RegistryObject<Block> registerBlock(Supplier<Block> block, ResourceLocation registryname, Item.Properties props) {
		RegistryObject<Block> ret = RegistryObject.createOptional(registryname, ForgeRegistries.Keys.BLOCKS, registryname.getNamespace());
		regItems.add(RegistryObject.createOptional(registryname, ForgeRegistries.Keys.ITEMS, registryname.getNamespace()));
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, ()-> new BlockItem(ret.get(), props)));
		return ret;
	}
	
	public static <T extends BlockItem,U extends Comparable<U>> void registerBlockWithVariants(Supplier<Block> block, ResourceLocation registryname, Property<U> prop, IBlockItemFactory<T> blockItemCtor, Item.Properties props) {
		RegistryObject<Block> ret = RegistryObject.createOptional(registryname, ForgeRegistries.Keys.BLOCKS, registryname.getNamespace());
		regItems.add(RegistryObject.createOptional(registryname, ForgeRegistries.Keys.ITEMS, registryname.getNamespace()));
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, () -> blockItemCtor.create(ret.get(), props)));
	}
	
	public interface IBlockItemFactory<T extends BlockItem> {
		T create(Block block, Item.Properties props);
	}
	
	public static void registerBlock(Supplier<Block> block, ResourceLocation registryname, Function<Block,BlockItem> iBlock) {
		RegistryObject<Block> ret = RegistryObject.createOptional(registryname, ForgeRegistries.Keys.BLOCKS, registryname.getNamespace());
		regItems.add(RegistryObject.createOptional(registryname, ForgeRegistries.Keys.ITEMS, registryname.getNamespace()));
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, () -> iBlock.apply(ret.get())));
	}
	
	public static void registerItem(Supplier<Item> item, ResourceLocation registryname) {
		regItems.add(RegistryObject.createOptional(registryname, ForgeRegistries.Keys.ITEMS, registryname.getNamespace()));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, item));
	}
	
	public static void registerTileEntity(Supplier<BlockEntityType<?>> create, ResourceLocation registryname) {
		tilesToReg.add(new Tuple<ResourceLocation, Supplier<BlockEntityType<?>>>(registryname, create));
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends CriterionTrigger<?>> T registerAdvancementTrigger(T trigger) {
		if(CriterionRegister == null) {
			//Class<?> persistentClass = trigger.getClass().getGenericSuperclass().getClass();
			CriterionRegister = ObfuscationReflectionHelper.findMethod(CriteriaTriggers.class, "register", CriterionTrigger.class);
			CriterionRegister.setAccessible(true);
		}
		try {
			trigger = (T) CriterionRegister.invoke(null, trigger);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Failed to register trigger " + trigger.getId() + "!");
			e.printStackTrace();
		}
		return trigger;
	}
	
	// I used to think that I was bold
	// I used to think love would be fun
	// Now all the stories have been told
	// Except for one
	@SuppressWarnings({ "unchecked" })
	public static <T> RegistryObject<T> registerOther(ResourceKey<Registry<T>> registryKey, Tuple<ResourceLocation, Supplier<T>> supplier) {
		if(!miscReg.containsKey(registryKey)) {
			List<Tuple<ResourceLocation, Supplier<T>>> list = new ArrayList<Tuple<ResourceLocation, Supplier<T>>>();
			// As the stars start to align
			// I hope you'll take it as a sign
			// Th'chu will be okay
			// Everything will be okay
			@SuppressWarnings("rawtypes")
			List t = list;
			// And if the seven hells collapse
			// Although the day could be my last
			// You will be okay
			// When I'm gone, you'll be okay
			@SuppressWarnings("rawtypes")
			ResourceKey r = registryKey;
			miscReg.put(r, t);
		}
		// And when creation goes to die
		// You can find me in the sky
		// Upon the last day
		// And you will be okay
		@SuppressWarnings("rawtypes")
		Tuple s = supplier;
		miscReg.get(registryKey).add(s);
		RegistryObject<T> ret = RegistryObject.createOptional(supplier.getA(), registryKey, supplier.getA().getNamespace());
		return ret;
	}

	public static void registerMenuType(ResourceLocation registryKey, Supplier<MenuType<?>> supplier) {
		menusToReg.add(new Tuple<ResourceLocation, Supplier<MenuType<?>>>(registryKey,supplier));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SubscribeEvent
	public static void onRegisterEvent(final RegisterEvent event) {
		@NotNull ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
		if (key.equals(ForgeRegistries.Keys.BLOCKS)){
			for(var it : blocksToReg) {
				event.getForgeRegistry().register(it.getA(), it.getB().get());
			}
			return;
		}
		if (key.equals(ForgeRegistries.Keys.ITEMS)){
			for(var it : itemsToReg) {
				event.getForgeRegistry().register(it.getA(), it.getB().get());
			}
			return;
		}
		if(key.equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES)) {
			for(var it : tilesToReg) {
				event.getForgeRegistry().register(it.getA(), it.getB().get());
			}
			return;
		}
		if(key.equals(ForgeRegistries.Keys.MENU_TYPES)) {
			for(var it : menusToReg) {
				event.getForgeRegistry().register(it.getA(), it.getB().get());
			}
			return;
		}
		if(miscReg.containsKey(key)) {
			boolean vanilla = (event.getForgeRegistry() == null);
			List<Tuple<ResourceLocation, Supplier<?>>> list = miscReg.get(key);
			for(Tuple<ResourceLocation, Supplier<?>> l : list) {
				//vanilla is such a whore
				if(vanilla) {
					WritableRegistry reg = (WritableRegistry)event.getVanillaRegistry();
					ResourceKey kk = ResourceKey.create(reg.key(), l.getA());
					reg.register(kk , l.getB().get(), Lifecycle.stable());
				}
				else {
					event.getForgeRegistry().register(l.getA(), l.getB().get());
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void addItemsToCreativeTab(final CreativeModeTabEvent.BuildContents event) {
		if(event.getTab() != CreativeModeTabs.SEARCH) return;
		regItems.forEach(itm -> {
			event.accept(itm);
		});
		if(event.getTab() != CreativeModeTabs.TOOLS_AND_UTILITIES) return;
		regItems.forEach(itm -> {
			if(itm.get() instanceof TieredItem)
				event.accept(itm);
		});
	}
}
