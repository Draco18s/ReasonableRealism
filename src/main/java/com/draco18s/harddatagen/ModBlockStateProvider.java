package com.draco18s.harddatagen;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.draco18s.farming.HarderFarming;
import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.blockproperties.ores.MillstoneOrientation;
import com.draco18s.hardlib.api.internal.OreNameHelper;
import com.draco18s.industry.ExpandedIndustry;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("unused")
public class ModBlockStateProvider extends BlockStateProvider {
	protected final ExistingFileHelper fileHelper;
	protected Map<ResourceLocation, Supplier<JsonElement>> map1;
	BiConsumer<ResourceLocation, Supplier<JsonElement>> biconsumer;
	public ModBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
		super(output, modid, exFileHelper);
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
		createPassiveRail(ExpandedIndustry.ModBlocks.rail_bridge,
				new ResourceLocation(ExpandedIndustry.MODID, ModelProvider.BLOCK_FOLDER + "/rail_bridge"),
				new ResourceLocation("minecraft", ModelProvider.BLOCK_FOLDER + "/oak_planks"));
		createActiveRail(ExpandedIndustry.ModBlocks.powered_rail_bridge,
				new ResourceLocation(ExpandedIndustry.MODID, ModelProvider.BLOCK_FOLDER + "/rail_bridge_powered"),
				new ResourceLocation("minecraft", ModelProvider.BLOCK_FOLDER + "/oak_planks"));
		
		millstone(HarderOres.ModBlocks.machine_millstone,
				new ResourceLocation(HarderOres.MODID, ModelProvider.BLOCK_FOLDER + "/mill"));

		simpleBlockWithItem(HarderOres.ModBlocks.ore_limonite, simpleOre(HarderOres.ModBlocks.ore_limonite));
		simpleBlockWithItem(HarderFarming.ModBlocks.ore_salt, simpleOre(HarderFarming.ModBlocks.ore_salt));
		
		genHardOres();
		
		sluicemodel(HarderOres.ModBlocks.sluice, prebuiltModel(new ResourceLocation(HarderOres.MODID,"sluice-1")));
		simpleBlockWithItem(HarderOres.ModBlocks.machine_sifter, prebuiltModel(new ResourceLocation(HarderOres.MODID,"machine_sifter")));
		extHopper(ExpandedIndustry.ModBlocks.machine_wood_hopper, new ResourceLocation(ExpandedIndustry.MODID, "wood_hopper"));
		extHopper(ExpandedIndustry.ModBlocks.machine_distributor, new ResourceLocation(ExpandedIndustry.MODID, "distributor"));
		axel(HarderOres.ModBlocks.machine_axel);
		windvane(HarderOres.ModBlocks.machine_windvane);
		
