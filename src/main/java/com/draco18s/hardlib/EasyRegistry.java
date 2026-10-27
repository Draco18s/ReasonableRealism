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
import com.draco18s.hardlib.util.Tuple;

import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;

public class EasyRegistry {
	private static List<Tuple<Identifier, Supplier<Block>>> blocksToReg = Collections.synchronizedList(new ArrayList<Tuple<Identifier, Supplier<Block>>>());
	private static List<Tuple<Identifier, Supplier<Item>>> itemsToReg = Collections.synchronizedList(new ArrayList<Tuple<Identifier, Supplier<Item>>>());
	private static List<Tuple<Identifier, Supplier<BlockEntityType<?>>>> tilesToReg = Collections.synchronizedList(new ArrayList<Tuple<Identifier,Supplier<BlockEntityType<?>>>>());
	private static List<Tuple<Identifier, Supplier<MenuType<?>>>> menusToReg = Collections.synchronizedList(new ArrayList<Tuple<Identifier, Supplier<MenuType<?>>>>());
	private static Map<ResourceKey<Registry<?>>, List<Tuple<Identifier, Supplier<?>>>> miscReg = Collections.synchronizedMap(new HashMap<ResourceKey<Registry<?>>, List<Tuple<Identifier,Supplier<?>>>>());
	private static List<DeferredHolder<Item, Item>> regItems = Collections.synchronizedList(new ArrayList<DeferredHolder<Item, Item>>());
	
	static void registerEventBus(IEventBus modEventBus) {
		modEventBus.register(EasyRegistry.class);
	}

	/*
	 * Since 1.21.2 blocks and items know their id from construction (it is in their properties), so these take a
	 * factory given the properties with the id set, like NeoForge's DeferredRegister does.
	 */

	/** The properties, with its id set, that a block registered under this name is made with. */
	private static Supplier<Block> block(Function<BlockBehaviour.Properties, ? extends Block> block, Supplier<BlockBehaviour.Properties> props, Identifier registryname) {
		return () -> block.apply(props.get().setId(ResourceKey.create(Registries.BLOCK, registryname)));
	}

	/** Same for an item; block items name themselves after their block. */
	private static Item.Properties itemProps(Item.Properties props, Identifier registryname, boolean blockItem) {
		props.setId(ResourceKey.create(Registries.ITEM, registryname));
		return blockItem ? props.useBlockDescriptionPrefix() : props;
	}

	public static DeferredHolder<Block, Block> registerBlock(Function<BlockBehaviour.Properties, ? extends Block> block, Identifier registryname) {
		return registerBlock(block, BlockBehaviour.Properties::of, registryname);
	}

