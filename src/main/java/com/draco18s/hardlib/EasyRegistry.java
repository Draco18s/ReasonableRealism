package com.draco18s.hardlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.state.IProperty;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class EasyRegistry {
	private static List<Block> blocksToReg = new ArrayList<Block>();
	private static List<Item>  itemsToReg  = new ArrayList<Item>();
	private static List<TileEntityType<?>> tilesToReg = new ArrayList<TileEntityType<?>>();
	private static List<IForgeRegistryEntry<?>> otherItems = new ArrayList<IForgeRegistryEntry<?>>();
	private static Method CriterionRegister;
	
	public static void registerBlock(Block block, String registryname) {
		block.setRegistryName(registryname);
		blocksToReg.add(block);
	}
	
	public static void registerBlock(Block block, String registryname, Item.Properties props) {
		BlockItem iBlock = new BlockItem(block, props);
		block.setRegistryName(registryname);
		iBlock.setRegistryName(block.getRegistryName());
		
		blocksToReg.add(block);
		itemsToReg.add(iBlock);
	}
	
	public static void registerBlockWithVariants(Block block, String registryname, IProperty<?> prop, Item.Properties props) {
		block.setRegistryName(registryname);
		blocksToReg.add(block);
		Collection<?> col = prop.getAllowedValues();
		for(Object o : col) {
			BlockItem iBlock = new BlockItem(block, props);
			iBlock.setRegistryName(block.getRegistryName().getNamespace(),block.getRegistryName().getPath()+"_"+o.toString());
			itemsToReg.add(iBlock);
		}
	}
	
	public static void registerBlock(Block block, String registryname, BlockItem iBlock) {
		block.setRegistryName(registryname);
		iBlock.setRegistryName(block.getRegistryName());
		blocksToReg.add(block);
		itemsToReg.add(iBlock);
	}

	public static void registerItem(Item item, String registryname) {
		item.setRegistryName(registryname);
		itemsToReg.add(item);
	}

	public static void registerTileEntity(TileEntityType<?> teType, String registryname) {
		teType.setRegistryName(registryname);
		tilesToReg.add(teType);
	}
	
	public static <K extends IForgeRegistryEntry<K>> void registerOther(K object) {
		otherItems.add(object);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ICriterionInstance> ICriterionTrigger<T> registerAdvancementTrigger(ICriterionTrigger<T> trigger) {
		if(CriterionRegister == null) {
			CriterionRegister = ObfuscationReflectionHelper.findMethod(CriteriaTriggers.class, "register", ICriterionTrigger.class);
			CriterionRegister.setAccessible(true);
		}
		try {
			trigger = (ICriterionTrigger<T>) CriterionRegister.invoke(null, trigger);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			System.out.println("Failed to register trigger " + trigger.getId() + "!");
			e.printStackTrace();
		}
		return trigger;
	}
	
	@EventBusSubscriber(modid = HardLib.MODID, bus = EventBusSubscriber.Bus.MOD)
	private static class Registration {
		@SubscribeEvent
		public static void onRegisterBlocks(final RegistryEvent.Register<Block> event) {
			// Register all your blocks inside this registerAll call
			event.getRegistry().registerAll(
				blocksToReg.toArray(new Block[0])
			);
			HardLib.LOGGER.debug("Registered Blocks");
		}
	
		@SubscribeEvent
		public static void onModConfigEvent(final ModConfig.ModConfigEvent event) {
			//final ModConfig config = event.getConfig();
			// Rebake the configs when they change
			//if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
			//	ConfigHelper.bakeClient(config);
			//} else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
			//	ConfigHelper.bakeServer(config);
			//}
		}
	
		@SubscribeEvent
		public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			registry.registerAll(
				itemsToReg.toArray(new Item[0])
			);
			HardLib.LOGGER.debug("Registered Items");
		}
	
		@SubscribeEvent
		public static void onRegister(@Nonnull final RegistryEvent.Register<TileEntityType<?>> event) {
			// Register your TileEntities here if you have them
			event.getRegistry().registerAll(
				tilesToReg.toArray(new TileEntityType<?>[0])
			);
			HardLib.LOGGER.debug("Registered TileEntitys");
		}
		
		@SubscribeEvent
		public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
			for(IForgeRegistryEntry<?> e : otherItems) {
				if(e instanceof Enchantment)
					event.getRegistry().register((Enchantment)e);
			}
		}
	}
}