		crops((CropBlock)HarderFarming.ModBlocks.crop_winter_wheat, "block/winter_wheat_stage_", "winter_wheat_stage_");
	}

	private void sluicemodel(Block block, ModelFile prebuiltModel) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block).withPrefix(ModelProvider.BLOCK_FOLDER + "/");
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model = null;
			int h = ((state.getValue(SluiceBlock.LEVEL) + 1) / 2) * 2 - 1;
			h = Math.max(h, 1);
			model = new ConfiguredModel(models().getExistingFile(registryName.withSuffix("-"+h)),0,0,false);
			return new ConfiguredModel[] {model}; 
		};
		getVariantBuilder(block).forAllStates(f);
		ResourceLocation registryName2 = ForgeRegistries.BLOCKS.getKey(block);
		//itemModels().withExistingParent(registryName2.toString(), registryName);
	}

	private void windvane(Block block) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block).withPrefix(ModelProvider.BLOCK_FOLDER + "/");
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
		ResourceLocation registryName2 = ForgeRegistries.BLOCKS.getKey(block);
		itemModels().withExistingParent(registryName2.toString(), registryName);
	}

	private void axel(Block block) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block).withPrefix(ModelProvider.BLOCK_FOLDER + "/");
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
		ResourceLocation registryName2 = ForgeRegistries.BLOCKS.getKey(block);
		itemModels().withExistingParent(registryName2.toString(), registryName);
	}

	private void genHardOres() {
		OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName)->{
			Block blk = ForgeRegistries.BLOCKS.getValue(harderOreBlockName);
			hardOre(blk, harderOreBlockName.getPath(), vanillaOriginalOreTexture, harderOreBlockName.withPrefix(ModelProvider.BLOCK_FOLDER+"/"));
	    });
	}

	private void millstone(Block block, ResourceLocation textureName) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model = null;
			MillstoneOrientation v = state.getValue(BlockProperties.MILL_ORIENTATION);
			ResourceLocation cornerName = registryName.withSuffix("_corner");
			ResourceLocation edgeName = registryName.withSuffix("_side");
			switch(v) {
				case NONE:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(), new ResourceLocation("cube_column"))
						.texture("end", textureName.withSuffix("_generic"))
						.texture("side", textureName.withSuffix("_side")));
					break;
				case CENTER:
					model = new ConfiguredModel(models().withExistingParent(registryName.withSuffix("_center").toString(), new ResourceLocation("cube_column"))
							.texture("end", textureName.withSuffix("_center"))
							.texture("side", textureName.withSuffix("_side")));
					break;
				case EAST:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), new ResourceLocation("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")));
					break;
				case NORTH:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), new ResourceLocation("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")),0,270,false);
					break;
				case NORTH_EAST:
					/*model = new ConfiguredModel(models().getExistingFile(new ResourceLocation("harderores","block/test.obj")));
					*/
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), new ResourceLocation("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")));
					break;
				case NORTH_WEST:
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), new ResourceLocation("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")),0,270,false);
					break;
				case SOUTH:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), new ResourceLocation("cube_column"))
							.texture("end", textureName.withSuffix("_edge"))
							.texture("side", textureName.withSuffix("_side")),0,90,false);
					break;
				case SOUTH_EAST:
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), new ResourceLocation("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")),0,90,false);
					break;
				case SOUTH_WEST:
					model = new ConfiguredModel(models().withExistingParent(cornerName.toString(), new ResourceLocation("cube_bottom_top"))
							.texture("bottom", textureName.withSuffix("_corner2"))
							.texture("top", textureName.withSuffix("_corner"))
							.texture("side", textureName.withSuffix("_side")),0,180,false);
					break;
				case WEST:
					model = new ConfiguredModel(models().withExistingParent(edgeName.toString(), new ResourceLocation("cube_column"))
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
				new ResourceLocation(HarderOres.MODID,ModelProvider.BLOCK_FOLDER + "/mill_side"),
				new ResourceLocation(HarderOres.MODID,ModelProvider.BLOCK_FOLDER + "/mill_top"));
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
							new ResourceLocation(ExpandedIndustry.MODID,"hopper")), 0, 180, true);
					return new ConfiguredModel[] {model};
				case EAST :
					model = new ConfiguredModel(models().getExistingFile(
							new ResourceLocation(ExpandedIndustry.MODID,"hopper_side")), 0, 90, true);
					return new ConfiguredModel[] {model};
				case NORTH :
					model = new ConfiguredModel(models().getExistingFile(
							new ResourceLocation(ExpandedIndustry.MODID,"hopper_side")), 0, 0, true);
					return new ConfiguredModel[] {model};
				case SOUTH :
					model = new ConfiguredModel(models().getExistingFile(
							new ResourceLocation(ExpandedIndustry.MODID,"hopper_side")), 0, 180, true);
					return new ConfiguredModel[] {model};
				case WEST :
					model = new ConfiguredModel(models().getExistingFile(
							new ResourceLocation(ExpandedIndustry.MODID,"hopper_side")), 0, 270, true);
					return new ConfiguredModel[] {model};
				default :
					throw new RuntimeException("Invalid hopper block state!");
			}
		};
		getVariantBuilder(block).forAllStatesExcept(f, BlockStateProperties.ENABLED);
		createSimpleFlatItemModel(block, ModelProvider.ITEM_FOLDER, itemTexture);
	}

	private void createPassiveRail(Block block, ResourceLocation railTexture, ResourceLocation supportTexture) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);

		Function<BlockState, ConfiguredModel[]> f = state -> {
			ConfiguredModel model;
			switch(state.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
				case NORTH_SOUTH:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							new ResourceLocation(ExpandedIndustry.MODID,"rail_bridge_base"))
							.texture("rail", railTexture)
							.texture("supports", supportTexture), 0, 0, false);
					return new ConfiguredModel[] {model};
				case EAST_WEST:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							new ResourceLocation(ExpandedIndustry.MODID,"rail_bridge_base"))
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
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
		ResourceLocation railTextureOn = new ResourceLocation(railTexture.getNamespace(), railTexture.getPath() + "_on");

		Function<BlockState, ConfiguredModel[]> f = state -> {
			boolean isPowered = state.getValue(BlockStateProperties.POWERED);
			ConfiguredModel model;
			switch(state.getValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT)) {
				case NORTH_SOUTH:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							new ResourceLocation(ExpandedIndustry.MODID,"rail_bridge_base"))
							.texture("rail", isPowered ? railTexture : railTextureOn)
							.texture("supports", supportTexture), 0, 0, false);
					return new ConfiguredModel[] {model};
				case EAST_WEST:
					model = new ConfiguredModel(models().withExistingParent(registryName.toString(),
							new ResourceLocation(ExpandedIndustry.MODID,"rail_bridge_base"))
							.texture("rail", isPowered ? railTexture : railTextureOn)
							.texture("supports", supportTexture), 0, 90, false);
					return new ConfiguredModel[] {model};
				default:
					model = new ConfiguredModel(models().getExistingFile(new ResourceLocation("minecraft:air")));
					return new ConfiguredModel[] { model };
			}
		};
		getVariantBuilder(block).forAllStatesExcept(f, BlockStateProperties.WATERLOGGED);
		createSimpleFlatItemModel(block, railTextureOn);
	}

	public ModelFile pillarBlockModel(Block block, ResourceLocation topTextureName, ResourceLocation sideTextureName) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
		return models().cubeColumn(registryName.getPath(), topTextureName, sideTextureName);
	}

	public ModelFile simpleOre(Block block) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
		return models().cubeAll(registryName.getPath(), new ResourceLocation(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/ore/" + registryName.getPath().replace("ore_","")));
	}

	public ModelFile variantOre(Block block, String suffix) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
		return models().cubeAll(registryName.getPath(), new ResourceLocation(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/ore/" + registryName.getPath().replace("ore_","") + suffix));
	}

	private void crops(CropBlock block, String modelName, String textureName) {
		Function<BlockState, ConfiguredModel[]> f = state -> cropStates(state, block, modelName, textureName);
		getVariantBuilder(block).forAllStates(f);
	}

	private ConfiguredModel[] cropStates(BlockState state, CropBlock block, String modelName, String textureName) {
		ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
		modelName = registryName.getNamespace() + ":" + modelName;
		ConfiguredModel[] models = new ConfiguredModel[1];
		models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(block.getAgeProperty()),
				new ResourceLocation(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + textureName + state.getValue(block.getAgeProperty()))
				));
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
		return models().withExistingParent(name, new ResourceLocation(HarderOres.MODID, ModelProvider.BLOCK_FOLDER + "/hard_ore"))
				.texture("all", baseRL)
				.texture("overlay", overlayRL);
	}

	private void createSimpleFlatItemModel(Block block) {
		ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(block.asItem());
		itemModels().getBuilder(registryName.toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", new ResourceLocation(registryName.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + registryName.getPath()));
	}

	private void createSimpleFlatItemModel(Block block, ResourceLocation itemTexture) {
		ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(block.asItem());
		itemModels().getBuilder(registryName.toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", new ResourceLocation(itemTexture.getNamespace(), itemTexture.getPath()));
	}

	private void createSimpleFlatItemModel(Block block, String directory, ResourceLocation itemTexture) {
		ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(block.asItem());
		itemModels().getBuilder(registryName.toString())
			.parent(new ModelFile.UncheckedModelFile("item/generated"))
			.texture("layer0", new ResourceLocation(itemTexture.getNamespace(), directory + "/" + itemTexture.getPath()));
	}

	private <T extends Comparable<T>> ItemModelBuilder createVariantItemModel(Block block, Property<T> property, Function<? super Property.Value<T>, Float> converter, Function<? super Property.Value<T>, String> nameConverter) {
		Item item = block.asItem();
		ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(item);
		ItemModelBuilder modelNormal = itemModels().getBuilder(registryName.toString());
		property.getAllValues().forEach(value ->{
			//ResourceLocation harderOreBlockName = new ResourceLocation(HarderOres.MODID, "ore_hard"+stone_+ore);
			ModelFile model16 = new ModelFile.ExistingModelFile(
					new ResourceLocation(registryName.getNamespace(),
							ModelProvider.BLOCK_FOLDER + "/" +  registryName.getPath() + nameConverter.apply(value)), fileHelper);
			modelNormal.override()
				.predicate(new ResourceLocation(HarderOres.MODID,BlockProperties.ORE_DENSITY.getName()), converter.apply(value))
				.model(model16)
				.end();
		});
		return modelNormal;
	}
}
