package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.google.gson.JsonObject;

import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

/**
 * hardlib:advancement_earned {advancement: id} - met once the player has the named advancement. Lets an advancement
 * require more than its one tree parent (which is all vanilla tracks).
 */
public class AdvancementEarnedTrigger extends HardLibCriteriaTrigger<AdvancementEarnedTrigger.Instance> {
	private static final ResourceLocation ID = HardLib.getRL("advancement_earned");
	public static LootContextParam<ResourceLocation> ADVANCEMENT = new LootContextParam<ResourceLocation>(ID);
	public static LootContextParamSet requiredParams = LootContextParamSet.builder().required(AdvancementEarnedTrigger.ADVANCEMENT).build();

	@Override
	public ResourceLocation getId() {
		return ID;
	}

	@Override
	public Instance createInstance(JsonObject json, DeserializationContext context) {
		return new AdvancementEarnedTrigger.Instance(HardLib.parseRL(GsonHelper.getAsString(json, "advancement")));
	}

	public static class Instance implements ICriterionTriggerInstanceTester {
		private final ResourceLocation advancement;

		public Instance(ResourceLocation advancement) {
			this.advancement = advancement;
		}

		/** The advancement this criterion waits for. */
		public ResourceLocation getAdvancement() {
			return advancement;
		}

		@Override
		public ResourceLocation getCriterion() {
			return AdvancementEarnedTrigger.ID;
		}

		@Override
		public JsonObject serializeToJson(SerializationContext context) {
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("advancement", advancement.toString());
			return jsonobject;
		}

		@Override
		public boolean test(LootContext ctx) {
			return advancement.equals(ctx.getParam(ADVANCEMENT));
		}
	}
}
