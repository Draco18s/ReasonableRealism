package com.draco18s.farming;

import net.minecraftforge.common.ForgeConfigSpec;

/** config/harderfarming-common.toml */
public class FarmingConfig {
	public static final ForgeConfigSpec SPEC;
	public static final ForgeConfigSpec.BooleanValue doRawLeather;
	public static final ForgeConfigSpec.BooleanValue doSlowCrops;
	public static final ForgeConfigSpec.BooleanValue doBiomeCrops;
	public static final ForgeConfigSpec.IntValue cropsWorst;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		doRawLeather = builder
				.comment("If animals drop raw leather (which needs tanning) instead of leather")
				.translation(HarderFarming.MODID + ".config.doRawLeather")
				.define("doRawLeather", true);
		builder.pop();
		builder.push("crops");
		doSlowCrops = builder
				.comment("If crops grow slower, affected by their neighbors (rows, weeds, bare farmland)")
				.translation(HarderFarming.MODID + ".config.doSlowCrops")
				.define("doSlowCrops", true);
		doBiomeCrops = builder
				.comment("If crops grow slower outside their preferred climate (biome temperature and rainfall)")
				.translation(HarderFarming.MODID + ".config.doBiomeCrops")
				.define("doBiomeCrops", true);
		cropsWorst = builder
				.comment("The most a poor climate can slow growth: 1 in (10 + this) growth ticks succeeds")
				.translation(HarderFarming.MODID + ".config.cropsWorst")
				.defineInRange("cropsWorst", 16, 0, 100);
		builder.pop();
		SPEC = builder.build();
	}
}
