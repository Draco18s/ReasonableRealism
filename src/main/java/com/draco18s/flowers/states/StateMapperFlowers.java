package com.draco18s.flowers.states;

import java.util.Map;
import java.util.Map.Entry;

import com.draco18s.hardlib.blockproperties.Props;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperFlowers extends StateMapperBase {
	private final Object type_prop;
	
	public StateMapperFlowers(Object prop) {
		type_prop = prop;
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
		ResourceLocation loc;
		if(state.getValue(Props.FLOWER_STALK)) {
			loc = new ResourceLocation(state.getBlock().getRegistryName() + "_tall");
		}
		else {
			loc = (ResourceLocation)Block.REGISTRY.getNameForObject(state.getBlock());
		}
		String str = getPropertyString(state.getProperties());
		ModelResourceLocation p = new ModelResourceLocation(loc, str);
		return p;
	}
	
	public String getPropertyString(Map < IProperty<?>, Comparable<? >> values) {
		StringBuilder stringbuilder = new StringBuilder();

		for (Entry < IProperty<?>, Comparable<? >> entry : values.entrySet())
		{
			if (stringbuilder.length() != 0)
			{
				stringbuilder.append(",");
			}

			IProperty<?> iproperty = (IProperty)entry.getKey();
			
			if(iproperty.equals(Props.FLOWER_STALK)) {
				continue;
			}
			
			stringbuilder.append(iproperty.getName());
			stringbuilder.append("=");
			stringbuilder.append(this.getPropertyName(iproperty, (Comparable)entry.getValue()));
		}

		if (stringbuilder.length() == 0)
		{
			stringbuilder.append("normal");
		}

		return stringbuilder.toString();
	}
	
	private <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
		return property.getName((T)value);
	}
}
