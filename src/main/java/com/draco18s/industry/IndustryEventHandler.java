package com.draco18s.industry;

import com.draco18s.hardlib.api.recipes.RecipeToolMold;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IndustryEventHandler {
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		/*Item itemMold = ExpandedIndustryBase.itemMold;
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_AXE, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_SHOVEL, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_PICKAXE, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_HOE, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_SWORD, itemMold));
		
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_HELMET, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_CHESTPLATE, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_LEGGINGS, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.IRON_BOOTS, itemMold));

		event.getRegistry().register(new RecipeToolMold(itemMold, Items.SHEARS, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, Items.BUCKET, itemMold));
		event.getRegistry().register(new RecipeToolMold(itemMold, new ItemStack(Blocks.RAIL, 16), itemMold));*/
	}
}
