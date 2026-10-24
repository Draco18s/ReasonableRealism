package com.draco18s.farming.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.hardlib.HardLib;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;
import com.mojang.serialization.JsonOps;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;

/**
 * Thermometer and hydrometer: show the local biome's temperature or rainfall as the item's "time" model
 * property, optionally as felt by a linked crop (right-click a crop to link it, anything else to unlink).
 */
public abstract class ClimateGaugeItem extends Item {
	protected ClimateGaugeItem() {
		super(new Item.Properties());
	}

	/** The biome's base reading. */
	protected abstract float reading(Biome biome);

	/** The tag key the linked crop's offset is stored under. */
	protected abstract String offsetKey();

	/** Model index shown when there's nothing to read (in a GUI, say). */
	protected abstract int idleFrame();

	/** The value for the "time" item property: 0-11, from cold/dry to hot/wet. */
	public float getFrame(ItemStack stack, @Nullable Level world, @Nullable Entity entity) {
		if(world == null && entity != null) {
			world = entity.level();
		}
		if(world == null || entity == null) {
			return idleFrame();
		}
		BlockPos pos = entity.blockPosition();
		float t = reading(world.getBiome(pos).value());
		t += stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getFloat(offsetKey());
		float n = Math.round((t + 0.35f) * 4);
		return Math.max(Math.min(n, 11), 0);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Block block = context.getLevel().getBlockState(context.getClickedPos()).getBlock();
		ItemStack stack = context.getItemInHand();
		if(HardLibAPI.hardCrops.isCropBlock(block)) {
			CropWeatherOffsets offsets = HardLibAPI.hardCrops.getCropOffsets(block);
			if(block instanceof StemBlock stem) {
				block = stemFruit(stem);
			}
			String cropName = block.getDescriptionId();
			CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
				tag.putString("linkedCropName", cropName);
				tag.putFloat("rainflat", offsets.rainfallFlat);
				tag.putFloat("tempflat", offsets.temperatureFlat);
			});
		}
		else {
			stack.remove(DataComponents.CUSTOM_DATA);
		}
		return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if(tag.contains("linkedCropName")) {
			tooltip.add(Component.translatable("tooltip.harderfarming.linkedcrop").withStyle(ChatFormatting.ITALIC)
					.append(" ").append(Component.translatable(tag.getString("linkedCropName"))));
			tooltip.add(Component.translatable("tooltip.harderfarming.unlink"));
		}
		else {
			tooltip.add(Component.translatable("tooltip.harderfarming.link"));
		}
	}

	/** The block a stem grows: StemBlock keeps it private (since 1.20.3), but its codec still writes it out. */
	private static Block stemFruit(StemBlock stem) {
		return StemBlock.CODEC.codec().encodeStart(JsonOps.INSTANCE, stem).result()
				.map(json -> json.getAsJsonObject().get("fruit").getAsString())
				.map(id -> BuiltInRegistries.BLOCK.get(HardLib.parseRL(id))).orElse(stem);
	}

	public static class Thermometer extends ClimateGaugeItem {
		@Override
		protected float reading(Biome biome) {
			return biome.getModifiedClimateSettings().temperature();
		}

		@Override
		protected String offsetKey() {
			return "tempflat";
		}

		@Override
		protected int idleFrame() {
			return 6;
		}
	}

	public static class Hydrometer extends ClimateGaugeItem {
		@Override
		protected float reading(Biome biome) {
			return biome.getModifiedClimateSettings().downfall();
		}

		@Override
		protected String offsetKey() {
			return "rainflat";
		}

		@Override
		protected int idleFrame() {
			return 6;
		}
	}
}
