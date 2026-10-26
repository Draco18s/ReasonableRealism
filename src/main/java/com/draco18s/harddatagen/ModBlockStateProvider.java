package com.draco18s.harddatagen;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.draco18s.farming.HarderFarming;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.internal.OreNameHelper;
import com.draco18s.industry.ExpandedIndustry;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

@SuppressWarnings("unused")
public class ModBlockStateProvider extends BlockStateProvider {
	protected final ExistingFileHelper fileHelper;
	protected Map<ResourceLocation, Supplier<JsonElement>> map1;
	BiConsumer<ResourceLocation, Supplier<JsonElement>> biconsumer;
	private final String modid;

	/** One provider per mod: modid picks which mod's blocks it generates, and the namespace of relative model names. */
	public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
		super(output, modid, exFileHelper);
		this.modid = modid;
		fileHelper = exFileHelper;
		map1 = Maps.newHashMap();
		biconsumer = (p_125123_, p_125124_) -> {
			Supplier<JsonElement> supplier = map1.put(p_125123_, p_125124_);
			if (supplier != null) {
				throw new IllegalStateException("Duplicate model definition for " + p_125123_);
			}
		};
	}

	@Override
	protected void registerStatesAndModels() {
		switch(modid) {
			case ExpandedIndustry.MODID -> industry();
			case HarderOres.MODID -> harderOres();
			case HarderFarming.MODID -> farming();
			case OreFlowers.MODID -> flowers();
			default -> throw new IllegalArgumentException("No block states for " + modid);
		}
	}

	private void industry() {
		createPassiveRail(ExpandedIndustry.ModBlocks.rail_bridge,
				ExpandedIndustry.getRL(ModelProvider.BLOCK_FOLDER + "/rail_bridge"),
				HardLib.getRL("minecraft", ModelProvider.BLOCK_FOLDER + "/oak_planks"));
		createActiveRail(ExpandedIndustry.ModBlocks.powered_rail_bridge,
				ExpandedIndustry.getRL(ModelProvider.BLOCK_FOLDER + "/rail_bridge_powered"),
				HardLib.getRL("minecraft", ModelProvider.BLOCK_FOLDER + "/oak_planks"));
		extHopper(ExpandedIndustry.ModBlocks.machine_wood_hopper, ExpandedIndustry.getRL("wood_hopper"));
		extHopper(ExpandedIndustry.ModBlocks.machine_distributor, ExpandedIndustry.getRL("distributor"));
	}

	private void harderOres() {
		millstone(HarderOres.ModBlocks.machine_millstone,
				HarderOres.getRL(ModelProvider.BLOCK_FOLDER + "/mill"));

		simpleBlockWithItem(HarderOres.ModBlocks.ore_limonite, simpleOre(HarderOres.ModBlocks.ore_limonite));
		
		genHardOres();
		
		sluicemodel(HarderOres.ModBlocks.sluice, prebuiltModel(HarderOres.getRL("sluice-1")));
		simpleBlockWithItem(HarderOres.ModBlocks.machine_sifter, prebuiltModel(HarderOres.getRL("machine_sifter")));
		simpleBlockWithItem(HarderOres.ModBlocks.packager, models().cubeTop("packager",
				HarderOres.getRL(ModelProvider.BLOCK_FOLDER + "/packager-side"),
				HarderOres.getRL(ModelProvider.BLOCK_FOLDER + "/packager-top")));
		axel(HarderOres.ModBlocks.machine_axel);
		windvane(HarderOres.ModBlocks.machine_windvane);
	}

	private void farming() {
		simpleBlockWithItem(HarderFarming.ModBlocks.ore_salt, simpleOre(HarderFarming.ModBlocks.ore_salt));
		crops((CropBlock)HarderFarming.ModBlocks.crop_winter_wheat, "block/winter_wheat_stage_", "winter_wheat_stage_");
		weeds((CropBlock)HarderFarming.ModBlocks.crop_weeds);
	}

	/**
	 * Flowers are crosses of their item texture. Two-tall ones use "<name>1" for the lower half, and blooming ones
	 * use it for the not-blooming phase (bloom=false shows the "_bloom" model, as 1.14 did).
	 */
	private void flowers() {
		for(Map.Entry<String, DeferredHolder<Block, Block>> entry : OreFlowers.FLOWERS.entrySet()) {
			String name = entry.getKey();
			Block block = entry.getValue().get();
			ModelFile main = flowerModel(name, name);
			if(block.defaultBlockState().hasProperty(BlockProperties.BLOOM_PHASE)) {
				ModelFile other = flowerModel(name + "_bloom", name + "1");
				getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(state.getValue(BlockProperties.BLOOM_PHASE) ? main : other).build());
			}
			else if(block instanceof DoublePlantBlock) {
				ModelFile lower = flowerModel(name + "_down", name + "1");
				getVariantBuilder(block).forAllStates(state -> ConfiguredModel.builder().modelFile(state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER ? main : lower).build());
			}
			else {
				simpleBlock(block, main);
			}
			createSimpleFlatItemModel(block, HardLib.getRL(modid, ModelProvider.ITEM_FOLDER + "/" + name));
		}
	}

	private ModelFile flowerModel(String name, String texture) {
		return models().cross(name, HardLib.getRL(modid, ModelProvider.ITEM_FOLDER + "/" + texture)).renderType("cutout");
	}

	/** A crop's age property (CropBlock.getAgeProperty is protected now). */
	private static IntegerProperty age(CropBlock block) {
		return (IntegerProperty)block.getStateDefinition().getProperty(BlockStateProperties.AGE_7.getName());
	}

	/** Weeds have 16 ages but 8 stages of growth to show; the later ages all look fully grown. */
	private void weeds(CropBlock block) {
		getVariantBuilder(block).forAllStates(state -> {
			int stage = Math.min(state.getValue(age(block)), 7);
			return new ConfiguredModel[] { new ConfiguredModel(models().crop(HarderFarming.MODID + ":block/weeds_stage_" + stage,
					HarderFarming.getRL(ModelProvider.BLOCK_FOLDER + "/weeds_stage_" + stage)).renderType("cutout")) };
		});
	}

	private void sluicemodel(Block block, ModelFile prebuiltModel) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block).withPrefix(ModelProvider.BLOCK_FOLDER + "/");
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model = null;
			int h = ((state.getValue(SluiceBlock.LEVEL) + 1) / 2) * 2 - 1;
			h = Math.max(h, 1);
			model = new ConfiguredModel(models().getExistingFile(registryName.withSuffix("-"+h)),0,0,false);
			return new ConfiguredModel[] {model}; 
		};
		getVariantBuilder(block).forAllStates(f);
		ResourceLocation registryName2 = BuiltInRegistries.BLOCK.getKey(block);
		//itemModels().withExistingParent(registryName2.toString(), registryName);
	}

	private void windvane(Block block) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block).withPrefix(ModelProvider.BLOCK_FOLDER + "/");
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model = null;
			Direction v = state.getValue(BlockStateProperties.FACING);
			switch(v) {
				case DOWN:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,180,180,false);
					break;
				case EAST:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,90,270,false);
					break;
				case NORTH:
				case SOUTH:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,90,180,false);
					break;
				case UP:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,0,0,false);
					break;
				case WEST:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,270,270,false);
					break;
				default:
					break;
			}
			return new ConfiguredModel[] {model}; 
		};
		getVariantBuilder(block).forAllStates(f);
		ResourceLocation registryName2 = BuiltInRegistries.BLOCK.getKey(block);
		itemModels().withExistingParent(registryName2.toString(), registryName);
	}

	private void axel(Block block) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block).withPrefix(ModelProvider.BLOCK_FOLDER + "/");
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model = null;
			AxelOrientation v = state.getValue(BlockProperties.AXEL_ORIENTATION);
			Direction dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			int yRot = 0;
			switch(dir) {
				case SOUTH:
					yRot = 180;
					break;
				case EAST:
					yRot = 90;
					break;
				case WEST:
					yRot = 270;
					break;
				default:
					break;
			}
			switch(v) {
				case GEARS:
					model = new ConfiguredModel(models().getExistingFile(registryName.withSuffix("_gears"))
							,0,yRot,false);
					break;
				case NONE:
				case HUB:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,0,yRot,false);
					break;
				case UP:
					model = new ConfiguredModel(models().getExistingFile(registryName)
							,270,0,false);
					break;
				default:
					break;
			}
			return new ConfiguredModel[] {model};
		};
		getVariantBuilder(block).forAllStates(f);
		ResourceLocation registryName2 = BuiltInRegistries.BLOCK.getKey(block);
		itemModels().withExistingParent(registryName2.toString(), registryName);
	}

	private void genHardOres() {
		OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName)->{
			Block blk = BuiltInRegistries.BLOCK.get(harderOreBlockName);
			hardOre(blk, harderOreBlockName.getPath(), vanillaOriginalOreTexture, harderOreBlockName.withPrefix(ModelProvider.BLOCK_FOLDER+"/"));
	    });
	}

	private void millstone(Block block, ResourceLocation textureName) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model = null;
			MillstoneOrientation v = state.getValue(BlockProperties.MILL_ORIENTATION);
			ResourceLocation cornerName = registryName.withSuffix("_corner");
			ResourceLocation edgeName = registryName.withSuffix("_side");
			switch(v) {
				case NONE:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(), HardLib.parseRL("cube_column"))
						.texture("end", textureName.withSuffix("_generic"))
						.texture("side", textureName.withSuffix("_side")));
					break;
				case CENTER:
					model = new ConfiguredModel(models().withExistingParent(registryName.withSuffix("_center").toString(), HardLib.parseRL("cube_column"))
							.texture("end", textureName.withSuffix("_center"))
							.texture("side", textureName.withSuffix("_side")));
					break;
				case EAST:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), HardLib.parseRL("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")));
					break;
				case NORTH:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), HardLib.parseRL("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")),0,270,false);
					break;
				case NORTH_EAST:
					/*model = new ConfiguredModel(models().getExistingFile(HarderOres.getRL("block/test.obj")));
					*/
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), HardLib.parseRL("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")));
					break;
				case NORTH_WEST:
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), HardLib.parseRL("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")),0,270,false);
					break;
				case SOUTH:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), HardLib.parseRL("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")),0,90,false);
					break;
				case SOUTH_EAST:
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), HardLib.parseRL("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")),0,90,false);
					break;
				case SOUTH_WEST:
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), HardLib.parseRL("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")),0,180,false);
					break;
				case WEST:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), HardLib.parseRL("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")),0,180,false);
					break;
				default:
					break;
			}
			return new ConfiguredModel[] {model};
		};
		getVariantBuilder(block).forAllStates(f);
		//this.simpleBlockItem(block, null);
		this.itemModels().cubeColumn(registryName.toString(),
				HarderOres.getRL(ModelProvider.BLOCK_FOLDER + "/mill_side"),
				HarderOres.getRL(ModelProvider.BLOCK_FOLDER + "/mill_top"));
	}

	private ModelFile prebuiltModel(ResourceLocation resourceLocation) {
		return new ConfiguredModel(models().getExistingFile(resourceLocation), 0, 0, true).model;
	}

	private void extHopper(Block block, ResourceLocation itemTexture) {
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model;
			switch(state.getValue(HopperBlock.FACING)) {
				case DOWN :
					model = new ConfiguredModel(models().getExistingFile(
							ExpandedIndustry.getRL("hopper")), 0, 180, true);
					return new ConfiguredModel[] {model};
				case EAST :
					model = new ConfiguredModel(models().getExistingFile(
							ExpandedIndustry.getRL("hopper_side")), 0, 90, true);
					return new ConfiguredModel[] {model};
				case NORTH :
					model = new ConfiguredModel(models().getExistingFile(
							ExpandedIndustry.getRL("hopper_side")), 0, 0, true);
					return new ConfiguredModel[] {model};
				case SOUTH :
					model = new ConfiguredModel(models().getExistingFile(
							ExpandedIndustry.getRL("hopper_side")), 0, 180, true);
					return new ConfiguredModel[] {model};
				case WEST :
					model = new ConfiguredModel(models().getExistingFile(
							ExpandedIndustry.getRL("hopper_side")), 0, 270, true);
					return new ConfiguredModel[] {model};
				default :
					throw new RuntimeException("Invalid hopper block state!");
			}
		};
		getVariantBuilder(block).forAllStatesExcept(f, BlockStateProperties.ENABLED);
		createSimpleFlatItemModel(block, ModelProvider.ITEM_FOLDER, itemTexture);
	}

	private void createPassiveRail(Block block, ResourceLocation railTexture, ResourceLocation supportTexture) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);

		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model;
			switch(state.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
				case NORTH_SOUTH:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							ExpandedIndustry.getRL("rail_bridge_base"))
							.texture("rail", railTexture)
							.texture("supports", supportTexture), 0, 0, false);
					return new ConfiguredModel[] {model};
				case EAST_WEST:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							ExpandedIndustry.getRL("rail_bridge_base"))
							.texture("rail", railTexture)
							.texture("supports", supportTexture), 0, 90, false);
					return new ConfiguredModel[] {model};
				default:
					model = new ConfiguredModel(models().withExistingParent("minecraft:air", "minecraft:air"));
					return new ConfiguredModel[] { model };
			}
		};
		getVariantBuilder(block).forAllStatesExcept(f, BlockStateProperties.WATERLOGGED);
		createSimpleFlatItemModel(block);
	}

	private void createActiveRail(Block block, ResourceLocation railTexture, ResourceLocation supportTexture) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
		ResourceLocation railTextureOn = HardLib.getRL(railTexture.getNamespace(), railTexture.getPath() + "_on");

		Function<BlockState, ConfiguredModel[]> f = state -> {
			boolean isPowered = state.getValue(BlockStateProperties.POWERED);
			ConfiguredModel model;
			switch(state.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
				case NORTH_SOUTH:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							ExpandedIndustry.getRL("rail_bridge_base"))
							.texture("rail", isPowered ? railTexture : railTextureOn)
							.texture("supports", supportTexture), 0, 0, false);
					return new ConfiguredModel[] {model};
				case EAST_WEST:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							ExpandedIndustry.getRL("rail_bridge_base"))
							.texture("rail", isPowered ? railTexture : railTextureOn)
							.texture("supports", supportTexture), 0, 90, false);
					return new ConfiguredModel[] {model};
				default:
					model = new ConfiguredModel(models().getExistingFile(HardLib.parseRL("minecraft:air")));
					return new ConfiguredModel[] { model };
			}
		};
		getVariantBuilder(block).forAllStatesExcept(f, BlockStateProperties.WATERLOGGED);
		createSimpleFlatItemModel(block, railTextureOn);
	}

	public ModelFile pillarBlockModel(Block block, ResourceLocation topTextureName, ResourceLocation sideTextureName) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
		return models().cubeColumn(registryName.getPath(), topTextureName, sideTextureName);
	}

	public ModelFile simpleOre(Block block) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
		return models().cubeAll(registryName.getPath(), HardLib.getRL(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/ore/" + registryName.getPath().replace("ore_","")));
	}

	public ModelFile variantOre(Block block, String suffix) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
		return models().cubeAll(registryName.getPath(), HardLib.getRL(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/ore/" + registryName.getPath().replace("ore_","") + suffix));
	}

	private void crops(CropBlock block, String modelName, String textureName) {
		Function<BlockState, ConfiguredModel[]> f = state -> cropStates(state, block, modelName, textureName);
		getVariantBuilder(block).forAllStates(f);
	}

	private ConfiguredModel[] cropStates(BlockState state, CropBlock block, String modelName, String textureName) {
		ResourceLocation registryName = BuiltInRegistries.BLOCK.getKey(block);
		modelName = registryName.getNamespace() + ":" + modelName;
		ConfiguredModel[] models = new ConfiguredModel[1];
		models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(age(block)),
				HardLib.getRL(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + textureName + state.getValue(age(block)))
				).renderType("cutout"));
		return models;
	}

	private void hardOre(Block block, String blockName, ResourceLocation baseTexture, ResourceLocation overlayTexture) {
		Function<BlockState, ConfiguredModel[]> f = state -> oreStates(state, blockName, baseTexture, overlayTexture);
		getVariantBuilder(block).forAllStates(f);
		
		createVariantItemModel(block, BlockProperties.ORE_DENSITY, v -> (float)v.value(), v -> "_" + v.value().toString());
	}

	private ConfiguredModel[] oreStates(BlockState state, String modelName, ResourceLocation baseTexture, ResourceLocation overlayTexture) {
		ConfiguredModel[] models = new ConfiguredModel[1];
		models[0] = new ConfiguredModel(
				hardOreSingleModel(modelName + "_" + state.getValue(BlockProperties.ORE_DENSITY),
						baseTexture, overlayTexture.withSuffix("_"+state.getValue(BlockProperties.ORE_DENSITY))));
		return models;
	}

	private BlockModelBuilder hardOreSingleModel(String name, ResourceLocation baseRL, ResourceLocation overlayRL) {
		return models().withExistingParent(name, HarderOres.getRL(ModelProvider.BLOCK_FOLDER + "/hard_ore"))
				.texture("all", baseRL)
				.texture("overlay", overlayRL);
	}

	private void createSimpleFlatItemModel(Block block) {
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(block.asItem());
		itemModels().getBuilder(registryName.toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", HardLib.getRL(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + registryName.getPath()));
	}

	private void createSimpleFlatItemModel(Block block, ResourceLocation itemTexture) {
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(block.asItem());
		itemModels().getBuilder(registryName.toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", HardLib.getRL(itemTexture.getNamespace(), itemTexture.getPath()));
	}

	private void createSimpleFlatItemModel(Block block, String directory, ResourceLocation itemTexture) {
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(block.asItem());
		itemModels().getBuilder(registryName.toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", HardLib.getRL(itemTexture.getNamespace(), directory + "/" + itemTexture.getPath()));
	}

	private <T extends Comparable<T>> ItemModelBuilder createVariantItemModel(Block block, Property<T> property, Function<? super Property.Value<T>, Float> converter, Function<? super Property.Value<T>, String> nameConverter) {
		Item item = block.asItem();
		ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
		ItemModelBuilder modelNormal = itemModels().getBuilder(registryName.toString());
		property.getAllValues().forEach(value ->{
			//ResourceLocation harderOreBlockName = new ResourceLocation(HarderOres.MODID, "ore_hard"+stone_+ore);
			ModelFile model16 = new ModelFile.ExistingModelFile(
					HardLib.getRL(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" +  registryName.getPath() + nameConverter.apply(value)), fileHelper);
			modelNormal.override()
				.predicate(HarderOres.getRL(BlockProperties.ORE_DENSITY.getName()), converter.apply(value))
				.model(model16)
				.end();
		});
		return modelNormal;
	}
}
