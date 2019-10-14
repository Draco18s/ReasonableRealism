package com.draco18s.harderfarming.item;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ButcherKnifeItem extends ToolItem {
	private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.SLIME_BLOCK, Blocks.SPONGE, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.MELON});
	
	public ButcherKnifeItem(ItemTier material) {
		super((material.getAttackDamage()/2f)-1, -1f, material, EFFECTIVE_ON, new Properties().group(ItemGroup.TOOLS).maxDamage(material.getMaxUses()/2));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(new TranslationTextComponent("tooltip.harderfarming:damagebonus", 10));
	}
}
