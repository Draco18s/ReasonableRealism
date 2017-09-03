package com.draco18s.ores.entities;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.ores.OresBase;
import com.draco18s.ores.block.BlockSluice;
import com.draco18s.ores.entities.capabilities.ItemStackHandlerDirt;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

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
		inputSlot = new ItemStackHandlerDirt();
		rand = new Random();
	}

	@Override
	public void update() {
		downstremrequests = Math.min(downstremrequests, 3);
		if(world.isRemote) downstremrequests = 0;
		
		BlockPos p = pos;
		EnumFacing dir = world.getBlockState(p).getValue(BlockSluice.FACING).getOpposite();
		do {
			p=p.offset(dir,1);
		} while(world.getBlockState(p).getBlock() == OresBase.sluice);
		p = p.offset(dir.getOpposite(), 1);
		if(pos.equals(p) && !world.isRemote) {
			//OresBase.logger.log(Level.INFO, " > Requests " + downstremrequests);
		}
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
		else if(waterAmount > 0) {
			if(!inputSlot.getStackInSlot(0).isEmpty()) {
				timer = 25 * cycleLength;
			}
			else {
				makeRequest();
				timer = -5 * cycleLength;
				downstremrequests = 0;
			}
			sendUpdates();
		}
		if(downstremrequests > 0 && !inputSlot.getStackInSlot(0).isEmpty() && inputSlot.getStackInSlot(0).getCount() > 1) {
			float rx = 0.5F;
			float ry = rand.nextFloat() * 0.25F + 0.25F;
			float rz = 0.5F;

			EntityItem entityItem = new EntityItem(world,
					pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,
					inputSlot.getStackInSlot(0).splitStack(1));

			//Vec3d flowVec = BlockSluice.getFlowVec(world.getBlockState(pos));
			entityItem.motionX = 0;//flowVec.xCoord*0.014D;
			entityItem.motionY = 0;
			entityItem.motionZ = 0;//flowVec.zCoord*0.014D;
			entityItem.setPickupDelay(300);
			world.spawnEntity(entityItem);
			downstremrequests--;
			sendUpdates();
		}
	}

	private void makeRequest() {
		BlockPos p = pos;
		EnumFacing dir = world.getBlockState(p).getValue(BlockSluice.FACING).getOpposite();
		do {
			p=p.offset(dir,1);
		} while(world.getBlockState(p).getBlock() == OresBase.sluice);
		p = p.offset(dir.getOpposite(), 1);
		TileEntity te = world.getTileEntity(p);
		if(te != null && te instanceof TileEntityBasicSluice && te != this) {
			((TileEntityBasicSluice)te).downstremrequests++;
		}		
	}

	private void doFilter() {
		List<Block> list = HardLibAPI.oreMachines.getRandomSluiceResults(this.rand, inputSlot.getStackInSlot(0).getItem());
		for(Block b : list) {
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
						cur = HardLibAPI.oreData.getOreData(world, lookPos, ore);
						if(cur > best) {
							bestLoc = lookPos.down(0);
							best = cur;
						}
					}
				}
				if(best > 0) {
					HardLibAPI.oreData.adjustOreData(world, bestLoc, ore, 1);
					IBlockState oreState;
					if(ore.meta >= 0) oreState = ore.block.getStateFromMeta(ore.meta);
					else oreState = ore.block.getDefaultState();
					ItemStack basicDrop = new ItemStack(ore.block.getItemDropped(oreState, rand, 0));
					ItemStack toSpawn = HardLibAPI.oreMachines.getMillResult(basicDrop);
					if(!toSpawn.isEmpty()) {
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
								toSpawn.setCount(2);
							}
						}
						else {
							int r = rand.nextInt(20);
							if(r == 0) {
								toSpawn = basicDrop.copy();
							}
							else if(r == 1) {
								toSpawn.setCount(2);
							}
						}
					}
					else {
						toSpawn = basicDrop;
					}
					mergeStacks(toSpawn);
				}
			}
		}
	}
	
	private void sendUpdates() {
		world.markBlockRangeForRenderUpdate(pos, pos);
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
		world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}

	private IBlockState getState() {
		return world.getBlockState(pos);
	}
	
	private void subtractDirt() {
		if(inputSlot.getStackInSlot(0).getCount() > 1) inputSlot.getStackInSlot(0).shrink(1);
		else inputSlot.setStackInSlot(0, ItemStack.EMPTY);
		sendUpdates();
	}

	private void suckItems() {
		EntityItem ent;
		List<EntityItem> ents = world.getEntitiesWithinAABB(EntityItem.class, getAABB(pos));
		
		
		
		if (ents.size() > 0) {
			ItemStack stack;
			if(inputSlot.getStackInSlot(0).isEmpty()) {
				for (int e = ents.size() - 1; e >= 0; e--) {
					ent = (EntityItem) ents.get(e);
					stack = ent.getEntityItem();
					if (stack.getItem() == itemGravel || stack.getItem() == itemSand || (OresBase.sluiceAllowDirt && stack.getItem() == itemDirt)) {
						if (stack.getCount() > 1) {
							inputSlot.setStackInSlot(0, stack.splitStack(1));
						} else {
							inputSlot.setStackInSlot(0, stack.copy());
							ent.setDead();
						}
						sendUpdates();
						break;
					}
				}
			}
		}
		boolean flowItemsTowards = true;
		if(flowItemsTowards && waterAmount >= 2) {
			IBlockState state = world.getBlockState(pos);
			EnumFacing dir = state.getValue(BlockSluice.FACING).getOpposite();
			IBlockState source = this.world.getBlockState(pos.offset(dir));
			EnumFacing dirdir = dir;
			EnumFacing dirpos = dirdir.rotateY();
			BlockPos pos2 = pos;
			if (source.getBlock() == Blocks.WATER || source.getBlock() == Blocks.FLOWING_WATER) {
				if(dirdir.getAxisDirection() == AxisDirection.NEGATIVE) {
					dirdir = dirdir.getOpposite();
					pos2 = pos2.offset(dirdir,-1);
				}
				dirpos = dirdir.rotateY();
				if(dirpos.getAxisDirection() == AxisDirection.NEGATIVE) {
					dirpos = dirpos.getOpposite();
				}
				ents = world.getEntitiesWithinAABB(EntityItem.class, getAABB(pos2,pos2.up().offset(dirdir,2).offset(dirpos)));
			}
			else {
				ents = world.getEntitiesWithinAABB(EntityItem.class, getAABB(pos));
			}
			//OresBase.logger.log(Level.INFO, " > " + dir);
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
		if(world.isRemote) return;
		float rx = 0.4F + rand.nextFloat()*0.2f;
		float ry = rand.nextFloat() * 0.25F + 0.25F;
		float rz = 0.4F + rand.nextFloat()*0.2f;

		EntityItem entityItem = new EntityItem(world,pos.getX() + rx, pos.getY() + ry, pos.getZ() + rz,stack);

		entityItem.motionX = 0;
		entityItem.motionY = 0;
		entityItem.motionZ = 0;
		entityItem.setPickupDelay(10);
		world.spawnEntity(entityItem);
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
		IBlockState state = this.world.getBlockState(pos);
		EnumFacing dir = state.getValue(BlockSluice.FACING).getOpposite();

		IBlockState source = this.world.getBlockState(pos.offset(dir));

		if (source.getBlock() == Blocks.WATER || source.getBlock() == Blocks.FLOWING_WATER) {
			waterAmount = 8 - source.getValue(BlockLiquid.LEVEL);
		} else if (source.getBlock() == OresBase.sluice) {
			waterAmount = ((TileEntityBasicSluice) world
					.getTileEntity(pos.offset(dir))).getWaterAmount() - 2;
		}
		dir = dir.rotateY();
		Material left = world.getBlockState(pos.offset(dir)).getMaterial();
		Material right = world.getBlockState(pos.offset(dir.getOpposite())).getMaterial();

		if (left == Material.AIR || left == Material.GROUND || left == Material.GRASS || left == Material.SAND || right == Material.AIR || right == Material.GROUND || right == Material.GRASS || right == Material.SAND) {
			waterAmount = 0;
		}
		// model vertex calculation won't handle this well.
		//else if(left == Material.CLOTH || left == Material.CLOTH) {
		//	waterAmount--;
		//}
		if (prevWater != waterAmount) {
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
		dir = state.getValue(BlockSluice.FACING);
		if(waterAmount > 0 && !world.isRemote) {
			Material mat = world.getBlockState(pos.offset(dir).down()).getMaterial();
			if(mat == Material.AIR) {
				IBlockState st = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockDynamicLiquid.LEVEL, 0);
				world.setBlockState(pos.offset(dir).down(), st, 3);
			}
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		//IBlockState bs = world.getBlockState(pos);
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(!inputSlot.getStackInSlot(0).isEmpty() && inputSlot.getStackInSlot(0).getCount() >= (downstremrequests>0?1:0)+1) return null;
			return (T) inputSlot;
		}
		return super.getCapability(capability, facing);
	}

	public int getWaterAmount() {
		return waterAmount > 0 ? waterAmount : 0;
	}

	public int getTime() {
		return timer;
	}
	
	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("harderores:inputSlot", inputSlot.serializeNBT());
		compound.setInteger("harderores:timer", timer);
		//compound.setInteger("harderores:downstremrequests", downstremrequests);
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
		//downstremrequests = compound.getInteger("harderores:downstremrequests");
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
}
