package com.draco18s.industry.entities;

import javax.annotation.Nullable;

import com.draco18s.industry.entities.TileEntityFilter.EnumAcceptType;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TileEntityFilter extends TileEntityHopper {
    private ItemStack[] filters = new ItemStack[6];
    private TileEntity[] fakeEntities = new TileEntity[6];
    private EnumAcceptType acceptRule = EnumAcceptType.OR;

	public TileEntityFilter() {
		super();
		setCustomName("container.expindustry:filter");
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
    public ItemStack getStackInSlot(int slot) {
        if(slot < 5) {
        	return super.getStackInSlot(slot);
        }
        else {
        	return filters[slot-5];
        }
    }
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if(stack == null) { return true; }
		if(slot < 5) {
			if(doIHaveFilters()) {
				switch(acceptRule) {
					case OR:
						return acceptOr(slot, stack);
					case AND:
						return acceptAnd(slot, stack);
					case NONE:
						return !acceptOr(slot, stack);
					case SOME:
						return acceptOr(slot, stack) && !acceptAnd(slot, stack);
				}
			}
		}
		else if(stack.getItem() instanceof ItemBlock) {
			ItemBlock ib = (ItemBlock)stack.getItem();
			IBlockState state = ib.block.getStateFromMeta(ib.getMetadata(stack));
			if(ib.block.hasTileEntity(state)) {
				TileEntity te = ib.block.createTileEntity(null, state);
				return te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			}
		}
		return false;
    }

	public boolean doIHaveFilters() {
		for(TileEntity ii : fakeEntities) {
			if(ii != null) return true;
		}
		return false;
	}
	
	private boolean acceptOr(int slot, ItemStack stack) {
		boolean ret = false;
		for(int i = fakeEntities.length-1;i>=0;i--) {
			if(fakeEntities[i] != null) {
				TileEntity te = fakeEntities[i];
				EnumFacing side = EnumFacing.DOWN;
				switch(i) {
					case 0:
					case 1:
						side = EnumFacing.UP;
						break;
					case 2:
					case 3:
						side = EnumFacing.EAST;
						break;
					case 4:
					case 5:
						side = EnumFacing.DOWN;
						break;
				}
				IItemHandler inven;
				inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
				for(int fakeSlot = 0; inven != null && fakeSlot < inven.getSlots(); fakeSlot++) {
					ItemStack input = stack.copy();
					input.stackSize = 1;
					ret |= doesAccept(te, inven, fakeSlot, input);
				}
				/*if(fakeEntities[i] instanceof ISidedInventory) {
					ISidedInventory isi = ((ISidedInventory)fakeEntities[i]);
					int a = 0;
					switch(i) {
						case 0:
						case 1:
							a = 1;
							break;
						case 2:
						case 3:
							a = 2;
							break;
						case 4:
						case 5:
							a = 0;
							break;
					}
					int[] accessibleSlots = isi.getAccessibleSlotsFromSide(a);
					for(int s : accessibleSlots) {
						ret |= doesAccept(isi, s, a, stack);
						//System.out.println("Slot " + s + ret);
					}
					if(ret) return ret;
				}
				else {
					for(int s = fakeEntities[i].getSizeInventory()-1; s>=0; s--) {
						if(doesAccept(fakeEntities[i], s, stack)) {
							return true;
						}
					}
				}*/
			}
		}
		return ret;
	}

	private boolean acceptAnd(int slot, ItemStack stack) {
		boolean ret = true;
		for(int i = fakeEntities.length-1;i>=0;i--) {
			if(fakeEntities[i] != null) {
				boolean anySlot = false;
				TileEntity te = fakeEntities[i];
				EnumFacing side = EnumFacing.DOWN;
				switch(i) {
					case 0:
					case 1:
						side = EnumFacing.UP;
						break;
					case 2:
					case 3:
						side = EnumFacing.EAST;
						break;
					case 4:
					case 5:
						side = EnumFacing.DOWN;
						break;
				}
				IItemHandler inven;
				inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
				for(int fakeSlot = 0; inven != null && fakeSlot < inven.getSlots(); fakeSlot++) {
					ItemStack input = stack.copy();
					input.stackSize = 1;
					anySlot = anySlot || doesAccept(te, inven, fakeSlot, input);
				}
				
				/*if(fakeEntities[i] instanceof ISidedInventory) {
					ISidedInventory isi = ((ISidedInventory)fakeEntities[i]);
					int a = 0;
					switch(i) {
						case 0:
						case 1:
							a = 1;
							break;
						case 2:
						case 3:
							a = 2;
							break;
						case 4:
						case 5:
							a = 0;
							break;
					}
					int[] accessibleSlots = isi.getAccessibleSlotsFromSide(a);
					for(int s : accessibleSlots) {
						anySlot = anySlot || doesAccept(isi, s, a, stack);
					}
				}
				else {
					for(int s = fakeEntities[i].getSizeInventory()-1; !anySlot && s>=0; s--) {
						if(doesAccept(fakeEntities[i], s, stack)) {
							anySlot = true;
						}
					}
				}*/
				ret &= anySlot;
			}
		}
		return ret;
	}
	
	private boolean doesAccept(TileEntity te, IItemHandler inven, int slot, ItemStack stack) {
		if(te instanceof TileEntityFurnace) {
			if(slot == 1) {
				return TileEntityFurnace.getItemBurnTime(stack) > 0;
			}
			else if(slot == 0) {
				return FurnaceRecipes.instance().getSmeltingResult(stack) != null;
			}
		}
		else {
			ItemStack remain = inven.insertItem(slot, stack, true);
			return (remain == null);
		}
		return false;
	}
	
	public void setInventorySlotContents(int slot, @Nullable ItemStack stack) {
		if(slot < 5) {
			super.setInventorySlotContents(slot, stack);
		}
		else if(stack == null) {
			filters[slot - 5] = null;
			fakeEntities[slot - 5] = null;
		}
		else if(stack.getItem() instanceof ItemBlock) {
			ItemBlock ib = (ItemBlock)stack.getItem();
			IBlockState state = ib.block.getStateFromMeta(ib.getMetadata(stack));
			if(ib.block.hasTileEntity(state)) {
				TileEntity te = ib.block.createTileEntity(null, state);
				if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
					filters[slot - 5] = stack;
					fakeEntities[slot - 5] = te;
				}
				/*if(te instanceof IInventory) {
					filters[slot - 5] = stack;
					//filters[slot - 5].stackSize = 0;
					fakeEntities[slot - 5] = (IInventory)te;
				}*/
			}
		}
        if (worldObj != null) { 
    		worldObj.notifyBlockUpdate(pos, getState(), getState(), 3);
        	//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
		markDirty();
	}
	
	public ItemStack decrStackSize(int slot, int num) {
		if(slot < 5) {
			return super.decrStackSize(slot, num);
		}
		if (filters[slot-5] != null) {
            ItemStack itemstack;
            if (filters[slot-5].stackSize <= num) {
                itemstack = filters[slot-5];
                filters[slot-5] = null;
                fakeEntities[slot-5] = null;
                return itemstack;
            }
            else {
                itemstack = filters[slot-5].splitStack(num);
                if (filters[slot-5].stackSize == 0) {
                    filters[slot-5] = null;
                    fakeEntities[slot-5] = null;
                }
                return itemstack;
            }
        }
        else {
            return null;
        }
    }

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	private IBlockState getState() {
		return worldObj.getBlockState(pos);
	}

	public EnumAcceptType getEnumType() {
		return acceptRule;
	}

	public void setEnumType(EnumAcceptType enumAcceptType) {
		acceptRule = enumAcceptType;
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
	public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList nbttaglist = tag.getTagList("expindustry:filters", 10);
        filters = new ItemStack[6];
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("expindustry:fslot") & 255;
            if (j >= 0 && j < filters.length) {
            	filters[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            	ItemBlock ib = (ItemBlock)filters[j].getItem();
    			IBlockState state = ib.block.getStateFromMeta(ib.getMetadata(filters[j]));
            	if(ib.block.hasTileEntity(state)) {
    				TileEntity te = ib.block.createTileEntity(null, state);
    				if(te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
    					fakeEntities[j - 5] = te;
    				}
    			}
            }
        }
        acceptRule = EnumAcceptType.values()[tag.getInteger("expindustry:accepttype")];
    }

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < filters.length; ++i) {
            if (filters[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("expindustry:fslot", (byte)i);
                filters[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        tag.setTag("expindustry:filters", nbttaglist);
        tag.setInteger("expindustry:accepttype", acceptRule.ordinal());
        return tag;
    }
	
	public static enum EnumAcceptType {
    	OR,
    	AND,
    	NONE,
    	SOME;
    }
	
	public World getWorld() {
		//System.out.println("I am at " + this.getPos());
        return this.worldObj;
    }
}
