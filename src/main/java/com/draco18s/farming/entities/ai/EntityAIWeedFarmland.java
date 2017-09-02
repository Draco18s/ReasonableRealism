package com.draco18s.farming.entities.ai;

import com.draco18s.farming.block.BlockCropWeeds;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAIWeedFarmland extends EntityAIMoveToBlock
{
    /** Villager that is harvesting */
    private final EntityVillager villager;
    /** 0 => harvest, 1 => replant, -1 => none */
    private int currentTask;

    public EntityAIWeedFarmland(EntityVillager villagerIn, double speedIn)
    {
        super(villagerIn, speedIn, 16);
        this.villager = villagerIn;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (this.runDelay <= 0)
        {
            if (!this.villager.world.getGameRules().getBoolean("mobGriefing"))
            {
                return false;
            }

            this.currentTask = -1;
        }

        boolean ret = super.shouldExecute();
        runDelay /= 5;
        return ret;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.currentTask >= 0 && super.shouldContinueExecuting();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        super.updateTask();
        this.villager.getLookHelper().setLookPosition((double)this.destinationBlock.getX() + 0.5D, (double)(this.destinationBlock.getY() + 1), (double)this.destinationBlock.getZ() + 0.5D, 10.0F, (float)this.villager.getVerticalFaceSpeed());

        if (this.getIsAboveDestination())
        {
            World world = this.villager.world;
            BlockPos blockpos = this.destinationBlock.up();
            IBlockState iblockstate = world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (this.currentTask == 0 && block instanceof BlockCrops)
            {
                world.destroyBlock(blockpos, true);
            }
            else if (this.currentTask == 1 && iblockstate.getMaterial() == Material.AIR)
            {
                
            }

            this.currentTask = -1;
            this.runDelay = 10;
        }
    }

    /**
     * Return true to set given position as destination
     */
    protected boolean shouldMoveTo(World worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos).getBlock();

        if (block == Blocks.FARMLAND)
        {
            pos = pos.up();
            IBlockState iblockstate = worldIn.getBlockState(pos);
            block = iblockstate.getBlock();

            if (block instanceof BlockCropWeeds  && (this.currentTask == 0 || this.currentTask < 0))
            {
                this.currentTask = 0;
                return true;
            }

            /*if (iblockstate.getMaterial() == Material.AIR && (this.currentTask == 1 || this.currentTask < 0))
            {
                this.currentTask = 1;
                return true;
            }*/
        }

        return false;
    }
}