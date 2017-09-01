package com.draco18s.flowers.item;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.draco18s.flowers.OreFlowersBase;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.blockproperties.ores.EnumOreType;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.OreFlowerData;
import com.draco18s.hardlib.api.internal.OreFlowerDictator;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOreManipulator extends Item {
	public final BlockWrapper oreType;

	public ItemOreManipulator(BlockWrapper type) {
		oreType = type;
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.TOOLS);
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(hand == EnumHand.OFF_HAND || world.isRemote) return EnumActionResult.PASS;
		if(oreType != null) {
			if(player.isSneaking()) {
				HardLibAPI.oreData.adjustOreData(world, pos.down(0), oreType, -32);
				HardLibAPI.oreData.adjustOreData(world, pos.down(8), oreType, -32);
			}
			else {
				HardLibAPI.oreData.adjustOreData(world, pos.down(0), oreType, 32);
				HardLibAPI.oreData.adjustOreData(world, pos.down(8), oreType, 32);
			}
			player.sendMessage(new TextComponentString(oreType + " now " + HardLibAPI.oreData.getOreData(world, pos, oreType)));
		}
		else {
			Map<BlockWrapper, Tuple<OreFlowerDictator, List<OreFlowerData>>> list = HardLibAPI.oreFlowers.getOreList();
			List<OreFlowerData> entry;
			for(BlockWrapper ore : list.keySet()) {
				if(player.isSneaking()) {
					int count1 = HardLibAPI.oreData.getOreData(world, pos, ore);
					int count2 = HardLibAPI.oreData.getOreData(world, pos.down(8), ore);
					int count3 = HardLibAPI.oreData.getOreData(world, pos.down(16), ore);
					int count4 = HardLibAPI.oreData.getOreData(world, pos.down(24), ore);
					
					player.sendMessage(new TextComponentString(ore + " is " + count1 + "/" + count2 + "/" + count3 + "/" + count4));
				}
				else {
					int count = HardLibAPI.oreData.getOreData(world, pos, ore) +
							HardLibAPI.oreData.getOreData(world, pos.down(8), ore) +
							HardLibAPI.oreData.getOreData(world, pos.down(16), ore) +
							HardLibAPI.oreData.getOreData(world, pos.down(24), ore);
					if(count > 0)
						player.sendMessage(new TextComponentString(ore + " is " + count));
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag advanced) {
		super.addInformation(stack, worldIn, tooltip, advanced);
		if(oreType != null) {
			tooltip.add(I18n.format("tooltip:oreflowers:datamanipulation"));
			tooltip.add(I18n.format("tooltip:oreflowers:datashiftmanipulation"));
		}
		else {
			tooltip.add(I18n.format("tooltip:oreflowers:datainfo"));
			tooltip.add(I18n.format("tooltip:oreflowers:datashiftinfo"));
		}
	}
}
