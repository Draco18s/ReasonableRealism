package com.draco18s.farming.entities.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class EntityAIMakeRoomForSeeds extends EntityAIBase {
	private final EntityVillager villager;
	
	
    public EntityAIMakeRoomForSeeds(EntityVillager villagerIn) {
    	villager = villagerIn;
        this.setMutexBits(5);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute() {
        return hasWheat() && !hasSpaceForSeeds();
    }

	private boolean hasSpaceForSeeds() {
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
			if(itemstack.isEmpty() || itemstack.getItem() == Items.WHEAT_SEEDS) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasWheat() {
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
			if(!itemstack.isEmpty() && itemstack.getItem() == Items.WHEAT) {
				return true;
			}
		}
		return false;
	}
	
	public void updateTask() {
		super.updateTask();
		for (int i = 0; i < villager.getVillagerInventory().getSizeInventory(); ++i) {
			ItemStack itemstack = villager.getVillagerInventory().getStackInSlot(i);
			if(!itemstack.isEmpty() && itemstack.getItem() == Items.WHEAT) {
				tossItem(itemstack.copy(), villager);
				villager.getVillagerInventory().setInventorySlotContents(i, ItemStack.EMPTY);
				return;
			}
		}
	}

	private static void tossItem(ItemStack itemstack, EntityVillager villager) {
		double d0 = villager.posY - 0.30000001192092896D + (double)villager.getEyeHeight();
        EntityItem entityitem = new EntityItem(villager.world, villager.posX, d0, villager.posZ, itemstack);
        float f = 0.3F;
        float f1 = villager.rotationYawHead;
        float f2 = villager.rotationPitch;
        entityitem.motionX = (double)(-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F) * 0.3F);
        entityitem.motionZ = (double)(MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F) * 0.3F);
        entityitem.motionY = (double)(-MathHelper.sin(f2 * 0.017453292F) * 0.3F + 0.1F);
        entityitem.setDefaultPickupDelay();
        villager.world.spawnEntity(entityitem);
	}
}
