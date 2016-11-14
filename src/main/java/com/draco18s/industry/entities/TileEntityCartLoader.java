package com.draco18s.industry.entities;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TileEntityCartLoader extends TileEntityHopper {
	private HashMap<EntityMinecartContainer, Vec3d> carts = new HashMap<EntityMinecartContainer, Vec3d>();
	private HashMap<UUID, Vec3d> nbtCarts = new HashMap<UUID, Vec3d>();

	public TileEntityCartLoader() {
		super();
		setCustomName("container.expindustry:cart_loader");
	}
	
	@Override
	public void update() {
		if(worldObj.getBlockState(pos).getValue(BlockHopper.FACING) != EnumFacing.DOWN) {
			worldObj.setBlockState(this.getPos(), this.getBlockType().getDefaultState().withProperty(BlockHopper.FACING, EnumFacing.DOWN), 3);
		}
		if(nbtCarts.size() > 0) {
			List<EntityMinecartContainer> list = worldObj.getEntitiesWithinAABB(EntityMinecartContainer.class, new AxisAlignedBB(pos.north().west().down(2), pos.south(2).east(2).up(2)), EntitySelectors.HAS_INVENTORY);
			if (list != null && list.size() > 0) {
				for(Entity o : list) {
					if(o instanceof EntityMinecartContainer) {
						EntityMinecartContainer cart = (EntityMinecartContainer)o;
						UUID uuid = cart.getPersistentID();
						if(nbtCarts.containsKey(uuid)) {
							//System.out.println("Found our cart!");
							Vec3d v = nbtCarts.get(uuid);
							carts.put(cart, v);
							nbtCarts.remove(uuid);
						}
					}
				}
			}
		}
		super.update();
		if(!worldObj.isRemote) {
			List<EntityMinecartContainer> list = worldObj.getEntitiesWithinAABB(EntityMinecartContainer.class, new AxisAlignedBB(pos.down(), pos.south().east().up(2)), EntitySelectors.HAS_INVENTORY);
			//System.out.println("list:"+list.size());
			if (list != null && list.size() > 0) {
				for(EntityMinecartContainer obj : list) {
					if(obj instanceof EntityMinecartContainer) {
						EntityMinecartContainer cart = (EntityMinecartContainer)obj;
						boolean hasItems = false;
						if(cart.posY > getYPos()) {
							//System.out.println("Cart above...");
							//if cart has items...
							int firstNonEmpty = -1;
							for(int j = 0; j < cart.getSizeInventory() && !hasItems; j++) {
								if(cart.getStackInSlot(j) != null) {
									hasItems = true;
									firstNonEmpty = j;
								}
							}
							if(hasItems) {
								hasItems = false;
								//if I have room...
								for(int j = getSizeInventory() - 1; j >= 0 && !hasItems; j--) {
									if(getStackInSlot(j) == null || (isItemValidForSlot(j, cart.getStackInSlot(firstNonEmpty)) && getStackInSlot(j).stackSize < getInventoryStackLimit())) {
										hasItems = true;
									}
								}
							}
						}
						else {
							//System.out.println("Cart below...");
							//if I have items...
							int firstNonEmpty = -1;
							for(int j = 0; j < getSizeInventory() && !hasItems; j++) {
								if(getStackInSlot(j) != null) {
									hasItems = true;
									firstNonEmpty = j;
								}
							}
							//if cart has room...
							if(hasItems) {
								hasItems = false;
								for(int j = cart.getSizeInventory() - 1; j >= 0 && !hasItems; j--) {
									if(cart.getStackInSlot(j) == null || (cart.isItemValidForSlot(j, getStackInSlot(firstNonEmpty)) && cart.getStackInSlot(j).stackSize < cart.getInventoryStackLimit())) {
										hasItems = true;
									}
								}
							}
						}
						double dx = (getXPos()+0.5) - cart.posX;
						double dz = (getZPos()+0.5) - cart.posZ;
						if(carts.containsKey(cart)) {
							Vec3d v = carts.get(cart);
							if(!hasItems) {
								cart.motionX = ((getXPos()+0.5)-v.xCoord)*0.2;
								cart.motionZ = ((getZPos()+0.5)-v.zCoord)*0.2;
								if(cart.motionX != 0) {
									cart.motionX = 0.5;
								}
								if(cart.motionZ != 0) {
									cart.motionZ = 0.5;
								}
								cart.moveMinecartOnRail(pos);
								carts.remove(cart);
							}
							else if((dx*dx+dz*dz) < 0.7) {
								carts.put(cart, new Vec3d(cart.posX, cart.posY, cart.posZ));
								cart.motionX = 0;
								cart.motionZ = 0;
								cart.moveMinecartOnRail(pos);
								cart.setPosition(cart.posX, cart.posY, cart.posZ);
							}
						}
						else if(hasItems && (dx*dx+dz*dz) < 0.7) {
							carts.put(cart, new Vec3d(cart.posX, cart.posY, cart.posZ));
							cart.motionX = 0;
							cart.motionZ = 0;
							cart.moveMinecartOnRail(pos);
							cart.setPosition(cart.posX, cart.posY, cart.posZ);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		int i = 0;
		for(EntityMinecartContainer cart : carts.keySet()) {
			compound.setLong("cart_mb"+i, cart.getPersistentID().getMostSignificantBits());
			compound.setLong("cart_lb"+i, cart.getPersistentID().getLeastSignificantBits());
			Vec3d v = carts.get(cart);
			compound.setDouble("cart_x"+i, v.xCoord);
			compound.setDouble("cart_y"+i, v.yCoord);
			compound.setDouble("cart_z"+i, v.zCoord);
			i++;
		}
		compound.setInteger("totalCarts", i);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("totalCarts")) {
			for(int i = compound.getInteger("totalCarts") - 1; i >=0; i--) {
				UUID idToCheck = new UUID(compound.getLong("cart_mb"+i),compound.getLong("cart_lb"+i));
				Vec3d v = new Vec3d(compound.getDouble("cart_x"+i), compound.getDouble("cart_y"+i), compound.getDouble("cart_z"+i));
				nbtCarts.put(idToCheck, v);
			}
		}
	}
}
