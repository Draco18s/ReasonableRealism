package com.draco18s.hardlib;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.draco18s.hardlib.api.ObjectHolder;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;

public class EasyRegistry {
	private static List<Tuple<ResourceLocation, Supplier<Block>>> blocksToReg = Collections.synchronizedList(new ArrayList<Tuple<ResourceLocation, Supplier<Block>>>());
	private static List<Tuple<ResourceLocation, Supplier<Item>>> itemsToReg = Collections.synchronizedList(new ArrayList<Tuple<ResourceLocation, Supplier<Item>>>());
	private static List<Tuple<ResourceLocation, Supplier<BlockEntityType<?>>>> tilesToReg = Collections.synchronizedList(new ArrayList<Tuple<ResourceLocation,Supplier<BlockEntityType<?>>>>());
	private static List<Tuple<ResourceLocation, Supplier<MenuType<?>>>> menusToReg = Collections.synchronizedList(new ArrayList<Tuple<ResourceLocation, Supplier<MenuType<?>>>>());
	private static Map<ResourceKey<Registry<?>>, List<Tuple<ResourceLocation, Supplier<?>>>> miscReg = Collections.synchronizedMap(new HashMap<ResourceKey<Registry<?>>, List<Tuple<ResourceLocation,Supplier<?>>>>());
	private static List<DeferredHolder<Item, Item>> regItems = Collections.synchronizedList(new ArrayList<DeferredHolder<Item, Item>>());
	
	static void registerEventBus(IEventBus modEventBus) {
		modEventBus.register(EasyRegistry.class);
	}

