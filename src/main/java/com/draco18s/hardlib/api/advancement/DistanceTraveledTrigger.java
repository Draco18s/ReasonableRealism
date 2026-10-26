package com.draco18s.hardlib.api.advancement;

import com.draco18s.hardlib.HardLib;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;

public class DistanceTraveledTrigger extends HardLibCriteriaTrigger<DistanceTraveledTrigger.Instance> {
	public static final Identifier ID = HardLib.getRL("distance_traveled");
	public static ContextKey<Float> DISTANCE = new ContextKey<Float>(ID);
	public static ContextKey<TravelType> TRAVEL = new ContextKey<TravelType>(HardLib.getRL("travel_type"));
	public static ContextKeySet requiredParams = new ContextKeySet.Builder().required(DistanceTraveledTrigger.TRAVEL).required(DistanceTraveledTrigger.DISTANCE).build();

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	/** @param distance In blocks. */
	public record Instance(float distance, TravelType type) implements ICriterionTriggerInstanceTester {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				Codec.FLOAT.fieldOf("distance").forGetter(Instance::distance),
				TravelType.CODEC.fieldOf("travel_type").forGetter(Instance::type)
			).apply(inst, Instance::new));

		@Override
		public boolean test(LootParams params) {
			return params.contextMap().getOrThrow(TRAVEL) == type && params.contextMap().getOrThrow(DISTANCE) >= distance;
		}
	}

	public enum TravelType implements StringRepresentable {
		UNKNOWN,
		WALK,
		WALK_ON_WATER,
		WALK_UNDER_WATER,
		FLY,
		HORSE,
		PIG,
		BOAT,
		RAIL;

		public static final Codec<TravelType> CODEC = StringRepresentable.fromEnum(TravelType::values);

		/** The constant name ("RAIL"), as the advancement files have always used. */
		@Override
		public String getSerializedName() {
			return name();
		}
	}
}
