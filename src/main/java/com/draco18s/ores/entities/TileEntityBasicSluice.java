package com.draco18s.ores.entities;

import java.util.List;
import java.util.Random;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.internal.BlockWrapper;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.block.BlockSluice;
import com.draco18s.ores.entities.capabilities.SiftableItemsHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class TileEntityBasicSluice extends TileEntity implements ITickable {
	public static Item itemGravel;
	public static Item itemSand;
	public static Item itemDirt;
	public static int cycleLength;
	
	protected ItemStackHandler inputSlot;

	private int waterAmount;
	private AxisAlignedBB suckZone;
	private int downstremrequests;
	private int timer = 0;
	private Random rand;

	public TileEntityBasicSluice() {
		super();
		if (itemGravel == null) {
			itemGravel = Item.getItemFromBlock(Blocks.GRAVEL);
			itemSand = Item.getItemFromBlock(Blocks.SAND);
			itemDirt = Item.getItemFromBlock(Blocks.DIRT);
		}
		inputSlot = new ItemStackHandler();
		rand = new Random();
	}

	@Override
	public void update() {
		downstremrequests = Math.min(downstremrequests, 3);
		suckItems();
		updateWater();
		if(timer < 0) {
			timer++;
		}
		else if(timer > 0) {
			timer--;
			if(timer % (cycleLength*5) == 0) {
				doFilter();
			}
			if(timer == 0) {
				subtractDirt();
			}
		}
		else if(waterAmount > 0 && inputSlot.getStackInSlot(0) != null) {
			timer = 25 * cycleLength;
		}
		if(downstremrequests > 0 && inputSlot.getStackInSlot(0).stackSize > 1) {
			float rx = 0.5F;
			float ry = rand.nextFloat() * 0.5F + 0.5F;
			float rz = 0.5F;

			EntityItem entityItem = new EntityItem(worldObj,
					pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
					inputSlot.getStackInSlot(0).splitStack(1));

			Vec3d flowVec = BlockSluice.getFlowVec(worldObj.getBlockState(pos));
			entityItem.motionX = flowVec.xCoord*0.014D;
			entityItem.motionY = 0;
			entityItem.motionZ = flowVec.zCoord*0.014D;
			entityItem.setInfinitePickupDelay();
			worldObj.spawnEntityInWorld(entityItem);
		}
	}

	private void doFilter() {
		//TODO: failure rate
		//if(worldObj.isRemote || rand.nextInt(20) >= 7) return;
		Block b = HardLibAPI.oreMachines.getRandomSluiceResult(this.rand, inputSlot.getStackInSlot(0).getItem());
		//TODO this should be an error
		if(b == null) return;
		BlockWrapper ore = new BlockWrapper(b,16);
		if(b == Blocks.GRAVEL && inputSlot.getStackInSlot(0).getItem() == itemGravel) {
			mergeStacks(new ItemStack(Items.FLINT));
		}
		else {
			int best = 0, cur;
			BlockPos bestLoc = BlockPos.ORIGIN;
			for(int j = -1; j <= 1; j++) {
				for(int k = -1; k <= 1; k++) {
					BlockPos lookPos = pos.add(16*j, 0, 16*k);
					cur = HardLibAPI.oreData.getOreData(worldObj, lookPos, ore);
					if(cur > best) {
						bestLoc = lookPos.down(0);
						best = cur;
					}
				}
			}
			if(best > 0) {
				HardLibAPI.oreData.adjustOreData(worldObj, bestLoc, ore, 1);
				IBlockState oreState;
				if(ore.meta >= 0) oreState = ore.block.getStateFromMeta(ore.meta);
				else oreState = ore.block.getDefaultState();
				ItemStack basicDrop = new ItemStack(ore.block.getItemDropped(oreState, rand, 0));
				ItemStack toSpawn = HardLibAPI.oreMachines.getMillResult(basicDrop);
				if(toSpawn != null) {
					toSpawn.copy();
					if(inputSlot.getStackInSlot(0).getItem() == itemGravel) {
						if(rand.nextInt(10) == 0) {
							toSpawn = basicDrop.copy();
						}
						else {
							//toSpawn = HardLibAPI.oreMachines.getMillResult(basicDrop).copy();
						}
					}
					else if(inputSlot.getStackInSlot(0).getItem() == itemSand) {
						//toSpawn = HardLibAPI.oreMachines.getMillResult(basicDrop).copy();
						if(rand.nextInt(10) == 0) {
							toSpawn.stackSize = 2;
						}
					}
					else {
						//toSpawn = HardLibAPI.oreMachines.getMillResult(basicDrop).copy();
					}
				}
				else {
					toSpawn = basicDrop;
				}
				mergeStacks(toSpawn);
			}
		}
	}
	
	private void subtractDirt() {
		if(inputSlot.getStackInSlot(0).stackSize > 1) inputSlot.getStackInSlot(0).stackSize--;
		else inputSlot.setStackInSlot(0, null);
	}

	private void suckItems() {
		EntityItem ent;
		List<EntityItem> ents = worldObj.getEntitiesWithinAABB(EntityItem.class, getAABB(pos.getX(), pos.getY() + 1, pos.getZ()));
		if (ents.size() > 0) {
			ItemStack stack;
			if(inputSlot.getStackInSlot(0) == null) {
				for (int e = ents.size() - 1; e >= 0; e--) {
					ent = (EntityItem) ents.get(e);
					stack = ent.getEntityItem();
					if (stack.getItem() == itemGravel || stack.getItem() == itemSand || (OresBase.sluiceAllowDirt && stack.getItem() == itemDirt)) {
						if (stack.stackSize > 1) {
							inputSlot.setStackInSlot(0, stack.splitStack(1));
						} else {
							inputSlot.setStackInSlot(0, stack.copy());
							ent.setDead();
						}
						break;
					}
				}
			}
		}
		boolean flowItemsTowards = true;
		if(flowItemsTowards && waterAmount >= 2) {
			IBlockState state = worldObj.getBlockState(pos);
			EnumFacing dir = state.getValue(BlockSluice.FACING).getOpposite();
			IBlockState source = this.worldObj.getBlockState(pos.offset(dir));
			if (source.getBlock() == Blocks.WATER || source.getBlock() == Blocks.FLOWING_WATER) {
				ents = worldObj.getEntitiesWithinAABB(EntityItem.class, getAABB(pos.offset(dir.getOpposite()),pos.up().offset(dir).offset(dir.rotateY())));
			}
			else {
				ents = worldObj.getEntitiesWithinAABB(EntityItem.class, getAABB(pos));
			}
			if(ents.size() > 0) {
				for(int e = ents.size()-1; e >= 0; e--) {
					ent = (EntityItem) ents.get(e);
					Item it = ent.getEntityItem().getItem();
					if(it == itemGravel || it == itemSand || (OresBase.sluiceAllowDirt && it == itemDirt)) {
						ent.motionX += BlockSluice.getFlowVec(state).xCoord*0.014D;
						ent.motionZ += BlockSluice.getFlowVec(state).zCoord*0.014D;
					}
				}
			}
		}
	}
	
	private void mergeStacks(ItemStack stack) {
		if(worldObj.isRemote) return;
		float rx = 0.4F + rand.nextFloat()*0.2f;
		float ry = rand.nextFloat() * 0.25F + 0.25F;
		float rz = 0.4F + rand.nextFloat()*0.2f;

		EntityItem entityItem = new EntityItem(worldObj,pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,stack);

		entityItem.motionX = 0;
		entityItem.motionY = 0;
		entityItem.motionZ = 0;
		entityItem.setPickupDelay(10);
		worldObj.spawnEntityInWorld(entityItem);
	}

	private AxisAlignedBB getAABB(BlockPos p) {
		return new AxisAlignedBB(p.getX(), p.getY(), p.getZ(), p.getX()+1, p.getY()+.25, p.getZ()+1);
	}

	private AxisAlignedBB getAABB(BlockPos p1, BlockPos p2) {
		return new AxisAlignedBB(Math.min(p1.getX(), p2.getX()), Math.min(p1.getY(), p2.getY()), Math.min(p1.getZ(), p2.getZ()),Math.max(p1.getX(), p2.getX()), Math.max(p1.getY(), p2.getY()), Math.max(p1.getZ(), p2.getZ()));
	}

	private AxisAlignedBB getAABB(int x, int y, int z) {
		if (suckZone == null) {
			suckZone = new AxisAlignedBB(x, y, z, x + 1, y + 0.25, z + 1);
		}
		return suckZone;
	}

	private void updateWater() {
		int prevWater = waterAmount;
		IBlockState state = this.worldObj.getBlockState(pos);
		EnumFacing dir = state.getValue(BlockSluice.FACING).getOpposite();

		IBlockState source = this.worldObj.getBlockState(pos.offset(dir));

		if (source.getBlock() == Blocks.WATER || source.getBlock() == Blocks.FLOWING_WATER) {
			waterAmount = 8 - source.getValue(BlockLiquid.LEVEL);
		} else if (source.getBlock() == OresBase.sluice) {
			waterAmount = ((TileEntityBasicSluice) worldObj
					.getTileEntity(pos.offset(dir))).getWaterAmount() - 2;
		}
		dir = dir.rotateY();
		Material left = worldObj.getBlockState(pos.offset(dir)).getMaterial();
		Material right = worldObj.getBlockState(pos.offset(dir.getOpposite())).getMaterial();

		if (left == Material.AIR || left == Material.GROUND || left == Material.GRASS || left == Material.SAND || right == Material.AIR || right == Material.GROUND || right == Material.GRASS || right == Material.SAND) {
			waterAmount = 0;
		}
		// model vertex calculation won't handle this well.
		//else if(left == Material.CLOTH || left == Material.CLOTH) {
		//	waterAmount--;
		//}
		if (prevWater != waterAmount) {
			worldObj.markBlockRangeForRenderUpdate(pos, pos);
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
    }

	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		IBlockState bs = worldObj.getBlockState(pos);
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(inputSlot.getStackInSlot(0).stackSize > (downstremrequests>0?1:0)+1) return null;
			return (T) inputSlot;
		}
        return super.getCapability(capability, facing);
    }

	public int getWaterAmount() {
		return waterAmount > 0 ? waterAmount : 0;
	}

	public int getTime() {
		return 0;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("harderores:inputSlot", inputSlot.serializeNBT());
		compound.setInteger("harderores:timer", timer);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(inputSlot == null) {
			inputSlot = new ItemStackHandler();
		}
		if(compound.hasKey("harderores:inputSlot")) {
			inputSlot.deserializeNBT((NBTTagCompound) compound.getTag("harderores:inputSlot"));
		}
		timer = compound.getInteger("harderores:timer");
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
