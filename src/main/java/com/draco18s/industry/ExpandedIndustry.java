package com.draco18s.industry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.industry.block.DistributorBlock;
import com.draco18s.industry.block.PoweredRailBridgeBlock;
import com.draco18s.industry.block.RailBridgeBlock;
import com.draco18s.industry.block.WoodenHopperBlock;
import com.draco18s.industry.entity.DistributorBlockEntity;
import com.draco18s.industry.entity.WoodenHopperBlockEntity;
import com.draco18s.industry.inventory.DistributorContainerMenu;
import com.draco18s.industry.inventory.WoodHopperContainerMenu;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

@Mod(ExpandedIndustry.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ExpandedIndustry {
	public static final String MODID = "expindustry";
	public static final Logger LOGGER = LogManager.getLogger();
	
	@ObjectHolder(registryName = "minecraft:blocks", value = MODID+":"+"creative_tab")
	public static final CreativeModeTab tab = null;
	
	//public static final Material RAIL_BRIDGE = (new Material.Builder(MaterialColor.WOOD)).noCollider().nonSolid().build();//.notSolidBlocking()
	
	//public static final IProxy PROXY = DistExecutor.runForDist(()->ClientProxy::new, ()->ServerProxy::new);
	
	public ExpandedIndustry() {
		
		/*
		PacketHandler.register();
		block = new FilterBlock();
		EasyRegistry.registerBlock(block, "machine_filter", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(FilterTileEntity::new, block), ExpandedIndustry.MODID, "machine_filter");
		EasyRegistry.registerOther(IForgeContainerType.create(FilterContainer::new), new ResourceLocation(ExpandedIndustry.MODID,"machine_filter"));
`		*/

		RegistryObject<Block> wood_hopper;
		wood_hopper = EasyRegistry.registerBlock(WoodenHopperBlock::new, getRL("machine_wood_hopper"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(WoodenHopperBlockEntity::new, wood_hopper.get()).build(null), getRL("machine_wood_hopper"));
		EasyRegistry.registerMenuType(getRL("machine_wood_hopper"),() -> new MenuType<>(WoodHopperContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));
		
		RegistryObject<Block> distributor = EasyRegistry.registerBlock(DistributorBlock::new, getRL("machine_distributor"), new Item.Properties());
		EasyRegistry.registerTileEntity(() -> BlockEntityType.Builder.of(DistributorBlockEntity::new, distributor.get()).build(null), getRL("machine_distributor"));
		EasyRegistry.registerMenuType(getRL("machine_distributor"),() -> new MenuType<>(DistributorContainerMenu::new, FeatureFlags.DEFAULT_FLAGS));
		
		EasyRegistry.registerBlock(RailBridgeBlock::new, getRL("rail_bridge"), new Item.Properties());
		EasyRegistry.registerBlock(PoweredRailBridgeBlock::new, getRL("powered_rail_bridge"), new Item.Properties());
		//EasyRegistry.registerOther(IForgeContainerType.create(ExtHopperContainer::new), new ResourceLocation(ExpandedIndustry.MODID,"machine_wood_hopper"));
	}

	private static ResourceLocation getRL(String name) {
		return new ResourceLocation(MODID, name);
	}

	@SubscribeEvent
	public static void registerCreativeTab(CreativeModeTabEvent.Register event) {
		/*tab = */
		event.registerCreativeModeTab(getRL("creative_tab"), (builder) -> {
			builder.title(Component.translatable("itemGroup.industry"));
			builder.icon(() -> new ItemStack(ModBlocks.machine_wood_hopper));
			builder.displayItems(new DisplayItemsGenerator() {
				@Override
				public void accept(ItemDisplayParameters params, Output output) {
					output.accept(new ItemStack(Blocks.DROPPER));
					output.accept(new ItemStack(Blocks.DISPENSER));
					output.accept(new ItemStack(Blocks.OBSERVER));
					output.accept(new ItemStack(Blocks.HOPPER));
					output.accept(new ItemStack(ModBlocks.machine_wood_hopper, 1));
					output.accept(new ItemStack(ModBlocks.machine_distributor, 1));
					output.accept(new ItemStack(ModBlocks.rail_bridge, 1));
					output.accept(new ItemStack(ModBlocks.powered_rail_bridge, 1));
				}
			});
		});
	}

	@SubscribeEvent
	public static void addItemsToCreativeTab(final CreativeModeTabEvent.BuildContents event)
    {
		CreativeModeTab tab = event.getTab();
        if (tab == CreativeModeTabs.REDSTONE_BLOCKS) {
        	event.accept(new ItemStack(ModBlocks.machine_wood_hopper, 1));
        	event.accept(new ItemStack(ModBlocks.machine_distributor, 1));
        	event.accept(new ItemStack(ModBlocks.rail_bridge, 1));
        	event.accept(new ItemStack(ModBlocks.powered_rail_bridge, 1));
        }
    }
	
	public static class ModBlocks {
		//@ObjectHolder(registryName = "minecraft:blocks", value = MODID+"machine_filter")
		public static final Block machine_filter = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"rail_bridge")
		public static final Block rail_bridge = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"powered_rail_bridge")
		public static final Block powered_rail_bridge = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_wood_hopper")
		public static final Block machine_wood_hopper = null;
		@ObjectHolder(registryName = "minecraft:block", value = MODID+":"+"machine_distributor")
		public static final Block machine_distributor = null;
	}

	public static class ModItems {
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"machine_wood_hopper")
		public static final Item machine_wood_hopper = null;
		@ObjectHolder(registryName = "minecraft:item", value = MODID+":"+"machine_distributor")
		public static final Item machine_distributor = null;
		public static final Item machine_filter = null;
	}

	public static class ModTileEntities {
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_wood_hopper")
		public static final BlockEntityType<WoodenHopperBlockEntity> machine_wood_hopper = null;
		@ObjectHolder(registryName = "minecraft:block_entity_type", value = MODID+":"+"machine_distributor")
		public static final BlockEntityType<DistributorBlockEntity> machine_distributor = null;
		//public static final BlockEntityType<FilterTileEntity> machine_filter = null;
	}

	public static class ModContainerTypes {
		@ObjectHolder(registryName = "minecraft:menu", value = MODID+":"+"machine_wood_hopper")
		public static final MenuType<WoodHopperContainerMenu> machine_wood_hopper = null;
		@ObjectHolder(registryName = "minecraft:menu", value = MODID+":"+"machine_distributor")
		public static final MenuType<DistributorContainerMenu> machine_distributor = null;
		//public static final MenuType<FilterContainer> machine_filter = null;
		//public static final ContainerType<SifterContainer> sifter = null;
	}

	public static class ModEnchantments {

	}

	public static class ModItemTags {
		//public static final Tag<Item> STONE_ANY = new ItemTags.Wrapper(new ResourceLocation("forge", "stoneany"));
	}

	public static class ModDims {
		//public static final ModDimension FILTER_DIMENSION = null;
	}
	
	public static class ModDimensionType {
		//public static DimensionType FILTER_DIMENSION = null;
	}
	
	@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = EventBusSubscriber.Bus.MOD)
	private static class RegistrationMod {
		/*@SubscribeEvent
		public static void onRegisterDimType(@Nonnull final RegistryEvent.Register<ModDimension> event) {
			ModDimension value = new FilterModDimension();
			value.setRegistryName("filter_dimension");
			event.getRegistry().register(value);
		}*/
	}
	
	@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = EventBusSubscriber.Bus.FORGE)
	private static class RegistrationForge {
		/*@SuppressWarnings("deprecation")
		@SubscribeEvent
		public static void onRegisterDim(@Nonnull final RegisterDimensionsEvent event) {
			if(!DimensionManager.getRegistry().containsKey(new ResourceLocation(ExpandedIndustry.MODID,"filter_dimension")))
				ModDimensionType.FILTER_DIMENSION = DimensionManager.registerDimension(new ResourceLocation(ExpandedIndustry.MODID,"filter_dimension"), ModDims.FILTER_DIMENSION, null, false);
			else
				ModDimensionType.FILTER_DIMENSION = DimensionManager.getRegistry().getValue(new ResourceLocation(ExpandedIndustry.MODID,"filter_dimension")).orElse(null);
		}*/
	}
}