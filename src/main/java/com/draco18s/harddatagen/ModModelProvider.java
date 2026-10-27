package com.draco18s.harddatagen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.client.ClimateGaugeProperty;
import com.draco18s.flowers.OreFlowers;
import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.harderores.client.OreDensityProperty;
import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.internal.OreNameHelper;
import com.draco18s.industry.ExpandedIndustry;
import com.mojang.math.Quadrant;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Block states, block and item models, and item model definitions (vanilla's model generators, which replaced
 * NeoForge's block state and item model providers). One provider per mod: modid picks which mod's blocks and items
 * it generates.
 */
public class ModModelProvider extends ModelProvider {
	/** Hard ores: the vanilla ore texture under an overlay of stone that grows as the density drops. */
	private static final TextureSlot OVERLAY = TextureSlot.create("overlay");
	private static final ModelTemplate HARD_ORE = new ModelTemplate(Optional.of(HarderOres.getRL("block/hard_ore")), Optional.empty(), TextureSlot.ALL, OVERLAY);
	/** Rail bridges: a rail on supports. */
	private static final TextureSlot RAIL = TextureSlot.create("rail");
	private static final TextureSlot SUPPORTS = TextureSlot.create("supports");
	private static final ModelTemplate RAIL_BRIDGE = new ModelTemplate(Optional.of(ExpandedIndustry.getRL("block/rail_bridge_base")), Optional.empty(), RAIL, SUPPORTS);
	private static final Identifier AIR = HardLib.parseRL("minecraft:block/air");

	private final String modid;
	private BlockModelGenerators blocks;
	private ItemModelGenerators items;

	public ModModelProvider(PackOutput output, String modid) {
		super(output, modid);
		this.modid = modid;
	}

	/** The tanning rack's and the farmland variants' block states are written by hand (they're multiparts). */
	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return super.getKnownBlocks().filter(block -> block.value() != HarderFarming.ModBlocks.tanner
				&& block.value() != HarderFarming.ModBlocks.weedy_farmland && block.value() != HarderFarming.ModBlocks.mulched_farmland);
	}

	@Override
	protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
		blocks = blockModels;
		items = itemModels;
		switch(modid) {
			case ExpandedIndustry.MODID -> industry();
			case HarderOres.MODID -> harderOres();
			case HarderFarming.MODID -> farming();
			case OreFlowers.MODID -> flowers();
			default -> throw new IllegalArgumentException("No models for " + modid);
		}
	}

	private void industry() {
		passiveRail(ExpandedIndustry.ModBlocks.rail_bridge, ExpandedIndustry.getRL("block/rail_bridge"), HardLib.parseRL("minecraft:block/oak_planks"));
		activeRail(ExpandedIndustry.ModBlocks.powered_rail_bridge, ExpandedIndustry.getRL("block/rail_bridge_powered"), HardLib.parseRL("minecraft:block/oak_planks"));
		extHopper(ExpandedIndustry.ModBlocks.machine_wood_hopper, ExpandedIndustry.getRL("item/wood_hopper"));
		extHopper(ExpandedIndustry.ModBlocks.machine_distributor, ExpandedIndustry.getRL("item/distributor"));
	}

	private void harderOres() {
		millstone(HarderOres.ModBlocks.machine_millstone, HarderOres.getRL("block/mill"));
		simpleOre(HarderOres.ModBlocks.ore_limonite);
		genHardOres();
		sluice(HarderOres.ModBlocks.sluice);
		prebuilt(HarderOres.ModBlocks.machine_sifter, HarderOres.getRL("block/machine_sifter"));
		Identifier packager = ModelTemplates.CUBE_TOP.create(HarderOres.ModBlocks.packager, new TextureMapping()
				.put(TextureSlot.SIDE, tex(HarderOres.getRL("block/packager-side")))
				.put(TextureSlot.TOP, tex(HarderOres.getRL("block/packager-top"))), blocks.modelOutput);
		prebuilt(HarderOres.ModBlocks.packager, packager);
		axel(HarderOres.ModBlocks.machine_axel);
		windvane(HarderOres.ModBlocks.machine_windvane);

		flatItem(HarderOres.ModItems.diamond_studded_pick, "diamond_stud_pickaxe");
		flatItem(HarderOres.ModItems.diamond_studded_axe, "diamond_stud_axe");
		flatItem(HarderOres.ModItems.diamond_studded_shovel, "diamond_stud_shovel");
		flatItem(HarderOres.ModItems.diamond_studded_hoe, "diamond_stud_hoe");
		flatItem(HarderOres.ModItems.copper_nugget);
		flatItem(HarderOres.ModItems.orechunk_limonite, "limonite_chunk");
		flatItem(HarderOres.ModItems.orechunk_diamond, "diamond_chunk");
		flatItem(HarderOres.ModItems.tinydust_copper, "copper_dust_sm");
		flatItem(HarderOres.ModItems.largedust_copper, "copper_dust_lg");
		flatItem(HarderOres.ModItems.tinydust_gold, "gold_dust_sm");
		flatItem(HarderOres.ModItems.largedust_gold, "gold_dust_lg");
		flatItem(HarderOres.ModItems.tinydust_iron, "iron_dust_sm");
		flatItem(HarderOres.ModItems.largedust_iron, "iron_dust_lg");
		flatItem(HarderOres.ModItems.sluice, "basic_sluice");
	}

	private void farming() {
		simpleOre(HarderFarming.ModBlocks.ore_salt);
		crops((CropBlock)HarderFarming.ModBlocks.crop_winter_wheat, "block/winter_wheat_stage_");
		weeds((CropBlock)HarderFarming.ModBlocks.crop_weeds);
		// The rack's item model is written by hand too.
		items.itemModelOutput.accept(HarderFarming.ModBlocks.tanner.asItem(), ItemModelUtils.plainModel(HarderFarming.getRL("item/tanner")));

		Item knife = HarderFarming.ModItems.butcher_knife;
		items.generateFlatItem(knife, TextureMapping.getItemTexture(knife).sprite(), ModelTemplates.FLAT_HANDHELD_ITEM);
		flatItem(HarderFarming.ModItems.tinydust_sugar, "sugar_dust_sm");
		flatItem(HarderFarming.ModItems.tinydust_flour, "flour_dust_sm");
		flatItem(HarderFarming.ModItems.largedust_flour, "flour_dust_lg");
		flatItem(HarderFarming.ModItems.winter_wheat_seeds);
		flatItem(HarderFarming.ModItems.salt);
		flatItem(HarderFarming.ModItems.salt_chunk);
		flatItem(HarderFarming.ModItems.weed_seeds, "seeds_weeds");
		flatItem(HarderFarming.ModItems.raw_leather, "leather");
		// The gauges show a frame per reading (their models are written by hand).
		gauge(HarderFarming.ModItems.thermometer, "item/temp_", 12);
		gauge(HarderFarming.ModItems.hydrometer, "item/rain_", 8);
	}

	/**
	 * Flowers are crosses of their item texture. Two-tall ones use "<name>1" for the lower half, and blooming ones
	 * use it for the not-blooming phase (bloom=false shows the "_bloom" model, as 1.14 did).
	 */
	private void flowers() {
		for(Map.Entry<String, DeferredHolder<Block, Block>> entry : OreFlowers.FLOWERS.entrySet()) {
			String name = entry.getKey();
			Block block = entry.getValue().get();
			Identifier main = flowerModel(name, name);
			if(block.defaultBlockState().hasProperty(BlockProperties.BLOOM_PHASE)) {
				Identifier other = flowerModel(name + "_bloom", name + "1");
				blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
						.with(PropertyDispatch.initial(BlockProperties.BLOOM_PHASE).generate(bloom -> variant(bloom ? main : other))));
			}
			else if(block instanceof DoublePlantBlock) {
				Identifier lower = flowerModel(name + "_down", name + "1");
				blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
						.with(PropertyDispatch.initial(DoublePlantBlock.HALF).generate(half -> variant(half == DoubleBlockHalf.UPPER ? main : lower))));
			}
			else {
				blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, variant(main)));
			}
			items.generateFlatItem(block.asItem(), HardLib.getRL(modid, "item/" + name), ModelTemplates.FLAT_ITEM);
		}
		flatItem(OreFlowers.ModItems.sticky_goo);
	}

	/** (The item textures are in the blocks atlas as block/<name> too: see assets/minecraft/atlases/blocks.json.) */
	private Identifier flowerModel(String name, String texture) {
		return ModelTemplates.CROSS.create(HardLib.getRL(modid, "block/" + name), TextureMapping.cross(tex(HardLib.getRL(modid, "block/" + texture))), blocks.modelOutput);
	}

	/** A crop's age property (CropBlock.getAgeProperty is protected now). */
	private static IntegerProperty age(CropBlock block) {
		return (IntegerProperty)block.getStateDefinition().getProperty(BlockStateProperties.AGE_7.getName());
	}

	private void crops(CropBlock block, String textureName) {
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(age(block)).generate(age -> variant(cropModel(textureName + age)))));
	}

	/** Weeds have 16 ages but 8 stages of growth to show; the later ages all look fully grown. */
	private void weeds(CropBlock block) {
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(age(block)).generate(age -> variant(HarderFarming.getRL("block/weeds_stage_" + Math.min(age, 7))))));
		for(int stage = 0; stage <= 7; stage++) {
			cropModel("block/weeds_stage_" + stage);
		}
	}

	/** A crop model named for its texture. */
	private Identifier cropModel(String texture) {
		Identifier id = HardLib.getRL(modid, texture);
		return ModelTemplates.CROP.create(id, TextureMapping.crop(tex(id)), blocks.modelOutput);
	}

	/**
	 * A model per water level (9 is drawn as 8, its water is no deeper), turned to the flow: the models slope down
	 * to the south, with the riffles across x. Facing is the way the water flows out.
	 */
	private void sluice(Block block) {
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(SluiceBlock.LEVEL, BlockStateProperties.HORIZONTAL_FACING).generate((level, facing) -> {
					Identifier model = HarderOres.getRL("block/sluice_" + Math.min(level, 8));
					return switch(facing) {
						case NORTH -> rotated(model, 0, 180);
						case EAST -> rotated(model, 0, 270);
						case WEST -> rotated(model, 0, 90);
						default -> variant(model);
					};
				})));
	}

	private void windvane(Block block) {
		Identifier model = ModelLocationUtils.getModelLocation(block);
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockStateProperties.FACING).generate(facing -> switch(facing) {
					case DOWN -> rotated(model, 180, 180);
					case EAST -> rotated(model, 90, 270);
					case NORTH, SOUTH -> rotated(model, 90, 180);
					case UP -> rotated(model, 0, 0);
					case WEST -> rotated(model, 270, 270);
				})));
		items.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
	}

	private void axel(Block block) {
		Identifier model = ModelLocationUtils.getModelLocation(block);
		Identifier gears = model.withSuffix("_gears");
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockProperties.AXEL_ORIENTATION, BlockStateProperties.HORIZONTAL_FACING).generate((orientation, dir) -> {
					int yRot = switch(dir) {
						case SOUTH -> 180;
						case EAST -> 90;
						case WEST -> 270;
						default -> 0;
					};
					return switch(orientation) {
						case GEARS -> rotated(gears, 0, yRot);
						case UP -> rotated(model, 270, 0);
						case NONE, HUB -> rotated(model, 0, yRot);
					};
				})));
		items.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
	}

	private void genHardOres() {
		OreNameHelper.DoForTextureNames((vanillaStoneTexture, vanillaOriginalOreTexture, harderOreBlockName) -> {
			Block block = BuiltInRegistries.BLOCK.getValue(harderOreBlockName);
			hardOre(block, vanillaOriginalOreTexture, harderOreBlockName.withPrefix("block/"));
		});
	}

	/** One model per density, each with its own overlay; the item shows the density it places at. */
	private void hardOre(Block block, Identifier baseTexture, Identifier overlayTexture) {
		Identifier name = ModelLocationUtils.getModelLocation(block);
		List<RangeSelectItemModel.Entry> densities = new ArrayList<RangeSelectItemModel.Entry>();
		for(int density : BlockProperties.ORE_DENSITY.getPossibleValues()) {
			Identifier model = HARD_ORE.create(name.withSuffix("_" + density), new TextureMapping()
					.put(TextureSlot.ALL, tex(baseTexture))
					.put(OVERLAY, tex(overlayTexture.withSuffix("_" + density))), blocks.modelOutput);
			densities.add(ItemModelUtils.override(ItemModelUtils.plainModel(model), density));
		}
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockProperties.ORE_DENSITY).generate(density -> variant(name.withSuffix("_" + density)))));
		rangeItem(block.asItem(), new OreDensityProperty(), name.withSuffix("_16"), densities);
	}

	private void millstone(Block block, Identifier textureName) {
		Identifier registryName = ModelLocationUtils.getModelLocation(block);
		Material side = tex(textureName.withSuffix("_side"));
		Identifier generic = ModelTemplates.CUBE_COLUMN.create(registryName, TextureMapping.column(side, tex(textureName.withSuffix("_generic"))), blocks.modelOutput);
		Identifier center = ModelTemplates.CUBE_COLUMN.create(registryName.withSuffix("_center"), TextureMapping.column(side, tex(textureName.withSuffix("_center"))), blocks.modelOutput);
		Identifier edge = ModelTemplates.CUBE_COLUMN.create(registryName.withSuffix("_side"), TextureMapping.column(side, tex(textureName.withSuffix("_edge"))), blocks.modelOutput);
		Identifier corner = ModelTemplates.CUBE_BOTTOM_TOP.create(registryName.withSuffix("_corner"), new TextureMapping()
				.put(TextureSlot.BOTTOM, tex(textureName.withSuffix("_corner2")))
				.put(TextureSlot.TOP, tex(textureName.withSuffix("_corner")))
				.put(TextureSlot.SIDE, side), blocks.modelOutput);
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockProperties.MILL_ORIENTATION).generate(orientation -> switch(orientation) {
					case NONE -> variant(generic);
					case CENTER -> variant(center);
					case EAST -> variant(edge);
					case NORTH -> rotated(edge, 0, 270);
					case SOUTH -> rotated(edge, 0, 90);
					case WEST -> rotated(edge, 0, 180);
					case NORTH_EAST -> variant(corner);
					case NORTH_WEST -> rotated(corner, 0, 270);
					case SOUTH_EAST -> rotated(corner, 0, 90);
					case SOUTH_WEST -> rotated(corner, 0, 180);
				})));
		Identifier item = ModelTemplates.CUBE_COLUMN.create(ModelLocationUtils.getModelLocation(block.asItem()),
				TextureMapping.column(tex(HarderOres.getRL("block/mill_side")), tex(HarderOres.getRL("block/mill_top"))), items.modelOutput);
		items.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(item));
	}

	/** A block with one model (written by hand or made already), which its item shows too. */
	private void prebuilt(Block block, Identifier model) {
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, variant(model)));
		items.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(model));
	}

	private void extHopper(Block block, Identifier itemTexture) {
		Identifier down = ExpandedIndustry.getRL("block/hopper");
		Identifier sideways = ExpandedIndustry.getRL("block/hopper_side");
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(HopperBlock.FACING).generate(facing -> switch(facing) {
					case DOWN -> rotated(down, 0, 180).with(BlockModelGenerators.UV_LOCK);
					case EAST -> rotated(sideways, 0, 90).with(BlockModelGenerators.UV_LOCK);
					case NORTH -> rotated(sideways, 0, 0).with(BlockModelGenerators.UV_LOCK);
					case SOUTH -> rotated(sideways, 0, 180).with(BlockModelGenerators.UV_LOCK);
					case WEST -> rotated(sideways, 0, 270).with(BlockModelGenerators.UV_LOCK);
					default -> throw new RuntimeException("Invalid hopper block state!");
				})));
		items.generateFlatItem(block.asItem(), itemTexture, ModelTemplates.FLAT_ITEM);
	}

	/** A rail bridge, straight only (the sloped shapes are never placed). */
	private void passiveRail(Block block, Identifier railTexture, Identifier supportTexture) {
		Identifier model = railBridge(ModelLocationUtils.getModelLocation(block), railTexture, supportTexture);
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate(shape -> switch(shape) {
					case NORTH_SOUTH -> variant(model);
					case EAST_WEST -> rotated(model, 0, 90);
					default -> variant(AIR);
				})));
		items.generateFlatItem(block.asItem(), railTexture, ModelTemplates.FLAT_ITEM);
	}

	/** A powered rail bridge: the "_on" rail texture while powered. */
	private void activeRail(Block block, Identifier railTexture, Identifier supportTexture) {
		Identifier railTextureOn = railTexture.withSuffix("_on");
		Identifier off = railBridge(ModelLocationUtils.getModelLocation(block), railTexture, supportTexture);
		Identifier on = railBridge(ModelLocationUtils.getModelLocation(block, "_on"), railTextureOn, supportTexture);
		blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((powered, shape) -> {
					Identifier model = powered ? on : off;
					return switch(shape) {
						case NORTH_SOUTH -> variant(model);
						case EAST_WEST -> rotated(model, 0, 90);
						default -> variant(AIR);
					};
				})));
		items.generateFlatItem(block.asItem(), railTextureOn, ModelTemplates.FLAT_ITEM);
	}

	private Identifier railBridge(Identifier name, Identifier railTexture, Identifier supportTexture) {
		return RAIL_BRIDGE.create(name, new TextureMapping().put(RAIL, tex(railTexture)).put(SUPPORTS, tex(supportTexture)), blocks.modelOutput);
	}

	private void simpleOre(Block block) {
		Identifier registryName = BuiltInRegistries.BLOCK.getKey(block);
		Identifier texture = HardLib.getRL(registryName.getNamespace(), "block/ore/" + registryName.getPath().replace("ore_", ""));
		prebuilt(block, ModelTemplates.CUBE_ALL.create(block, TextureMapping.cube(tex(texture)), blocks.modelOutput));
	}

	/** A gauge's frames (frame i shows the model named prefix + i, two digits), picked by the climate property. */
	private void gauge(Item item, String prefix, int frames) {
		List<RangeSelectItemModel.Entry> entries = new ArrayList<RangeSelectItemModel.Entry>();
		for(int i = 0; i < frames; i++) {
			entries.add(ItemModelUtils.override(ItemModelUtils.plainModel(HarderFarming.getRL(prefix + String.format("%02d", i))), i));
		}
		rangeItem(item, new ClimateGaugeProperty(), ModelLocationUtils.getModelLocation(item), entries);
	}

	private void rangeItem(Item item, RangeSelectItemModelProperty property, Identifier fallback, List<RangeSelectItemModel.Entry> entries) {
		ItemModel.Unbaked model = ItemModelUtils.rangeSelect(property, ItemModelUtils.plainModel(fallback), entries);
		items.itemModelOutput.accept(item, model);
	}

	/** A flat item showing its own texture (item/<its name>). */
	private void flatItem(Item item) {
		items.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
	}

	/** A flat item showing another texture in its namespace's item folder. */
	private void flatItem(Item item, String texture) {
		items.generateFlatItem(item, HardLib.getRL(BuiltInRegistries.ITEM.getKey(item).getNamespace(), "item/" + texture), ModelTemplates.FLAT_ITEM);
	}

	private static Material tex(Identifier texture) {
		return new Material(texture);
	}

	private static MultiVariant variant(Identifier model) {
		return BlockModelGenerators.plainVariant(model);
	}

	private static MultiVariant rotated(Identifier model, int x, int y) {
		return variant(model).with(VariantMutator.X_ROT.withValue(quadrant(x))).with(VariantMutator.Y_ROT.withValue(quadrant(y)));
	}

	private static Quadrant quadrant(int degrees) {
		return switch(degrees) {
			case 90 -> Quadrant.R90;
			case 180 -> Quadrant.R180;
			case 270 -> Quadrant.R270;
			default -> Quadrant.R0;
		};
	}
}