	public static DeferredHolder<Block, Block> registerBlock(Supplier<Block> block, ResourceLocation registryname) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		return ret;
	}
	
	public static DeferredHolder<Block, Block> registerBlock(Supplier<Block> block, ResourceLocation registryname, Item.Properties props) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, ()-> new BlockItem(ret.get(), props)));
		return ret;
	}
	
	public static <T extends BlockItem,U extends Comparable<U>> void registerBlockWithVariants(Supplier<Block> block, ResourceLocation registryname, Property<U> prop, IBlockItemFactory<T> blockItemCtor, Item.Properties props) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, () -> blockItemCtor.create(ret.get(), props)));
	}
	
	public interface IBlockItemFactory<T extends BlockItem> {
		T create(Block block, Item.Properties props);
	}
	
	public static void registerBlock(Supplier<Block> block, ResourceLocation registryname, Function<Block,BlockItem> iBlock) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		blocksToReg.add(new Tuple<ResourceLocation, Supplier<Block>>(registryname, block));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, () -> iBlock.apply(ret.get())));
	}
	
	public static void registerItem(Supplier<Item> item, ResourceLocation registryname) {
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		itemsToReg.add(new Tuple<ResourceLocation, Supplier<Item>>(registryname, item));
	}
	
	public static void registerTileEntity(Supplier<BlockEntityType<?>> create, ResourceLocation registryname) {
		tilesToReg.add(new Tuple<ResourceLocation, Supplier<BlockEntityType<?>>>(registryname, create));
	}
	
	/**
	 * Registers an advancement criterion trigger (triggers are a registry since 1.20.2), returning it for the caller
	 * to keep. Call from a mod constructor, like the other registrations.
	 */
	public static <T extends CriterionTrigger<?>> T registerAdvancementTrigger(ResourceLocation id, T trigger) {
		registerOther(Registries.TRIGGER_TYPE, new Tuple<ResourceLocation, Supplier<CriterionTrigger<?>>>(id, () -> trigger));
		return trigger;
	}
	
	/** Registers a creative tab (tabs are a registry since 1.20); the builder only needs its title, icon and items. */
	public static DeferredHolder<CreativeModeTab, CreativeModeTab> registerCreativeTab(ResourceLocation id, Consumer<CreativeModeTab.Builder> tab) {
		return registerOther(Registries.CREATIVE_MODE_TAB, new Tuple<ResourceLocation, Supplier<CreativeModeTab>>(id, () -> {
			CreativeModeTab.Builder builder = CreativeModeTab.builder();
			tab.accept(builder);
			return builder.build();
		}));
	}

	// I used to think that I was bold
	// I used to think love would be fun
	// Now all the stories have been told
	// Except for one
	@SuppressWarnings({ "unchecked" })
	public static <T> DeferredHolder<T, T> registerOther(ResourceKey<Registry<T>> registryKey, Tuple<ResourceLocation, Supplier<T>> supplier) {
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
		DeferredHolder<T, T> ret = DeferredHolder.create(registryKey, supplier.getA());
		return ret;
	}

	public static void registerMenuType(ResourceLocation registryKey, Supplier<MenuType<?>> supplier) {
		menusToReg.add(new Tuple<ResourceLocation, Supplier<MenuType<?>>>(registryKey,supplier));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SubscribeEvent
	public static void onRegisterEvent(final RegisterEvent event) {
		ResourceKey<? extends Registry<?>> key = event.getRegistryKey();
		if (key.equals(Registries.BLOCK)) {
			register(event, Registries.BLOCK, blocksToReg);
		}
		else if (key.equals(Registries.ITEM)) {
			register(event, Registries.ITEM, itemsToReg);
		}
		else if (key.equals(Registries.BLOCK_ENTITY_TYPE)) {
			register(event, Registries.BLOCK_ENTITY_TYPE, tilesToReg);
		}
		else if (key.equals(Registries.MENU)) {
			register(event, Registries.MENU, menusToReg);
		}
		else if (miscReg.containsKey(key)) {
			register(event, (ResourceKey)key, (List)miscReg.get(key));
		}
		fillObjectHolders(event);
	}

	private static <T> void register(RegisterEvent event, ResourceKey<? extends Registry<T>> key, List<Tuple<ResourceLocation, Supplier<T>>> entries) {
		for(Tuple<ResourceLocation, Supplier<T>> entry : entries) {
			event.register(key, entry.getA(), entry.getB());
		}
	}

	/** Fields marked @ObjectHolder, by registry id (from the mods' scan data). */
	private static Map<ResourceLocation, List<ModFileScanData.AnnotationData>> objectHolders;

	/** Fills the @ObjectHolder fields for the registry just registered (everything of ours is in it by now). */
	private static void fillObjectHolders(RegisterEvent event) {
		if(objectHolders == null) {
			objectHolders = new HashMap<ResourceLocation, List<ModFileScanData.AnnotationData>>();
			for(ModFileScanData scan : ModList.get().getAllScanData()) {
				scan.getAnnotatedBy(ObjectHolder.class, ElementType.FIELD).forEach(holder -> objectHolders
						.computeIfAbsent(HardLib.parseRL((String)holder.annotationData().get("registryName")), k -> new ArrayList<ModFileScanData.AnnotationData>())
						.add(holder));
			}
		}
		Registry<?> registry = event.getRegistry();
		for(ModFileScanData.AnnotationData holder : objectHolders.getOrDefault(event.getRegistryKey().location(), List.of())) {
			ResourceLocation id = HardLib.parseRL((String)holder.annotationData().get("value"));
			String field = holder.clazz().getClassName() + "." + holder.memberName();
			if(!registry.containsKey(id)) {
				HardLib.LOGGER.warn("Object holder {} names {}, which isn't registered in {}", field, id, registry.key().location());
				continue;
			}
			try {
				Class.forName(holder.clazz().getClassName(), true, EasyRegistry.class.getClassLoader())
						.getField(holder.memberName()).set(null, registry.get(id));
			}
			catch(ReflectiveOperationException e) {
				throw new IllegalStateException("Couldn't fill object holder " + field, e);
			}
		}
	}

	@SubscribeEvent
	public static void addItemsToCreativeTab(final BuildCreativeModeTabContentsEvent event) {
		// (The search tab fills itself from the other tabs.)
		if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			regItems.forEach(itm -> {
				if(itm.get() instanceof TieredItem)
					event.accept(itm.get());
			});
			/*miscReg.forEach((reg,list) -> {
				if(reg.equals(ForgeRegistries.Keys.ENCHANTMENTS)) {
					list.forEach(msc -> {
						Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(msc.getA());
						for(int lv=1;lv <= ench.getMaxLevel(); lv++) {
							ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
							EnchantmentInstance enchInst = new EnchantmentInstance(ench, lv);
							EnchantedBookItem.addEnchantment(stack, enchInst);
							event.accept(stack);
						}
					});
				}
			});*/
		}
	}
}
