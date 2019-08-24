package com.draco18s.industry;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.draco18s.hardlib.EasyRegistry;
import com.draco18s.hardlib.api.internal.ModItemGroup;
import com.draco18s.industry.block.DistributorBlock;
import com.draco18s.industry.block.FilterBlock;
import com.draco18s.industry.block.PoweredRailBridgeBlock;
import com.draco18s.industry.block.RailBridgeBlock;
import com.draco18s.industry.block.WoodenHopperBlock;
import com.draco18s.industry.entity.DistributorTileEntity;
import com.draco18s.industry.entity.FilterTileEntity;
import com.draco18s.industry.entity.WoodenHopperTileEntity;
import com.draco18s.industry.inventory.DistributorContainer;
import com.draco18s.industry.inventory.FilterContainer;
import com.draco18s.industry.inventory.WoodHopperContainer;
import com.draco18s.industry.network.PacketHandler;
import com.draco18s.industry.proxy.ClientProxy;
import com.draco18s.industry.proxy.IProxy;
import com.draco18s.industry.proxy.ServerProxy;
import com.draco18s.industry.world.FilterModDimension;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@Mod(ExpandedIndustry.MODID)
public class ExpandedIndustry {
	public static final String MODID = "expindustry";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final ModItemGroup modItemGroup = new ModItemGroup("industry", ()->(ModItems.machine_wood_hopper)) {
		@OnlyIn(Dist.CLIENT)
		public void fill(NonNullList<ItemStack> items) {
			items.add(new ItemStack(Blocks.DROPPER));
			items.add(new ItemStack(Blocks.DISPENSER));
			items.add(new ItemStack(Blocks.OBSERVER));
			items.add(new ItemStack(Blocks.HOPPER));
			items.add(new ItemStack(ModItems.machine_wood_hopper));
			items.add(new ItemStack(ModItems.machine_distributor));
			//items.add(new ItemStack(ModItems.blockCartLoader));
			items.add(new ItemStack(ModItems.machine_filter));
			//items.add(new ItemStack(ModItems.blockFoundry));
			for(Item item : ForgeRegistries.ITEMS) {
				item.fillItemGroup(this, items);
			}
		}
	};
	public static final IProxy PROXY = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());

	public ExpandedIndustry() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener((FMLCommonSetupEvent event) -> {
			//LootFunctionManager.registerFunction(new BlockItemFunction.Serializer());
			//LootFunctionManager.registerFunction(new HarderSetCount.Serializer());
		});

		modEventBus.addListener((FMLLoadCompleteEvent event) -> {
			//for(OreItems ores : ORE_ITEMS) {
			//HardLibAPI.oreMachines.addMillRecipe(new ItemStack(ores.chunk,1), new ItemStack(ores.tiny,2));
			//HardLibAPI.oreMachines.addSiftRecipe(new ItemStack(ores.tiny,8), new ItemStack(ores.pile,1));
			//}
		});
		PacketHandler.register();
		Block block;
		block = new WoodenHopperBlock();
		EasyRegistry.registerBlock(block, "machine_wood_hopper", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(WoodenHopperTileEntity::new, block), ExpandedIndustry.MODID, "machine_wood_hopper");
		EasyRegistry.registerOther(IForgeContainerType.create(WoodHopperContainer::new), new ResourceLocation(ExpandedIndustry.MODID,"machine_wood_hopper"));
		block = new DistributorBlock();
		EasyRegistry.registerBlock(block, "machine_distributor", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(DistributorTileEntity::new, block), ExpandedIndustry.MODID, "machine_distributor");
		EasyRegistry.registerOther(IForgeContainerType.create(DistributorContainer::new), new ResourceLocation(ExpandedIndustry.MODID,"machine_distributor"));
		block = new FilterBlock();
		EasyRegistry.registerBlock(block, "machine_filter", new Item.Properties().group(ItemGroup.DECORATIONS));
		EasyRegistry.registerTileEntity(TileEntityType.Builder.create(FilterTileEntity::new, block), ExpandedIndustry.MODID, "machine_filter");
		EasyRegistry.registerOther(IForgeContainerType.create(FilterContainer::new), new ResourceLocation(ExpandedIndustry.MODID,"machine_filter"));

		block = new RailBridgeBlock();
		EasyRegistry.registerBlock(block, "rail_bridge", new Item.Properties().group(ItemGroup.DECORATIONS));
		block = new PoweredRailBridgeBlock();
		EasyRegistry.registerBlock(block, "powered_rail_bridge", new Item.Properties().group(ItemGroup.DECORATIONS));
		
		//EasyRegistry.registerOther(IForgeContainerType.create(ExtHopperContainer::new), new ResourceLocation(ExpandedIndustry.MODID,"machine_wood_hopper"));

	}

	@ObjectHolder(ExpandedIndustry.MODID)
	public static class ModBlocks {
		public static final Block machine_filter = null;
	}

	@ObjectHolder(ExpandedIndustry.MODID)
	public static class ModItems {
		public static final Item machine_wood_hopper = null;
		public static final Item machine_distributor = null;
		public static final Item machine_filter = null;
	}

	@ObjectHolder(ExpandedIndustry.MODID)
	public static class ModTileEntities {
		public static final TileEntityType<WoodenHopperTileEntity> machine_wood_hopper = null;
		public static final TileEntityType<DistributorTileEntity> machine_distributor = null;
		public static final TileEntityType<FilterTileEntity> machine_filter = null;
	}

	@ObjectHolder(ExpandedIndustry.MODID)
	public static class ModContainerTypes {
		public static final ContainerType<WoodHopperContainer> machine_wood_hopper = null;
		public static final ContainerType<DistributorContainer> machine_distributor = null;
		public static final ContainerType<FilterContainer> machine_filter = null;
		//public static final ContainerType<SifterContainer> sifter = null;
	}

	@ObjectHolder(ExpandedIndustry.MODID)
	public static class ModEnchantments {

	}

	public static class ModItemTags {
		//public static final Tag<Item> STONE_ANY = new ItemTags.Wrapper(new ResourceLocation("forge", "stoneany"));
	}

	@ObjectHolder(ExpandedIndustry.MODID)
	public static class ModDims {
		public static final ModDimension FILTER_DIMENSION = null;
	}
	
	public static class ModDimensionType {
		public static DimensionType FILTER_DIMENSION = null;
	}
	
	@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = EventBusSubscriber.Bus.MOD)
	private static class RegistrationMod {
		@SubscribeEvent
		public static void onRegisterDimType(@Nonnull final RegistryEvent.Register<ModDimension> event) {
			ModDimension value = new FilterModDimension();
			value.setRegistryName("filter_dimension");
			event.getRegistry().register(value);
		}
	}
	
	@EventBusSubscriber(modid = ExpandedIndustry.MODID, bus = EventBusSubscriber.Bus.FORGE)
	private static class RegistrationForge {
		@SuppressWarnings("deprecation")
		@SubscribeEvent
		public static void onRegisterDim(@Nonnull final RegisterDimensionsEvent event) {
			if(!DimensionManager.getRegistry().containsKey(new ResourceLocation(ExpandedIndustry.MODID,"filter_dimension")))
				ModDimensionType.FILTER_DIMENSION = DimensionManager.registerDimension(new ResourceLocation(ExpandedIndustry.MODID,"filter_dimension"), ModDims.FILTER_DIMENSION, null, false);
			else
				ModDimensionType.FILTER_DIMENSION = DimensionManager.getRegistry().getValue(new ResourceLocation(ExpandedIndustry.MODID,"filter_dimension")).orElse(null);
		}
	}
}