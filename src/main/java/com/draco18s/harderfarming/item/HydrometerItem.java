package com.draco18s.harderfarming.item;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.harderfarming.EventHandlers;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.date.HardLibDate;
import com.draco18s.hardlib.api.internal.CropWeatherOffsets;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StemBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HydrometerItem extends Item {

	public HydrometerItem() {
		super(new Item.Properties().group(ItemGroup.MISC));
		this.addPropertyOverride(new ResourceLocation("time"), new IItemPropertyGetter() {
			@Override
			public float call(ItemStack stack, World world, LivingEntity entityIn) {
				boolean flag = entityIn != null;
				Entity entity = (Entity)(flag ? entityIn : stack.getItemFrame());
				if (world == null && entity != null) {
					world = entity.world;
				}
				if(world == null || entity == null) {
					return 6;
				}
				CompoundNBT tag = stack.getTag();

				Biome bio = world.getBiome(new BlockPos(entity.posX, 0, entity.posZ));
				float t = bio.getDownfall();

				float flat = 0;
				float time = 0;
				if(tag != null) {
					flat = tag.getFloat("rainflat");
					time = tag.getFloat("raintime");
				}
				HardLibDate.getSeasonRain(world, (long) (EventHandlers.getTotalWorldTime(world) + (HardLibDate.getYearLength(world) * time)));
				t += flat;
				float n = Math.round((t+0.35f) * 4);
				n = Math.max(Math.min(n, 11),0);
				return n;
			}
		});
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		//TODO: use capabilities rather than nbt
		BlockState state = context.getWorld().getBlockState(context.getPos());
		Block block = state.getBlock();
		ItemStack stack = context.getPlayer().getHeldItem(context.getHand());
		if(HardLibAPI.hardCrops.isCropBlock(block)) {
			CompoundNBT tag = stack.getTag();
			if(tag == null) {
				tag = new CompoundNBT();
			}
			CropWeatherOffsets offsets = HardLibAPI.hardCrops.getCropOffsets(block);
			if(block instanceof StemBlock) {
				block = ((StemBlock) block).getCrop();
			}
			tag.putString("linkedCropName", block.getTranslationKey());
			tag.putBoolean("HasOffsets", true);
			tag.putFloat("rainflat", offsets.rainfallFlat);
			tag.putFloat("tempflat", offsets.temperatureFlat);
			tag.putFloat("raintime", offsets.rainfallTimeOffset);
			tag.putFloat("temptime", offsets.temperatureTimeOffset);
			stack.setTag(tag);
		}
		else {
			stack.setTag(null);
		}
		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		CompoundNBT tag = stack.getTag();
		if(tag != null) {
			tooltip.add(new TranslationTextComponent("tooltip.harderfarming:linkedcrop.text").applyTextStyle(TextFormatting.ITALIC).appendSibling(new TranslationTextComponent(tag.getString("linkedCropName"))));
			tooltip.add(new TranslationTextComponent("tooltip.harderfarming:unlink.text"));
			//tooltip.add(TextFormatting.ITALIC + I18n.format("tooltip.harderfarming:linkedcrop.text") + " " + tag.getString("linkedCropName"));
			//tooltip.add(I18n.format("tooltip.harderfarming:unlink.text"));
		}
		else {
			tooltip.add(new TranslationTextComponent("tooltip.harderfarming:link.text"));
			//tooltip.add(I18n.format("tooltip.harderfarming:link.text"));
		}
	}
}