	/** A block made from other properties than empty ones (a copy of another block's, say). */
	public static DeferredHolder<Block, Block> registerBlock(Function<BlockBehaviour.Properties, ? extends Block> block, Supplier<BlockBehaviour.Properties> props, Identifier registryname) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		blocksToReg.add(new Tuple<Identifier, Supplier<Block>>(registryname, block(block, props, registryname)));
		return ret;
	}

	public static DeferredHolder<Block, Block> registerBlock(Function<BlockBehaviour.Properties, ? extends Block> block, Identifier registryname, Item.Properties props) {
		return registerBlock(block, BlockBehaviour.Properties::of, registryname, props);
	}

	public static DeferredHolder<Block, Block> registerBlock(Function<BlockBehaviour.Properties, ? extends Block> block, Supplier<BlockBehaviour.Properties> blockProps, Identifier registryname, Item.Properties props) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		blocksToReg.add(new Tuple<Identifier, Supplier<Block>>(registryname, block(block, blockProps, registryname)));
		itemsToReg.add(new Tuple<Identifier, Supplier<Item>>(registryname, ()-> new BlockItem(ret.get(), itemProps(props, registryname, true))));
		return ret;
	}

	public static <T extends BlockItem,U extends Comparable<U>> void registerBlockWithVariants(Function<BlockBehaviour.Properties, ? extends Block> block, Identifier registryname, Property<U> prop, IBlockItemFactory<T> blockItemCtor, Item.Properties props) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		blocksToReg.add(new Tuple<Identifier, Supplier<Block>>(registryname, block(block, BlockBehaviour.Properties::of, registryname)));
		itemsToReg.add(new Tuple<Identifier, Supplier<Item>>(registryname, () -> blockItemCtor.create(ret.get(), itemProps(props, registryname, true))));
	}

	public interface IBlockItemFactory<T extends BlockItem> {
		T create(Block block, Item.Properties props);
	}

	public static void registerBlock(Function<BlockBehaviour.Properties, ? extends Block> block, Identifier registryname, IBlockItemFactory<?> iBlock) {
		DeferredHolder<Block, Block> ret = DeferredHolder.create(Registries.BLOCK, registryname);
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		blocksToReg.add(new Tuple<Identifier, Supplier<Block>>(registryname, block(block, BlockBehaviour.Properties::of, registryname)));
		itemsToReg.add(new Tuple<Identifier, Supplier<Item>>(registryname, () -> iBlock.create(ret.get(), itemProps(new Item.Properties(), registryname, true))));
	}

	public static void registerItem(Function<Item.Properties, ? extends Item> item, Identifier registryname) {
		registerItem(item, registryname, new Item.Properties());
	}

	public static void registerItem(Function<Item.Properties, ? extends Item> item, Identifier registryname, Item.Properties props) {
		regItems.add(DeferredHolder.create(Registries.ITEM, registryname));
		itemsToReg.add(new Tuple<Identifier, Supplier<Item>>(registryname, () -> item.apply(itemProps(props, registryname, false))));
	}

	public static void registerTileEntity(Supplier<BlockEntityType<?>> create, Identifier registryname) {
		tilesToReg.add(new Tuple<Identifier, Supplier<BlockEntityType<?>>>(registryname, create));
	}
	
	/**
	 * Registers an advancement criterion trigger (triggers are a registry since 1.20.2), returning it for the caller
	 * to keep. Call from a mod constructor, like the other registrations.
	 */
	public static <T extends CriterionTrigger<?>> T registerAdvancementTrigger(Identifier id, T trigger) {
		registerOther(Registries.TRIGGER_TYPE, new Tuple<Identifier, Supplier<CriterionTrigger<?>>>(id, () -> trigger));
		return trigger;
	}
	
	/** Registers a creative tab (tabs are a registry since 1.20); the builder only needs its title, icon and items. */
	public static DeferredHolder<CreativeModeTab, CreativeModeTab> registerCreativeTab(Identifier id, Consumer<CreativeModeTab.Builder> tab) {
		return registerOther(Registries.CREATIVE_MODE_TAB, new Tuple<Identifier, Supplier<CreativeModeTab>>(id, () -> {
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
	public static <T> DeferredHolder<T, T> registerOther(ResourceKey<Registry<T>> registryKey, Tuple<Identifier, Supplier<T>> supplier) {
		// Mods are constructed in parallel, so two can get here at once: make (and fill) each list one at a time.
		synchronized(miscReg) {
			if(!miscReg.containsKey(registryKey)) {
				List<Tuple<Identifier, Supplier<T>>> list = Collections.synchronizedList(new ArrayList<Tuple<Identifier, Supplier<T>>>());
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
		}
		DeferredHolder<T, T> ret = DeferredHolder.create(registryKey, supplier.getA());
		return ret;
	}

	public static void registerMenuType(Identifier registryKey, Supplier<MenuType<?>> supplier) {
		menusToReg.add(new Tuple<Identifier, Supplier<MenuType<?>>>(registryKey,supplier));
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

	private static <T> void register(RegisterEvent event, ResourceKey<? extends Registry<T>> key, List<Tuple<Identifier, Supplier<T>>> entries) {
		for(Tuple<Identifier, Supplier<T>> entry : entries) {
			event.register(key, entry.getA(), entry.getB());
		}
	}

	/** Fields marked @ObjectHolder, by registry id (from the mods' scan data). */
	private static Map<Identifier, List<ModFileScanData.AnnotationData>> objectHolders;

	/** Fills the @ObjectHolder fields for the registry just registered (everything of ours is in it by now). */
	private static void fillObjectHolders(RegisterEvent event) {
		if(objectHolders == null) {
			objectHolders = new HashMap<Identifier, List<ModFileScanData.AnnotationData>>();
			for(ModFileScanData scan : ModList.get().getAllScanData()) {
				scan.getAnnotatedBy(ObjectHolder.class, ElementType.FIELD).forEach(holder -> objectHolders
						.computeIfAbsent(HardLib.parseRL((String)holder.annotationData().get("registryName")), k -> new ArrayList<ModFileScanData.AnnotationData>())
						.add(holder));
			}
		}
		Registry<?> registry = event.getRegistry();
		for(ModFileScanData.AnnotationData holder : objectHolders.getOrDefault(event.getRegistryKey().identifier(), List.of())) {
			Identifier id = HardLib.parseRL((String)holder.annotationData().get("value"));
			String field = holder.clazz().getClassName() + "." + holder.memberName();
			if(!registry.containsKey(id)) {
				HardLib.LOGGER.warn("Object holder {} names {}, which isn't registered in {}", field, id, registry.key().identifier());
				continue;
			}
			try {
				Class.forName(holder.clazz().getClassName(), true, EasyRegistry.class.getClassLoader())
						.getField(holder.memberName()).set(null, registry.getValue(id));
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
			// Tools are items with the tool component now.
			regItems.forEach(itm -> {
				if(itm.get().components().has(DataComponents.TOOL))
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
