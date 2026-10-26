package com.draco18s.flowers;

import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.neoforged.neoforge.common.ModConfigSpec;

/** config/oreflowers-common.toml */
public class FlowersConfig {
	public static final ModConfigSpec SPEC;
	public static final ModConfigSpec.IntValue scanDepthSlices;
	public static final ModConfigSpec.BooleanValue poopBonemealFlowers;
	private static final Dictator DEFAULT, CLOSE, RARE, CLOSE_RARE, COMMON, CLOSE_COMMON;

	/** A dictator's settings: 1-in-chance per ore block of a flower cluster, up to distance blocks away. */
	private record Dictator(ModConfigSpec.IntValue chance, ModConfigSpec.IntValue distance) {
		OreFlowerDictator build() {
			return new OreFlowerDictator(chance.get(), distance.get());
		}
	}

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		builder.push("general");
		scanDepthSlices = builder
				.comment("How many 8-block slices below a bonemealed block are searched for ore (4 = 32 blocks)")
				.translation(OreFlowers.MODID + ".config.scanDepthSlices")
				.defineInRange("scanDepthSlices", 4, 0, 48);
		poopBonemealFlowers = builder
				.comment("If bonemeal used by machines (dispensers) can also grow ore flowers, at a much lower chance")
				.translation(OreFlowers.MODID + ".config.poopBonemealFlowers")
				.define("poopBonemealFlowers", true);
		builder.pop();
		builder.comment("How often ore flowers spawn over ore during world generation, and how far from it. Each ore uses one of these.").push("dictators");
		DEFAULT = dictator(builder, "default", 200, 30);
		CLOSE = dictator(builder, "close", 200, 15);
		RARE = dictator(builder, "rare", 400, 30);
		CLOSE_RARE = dictator(builder, "closeRare", 400, 15);
		COMMON = dictator(builder, "common", 100, 30);
		CLOSE_COMMON = dictator(builder, "closeCommon", 100, 15);
		builder.pop();
		SPEC = builder.build();
	}

	private static Dictator dictator(ModConfigSpec.Builder builder, String name, int chance, int distance) {
		return new Dictator(
				builder.comment("Chance of spawning flowers: 1/chance").defineInRange(name + "_chance", chance, 1, Integer.MAX_VALUE),
				builder.comment("Flower spawn range (in blocks)").defineInRange(name + "_distance", distance, 0, 160));
	}

	/** Sets OreFlowerDictator's dictators from the config (once it has loaded). */
	static void applyDictators() {
		OreFlowerDictator.defaultDictator = DEFAULT.build();
		OreFlowerDictator.closeDictator = CLOSE.build();
		OreFlowerDictator.rareDictator = RARE.build();
		OreFlowerDictator.closeRareDictator = CLOSE_RARE.build();
		OreFlowerDictator.commonDictator = COMMON.build();
		OreFlowerDictator.closeCommonDictator = CLOSE_COMMON.build();
	}
}
