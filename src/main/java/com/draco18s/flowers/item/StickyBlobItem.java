package com.draco18s.flowers.item;


import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

/** Aveloz sap, milled out of the plant. Burns. */
public class StickyBlobItem extends Item {

	/** Burns for 150 ticks (fuel is an item component now). */
	public StickyBlobItem(Item.Properties props) {
		super(props.component(DataComponents.COOKING_FUEL, new CookingFuel(new ResolvableInt.Constant(150), ResolvableFloat.fromKey(ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER))));
	}
}
