package com.draco18s.farming.integration;

import java.util.List;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.interfaces.IItemWithMeshDefinition;
import com.draco18s.hardlib.api.recipes.RecipeToolMold;
import com.draco18s.industry.ExpandedIndustryBase;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class IndustryIntegration {
	public static void addMoldRecipes() {
		if(HardLibAPI.itemMold != null) {
			ItemStack knife = new ItemStack(FarmingBase.butcherKnife);
			GameRegistry.addRecipe(new RecipeToolMold(HardLibAPI.itemMold, knife, HardLibAPI.itemMold, "harderfarming"));
		}
	}
}
