package com.draco18s.hardlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.datafixers.types.Type;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
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

	public static <T extends BlockItem,U extends Comparable<U>> void registerBlockWithVariants(Block block, String registryname, IProperty<U> prop, IBlockItemFactory<T,U> blockItemCtor, Item.Properties props) {
		block.setRegistryName(registryname);
		blocksToReg.add(block);
		Collection<U> col = prop.getAllowedValues();
		for(U o : col) {
			BlockItem iBlock = blockItemCtor.create(block, o, props);
			iBlock.setRegistryName(block.getRegistryName().getNamespace(),block.getRegistryName().getPath()+"_"+o.toString());
			itemsToReg.add(iBlock);
		}
	}

	public interface IBlockItemFactory<T extends BlockItem, U> {
		T create(Block block, U value, Item.Properties props);
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

	public static void registerTileEntity(Builder<?> create, String domain, String registryname) {
		tilesToReg.add(build(domain, registryname,create));
	}

	public static <K extends IForgeRegistryEntry<K>> void registerOther(K object, ResourceLocation registryname) {
		otherItems.add(object.setRegistryName(registryname));
	}
	
	/*public static <T extends ILootCondition> void registerLootCondition(ILootCondition.AbstractSerializer<? extends T> object) {
		LootConditionManager.registerCondition(object);
	}*/

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

	//private static int DATA_VERSION = 103;

	private static <T extends TileEntity> TileEntityType<T> build(final String domain, final String name, final TileEntityType.Builder<T> builder) {
		final ResourceLocation registryName = new ResourceLocation(domain,name);

		Type<?> dataFixerType = null;

		/*try {
			dataFixerType = DataFixesManager.getDataFixer()
					.getSchema(DataFixUtils.makeKey(DATA_VERSION))
					.getChoiceType(TypeReferences.BLOCK_ENTITY, registryName.toString());
		} catch (final IllegalArgumentException e) {
			if (SharedConstants.developmentMode) {
				throw e;
			}

			HardLib.LOGGER.warn("No data fixer registered for TileEntity {}", registryName);
		}*/

		//@SuppressWarnings("ConstantConditions")
		// dataFixerType will always be null until mod data fixers are implemented
		final TileEntityType<T> tileEntityType = builder.build(dataFixerType);
		tileEntityType.setRegistryName(registryName);

		return tileEntityType;
	}

	@EventBusSubscriber(modid = HardLib.MODID, bus = EventBusSubscriber.Bus.MOD)
	private static class Registration {
		@SubscribeEvent
		public static void onRegisterBlocks(@Nonnull final RegistryEvent.Register<Block> event) {
			// Register all your blocks inside this registerAll call
			event.getRegistry().registerAll(
					blocksToReg.toArray(new Block[0])
					);
			HardLib.LOGGER.debug("Registered Blocks");
		}

		@SubscribeEvent
		public static void onModConfigEvent(@Nonnull final ModConfig.ModConfigEvent event) {
			//final ModConfig config = event.getConfig();
			// Rebake the configs when they change
			//if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
			//	ConfigHelper.bakeClient(config);
			//} else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
			//	ConfigHelper.bakeServer(config);
			//}
		}

		@SubscribeEvent
		public static void onRegisterItems(@Nonnull final RegistryEvent.Register<Item> event) {
			final IForgeRegistry<Item> registry = event.getRegistry();
			registry.registerAll(
					itemsToReg.toArray(new Item[0])
					);
			HardLib.LOGGER.debug("Registered Items");
		}

		@SubscribeEvent
		public static void onRegister(@Nonnull final RegistryEvent.Register<TileEntityType<?>> event) {
			// Register your TileEntities here if you have them
			for(TileEntityType<?> e : tilesToReg) {
				event.getRegistry().register(e);
			}
			HardLib.LOGGER.debug("Registered TileEntitys");
		}

		@SubscribeEvent
		public static void registerEnchantments(@Nonnull final RegistryEvent.Register<Enchantment> event) {
			for(IForgeRegistryEntry<?> e : otherItems) {
				if(e instanceof Enchantment)
					event.getRegistry().register((Enchantment)e);
			}
		}

		@SubscribeEvent
		public static void registerRecipeSerializer(@Nonnull final RegistryEvent.Register<IRecipeSerializer<?>> event) {
			for(IForgeRegistryEntry<?> e : otherItems) {
				if(e instanceof IRecipeSerializer<?>)
					event.getRegistry().register((IRecipeSerializer<?>)e);
			}
		}

		@SubscribeEvent
		public static void registerContainer(@Nonnull final RegistryEvent.Register<ContainerType<?>> event) {
			for(IForgeRegistryEntry<?> e : otherItems) {
				if(e instanceof ContainerType<?>)
					event.getRegistry().register((ContainerType<?>)e);
			}
		}

		@SubscribeEvent
		public static void registerFeature(@Nonnull final RegistryEvent.Register<Feature<?>> event) {
			for(IForgeRegistryEntry<?> e : otherItems) {
				if(e instanceof Feature<?>)
					event.getRegistry().register((Feature<?>)e);
			}
		}
	}
}
