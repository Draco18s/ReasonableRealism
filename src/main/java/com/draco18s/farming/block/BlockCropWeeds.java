package com.draco18s.farming.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.farming.util.FarmingAchievements;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class BlockCropWeeds extends BlockCrops {
	public BlockCropWeeds() {
		setTickRandomly(true);
		setCreativeTab(null);
		setHardness(2.0F);
		setSoundType(SoundType.PLANT);
		disableStats();
	}

	@Override
	protected Item getSeed() {
		return null;
	}

	@Override
	protected Item getCrop() {
		return null;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		int age = state.getValue(BlockCrops.AGE);
		if (age < 7) {
			if (rand.nextInt(100) < 35) {
				worldIn.setBlockState(pos, state.withProperty(BlockCrops.AGE, age + 1), 2);
			}
		} else {
			weedSpread(worldIn, pos, rand);
		}
	}

	protected void weedSpread(World world, BlockPos pos, Random rand) {
		Iterable<BlockPos> list = BlockPos.getAllInBox(pos.south().west(), pos.north().east());
		world.setBlockState(pos, this.getDefaultState());
		int placed = 0;
		for (BlockPos p : list) {
			if (world.getBlockState(p.down(2)).getBlock() == Blocks.FARMLAND) {
				if (tryPlantWeeds(world, p.down(), rand)) {
					placed++;
				}
			} else if (world.getBlockState(p.down()).getBlock() == Blocks.FARMLAND) {
				if (tryPlantWeeds(world, p, rand)) {
					placed++;
				}
			} else if (world.getBlockState(p.up()).getBlock() == Blocks.FARMLAND) {
				if (tryPlantWeeds(world, p.up(2), rand)) {
					placed++;
				}
			}
		}
		if (placed < 2) {
			if (rand.nextInt(4) == 0) {
				world.setBlockState(pos.down(), Blocks.DIRT.getDefaultState());
				world.setBlockState(pos, Blocks.TALLGRASS.getDefaultState());
			} else {
				world.setBlockToAir(pos);
			}
		}
	}

	protected boolean tryPlantWeeds(World world, BlockPos pos, Random rand) {
		IBlockState growing = world.getBlockState(pos);
		if (growing.getMaterial() == Material.AIR) {
			world.setBlockState(pos, this.getDefaultState());
			return true;
		}
		if (growing.getBlock() == Blocks.CARPET && rand.nextInt(4) == 0) {
			growing.getBlock().dropBlockAsItem(world, pos, growing, 0);
			world.setBlockState(pos, this.getDefaultState());
			return true;
		}
		return false;
	}

	@Override
	@Deprecated
    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World worldIn, BlockPos pos)
    {
		float hardness = net.minecraftforge.common.ForgeHooks.blockStrength(state, player, worldIn, pos);
		ItemStack s = player.getHeldItemMainhand();
		
		ToolMaterial mat;
    	if(s != null && s.getItem() instanceof ItemHoe) {
			mat = ToolMaterial.valueOf(((ItemHoe)s.getItem()).getMaterialName());
    	}
    	else if(s != null && s.getItem().getToolClasses(s).contains("hoe")) {
    		mat = ((ItemTool)s.getItem()).getToolMaterial();
    	}
    	else {
    		return hardness;
    	}
		hardness += mat.getEfficiencyOnProperMaterial();
		int i = EnchantmentHelper.getEfficiencyModifier(player);
		
		if (i > 0 && s != null)
        {
            float f1 = (float)(i * i + 1);

            boolean canHarvest = ForgeHooks.canToolHarvestBlock(worldIn, pos, s);

            if (!canHarvest && hardness <= 1.0F)
            {
            	hardness += f1 * 0.08F;
            }
            else
            {
            	hardness += f1;
            }
        }
        return hardness / this.blockHardness / 30;
    }
	
	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, @Nullable ItemStack stack) {
    	if(stack != null) {
    		Item i = stack.getItem();
    		if(i instanceof ItemTool) {
    			stack.damageItem(2, player);
    		}
    		else if(i instanceof ItemHoe) {
    			stack.damageItem(1, player);
    		}
    	}
    	player.addStat(FarmingAchievements.killWeeds, 1);
    }
}
