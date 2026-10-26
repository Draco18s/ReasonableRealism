package com.draco18s.farming.item;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StemBlock;
import net.neoforged.fml.util.ObfuscationReflectionHelper;

/**
 * Thermometer and hydrometer: show the local biome's temperature or rainfall as the item's "time" model
 * property, optionally as felt by a linked crop (right-click a crop to link it, anything else to unlink).
 */
public abstract class ClimateGaugeItem extends Item {
	protected ClimateGaugeItem(Item.Properties props) {
		super(props);
	}

	/** The biome's base reading. */
	protected abstract float reading(Biome biome);

	/** The tag key the linked crop's offset is stored under. */
	protected abstract String offsetKey();

	/** Model index shown when there's nothing to read (in a GUI, say). */
	protected abstract int idleFrame();

	/** The value for the "time" item property: 0-11, from cold/dry to hot/wet. */
	public float getFrame(ItemStack stack, @Nullable Level world, @Nullable ItemOwner owner) {
		if(world == null && owner != null) {
			world = owner.level();
		}
		if(world == null || owner == null) {
			return idleFrame();
		}
		BlockPos pos = BlockPos.containing(owner.position());
		float t = reading(world.getBiome(pos).value());
		t += stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getFloatOr(offsetKey(), 0);
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
		return InteractionResult.SUCCESS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, tooltip, flag);
		CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
		if(tag.contains("linkedCropName")) {
			tooltip.accept(Component.translatable("tooltip.harderfarming.linkedcrop").withStyle(ChatFormatting.ITALIC)
					.append(" ").append(Component.translatable(tag.getStringOr("linkedCropName", ""))));
			tooltip.accept(Component.translatable("tooltip.harderfarming.unlink"));
		}
		else {
			tooltip.accept(Component.translatable("tooltip.harderfarming.link"));
		}
	}

	/** The block a stem grows: StemBlock keeps it private (and blocks have no codecs to write it out any more). */
	private static Block stemFruit(StemBlock stem) {
		ResourceKey<Block> fruit = ObfuscationReflectionHelper.getPrivateValue(StemBlock.class, stem, "fruit");
		return BuiltInRegistries.BLOCK.getOptional(fruit).orElse(stem);
	}

	public static class Thermometer extends ClimateGaugeItem {
		public Thermometer(Item.Properties props) {
			super(props);
		}

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
		public Hydrometer(Item.Properties props) {
			super(props);
		}

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
