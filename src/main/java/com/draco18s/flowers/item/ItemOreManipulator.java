package com.draco18s.flowers.item;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.internal.BlockWrapper;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemOreManipulator extends Item {
	public final BlockWrapper oreType;

	public ItemOreManipulator(BlockWrapper type) {
		oreType = type;
        this.setHasSubtypes(false);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.MATERIALS);
	}
	
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(player.isSneaking()) {
			HardLibAPI.oreData.adjustOreData(world, pos.down(8), oreType, -16);
		}
		else {
			HardLibAPI.oreData.adjustOreData(world, pos.down(8), oreType, 16);
		}
		return EnumActionResult.SUCCESS;
    }
}
