package com.draco18s.farming.item;

import javax.annotation.Nullable;

import com.draco18s.farming.entities.EntityItemFrameReplacement;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemNewFrame extends ItemHangingEntity
{

	public ItemNewFrame(Class <? extends EntityHanging > entityClass)
	{
		super(entityClass);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		BlockPos blockpos = pos.offset(facing);
		ItemStack stack = player.getHeldItem(hand);

		if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && player.canPlayerEdit(blockpos, facing, stack))
		{
			EntityHanging entityhanging = this.createEntity(world, blockpos, facing);

			if (entityhanging != null && entityhanging.onValidSurface())
			{
				if (!world.isRemote)
				{
					entityhanging.playPlaceSound();
					world.spawnEntity(entityhanging);
				}

				stack.shrink(1);
			}

			return EnumActionResult.SUCCESS;
		}
		else
		{
			return EnumActionResult.FAIL;
		}
	}

	@Nullable
	private EntityHanging createEntity(World world, BlockPos pos, EnumFacing clickedSide)
	{
		return new EntityItemFrameReplacement(world, pos, clickedSide);
	}
}