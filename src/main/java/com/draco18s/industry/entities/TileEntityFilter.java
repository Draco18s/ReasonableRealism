package com.draco18s.industry.entities;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustryBase;
import com.draco18s.industry.world.FilterDimension;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityFilter extends TileEntityHopper {
	private NonNullList<ItemStack> filters = NonNullList.create();
	private IBlockState[] filterStates = new IBlockState[6];
	private EnumAcceptType acceptRule = EnumAcceptType.OR;

	// can I get away with using the origin for ALL instances of the filter?
	// would need to save the exact blockstate being placed though...
	// that would just be the metadata, which could be displayed in the item...

	// use the TE's local position as the location in the void dim to set all of
	// its filters
	// it would overwrite itself if it has more than one filter rule
	// but the odds of a collision between Filters reduces dramatically

	public TileEntityFilter() {
		super();
		setCustomName("container.expindustry:filter");
		for(int i=0; i < 6; i++)
			filters.add(ItemStack.EMPTY);
	}

	@Override
	public void update() {
		super.update();
	}

	/*@Override
	public boolean updateHopper() {
		if (world != null && !world.isRemote) {
			if (!isOnTransferCooldown() && BlockHopper.isEnabled(getBlockMetadata())) {
				boolean flag = false;

				if (!isEmpty()) {
					flag = transferItemsOut();
				}

				if (!isFull()) {
					flag = captureDroppedItems(this) || flag;
				}

				if (flag) {
					setTransferCooldown(8);
					markDirty();
					return true;
				}
			}

			return false;
		}
		else {
			return false;
		}
	}*/

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < 5) {
			return super.getStackInSlot(slot);
		}
		else {
			return filters.get(slot - 5);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack == null) {
			return true;
		}
		if (slot < 5) {
			if (doIHaveFilters()) {
				switch (acceptRule) {
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
		else if (stack.getItem() instanceof ItemBlock) {
			World w = getFilterWorld();
			if(w == null) return false;
			boolean ret = createAndCheckTE(w, pos, stack, slot - 5);
			world.notifyBlockUpdate(pos, getState(), getState(), 3);
			world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
			markDirty();
			return ret;
		}
		return false;
	}

	private World getFilterWorld() {
		return ExpandedIndustryBase.proxy.getFilterWorld();
	}

	public boolean doIHaveFilters() {
		for (ItemStack ii : filters) {
			if (!ii.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private boolean acceptOr(int slot, ItemStack stack) {
		boolean ret = false;
		for (int i = filters.size() - 1; i >= 0; i--) {
			if (filters.get(i) != ItemStack.EMPTY) {
				World w = getFilterWorld();
				createAndCheckTE(w, pos, filters.get(i), i);
				TileEntity te = w.getTileEntity(pos.up(i));
				EnumFacing side = getFacingForSlot(i);
				IItemHandler inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
				for (int fakeSlot = 0; inven != null && fakeSlot < inven.getSlots(); fakeSlot++) {
					ItemStack input = stack.copy();
					input.setCount(1);
					int numSlots = inven.getSlots();
					ret |= doesAccept(te, inven, fakeSlot, input, i);
				}
			}
		}
		return ret;
	}

	private boolean acceptAnd(int slot, ItemStack stack) {
		boolean ret = true;
		for (int i = filters.size() - 1; i >= 0; i--) {
			if (filters.get(i) != ItemStack.EMPTY) {
				boolean anySlot = false;
				World w = getFilterWorld();
				createAndCheckTE(w, pos, filters.get(i), slot);
				TileEntity te = w.getTileEntity(pos.up(i));
				EnumFacing side = getFacingForSlot(i);
				IItemHandler inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
				for (int fakeSlot = 0; inven != null && fakeSlot < inven.getSlots(); fakeSlot++) {
					ItemStack input = stack.copy();
					input.setCount(1);
					anySlot = anySlot || doesAccept(te, inven, fakeSlot, input, i);
				}
				ret &= anySlot;
			}
		}
		return ret;
	}

	private boolean doesAccept(TileEntity te, IItemHandler inven, int slot, ItemStack stack, int filterSlot) {
		if (te instanceof TileEntityFurnace) {
			// have to check filterSlot because the Furnace slots aren't
			// restricted
			if (/* slot == 1 && */(filterSlot == 2 || filterSlot == 3)) {
				int t = TileEntityFurnace.getItemBurnTime(stack);
				return t > 0;
			}
			else if (/* slot == 0 && */(filterSlot == 0 || filterSlot == 1)) {
				ItemStack s = FurnaceRecipes.instance().getSmeltingResult(stack);
				return !s.areItemStacksEqual(s, ItemStack.EMPTY);
			}
			return false;
		}
		else {
			ItemStack remain = inven.insertItem(slot, stack, true);
			return (remain.areItemStacksEqual(remain, ItemStack.EMPTY));
		}
	}

	@Override
	public void setInventorySlotContents(int slot, @Nullable ItemStack stack) {
		if (slot < 5) {
			super.setInventorySlotContents(slot, stack);
		}
		else if (stack == null) {
			filters.set(slot-5, ItemStack.EMPTY);
			filterStates[slot - 5] = null;
			if(!this.world.isRemote) {
				World w = getFilterWorld();
				if(w == null) return;
				w.setBlockToAir(pos);
			}
		}
		else if (stack.getItem() instanceof ItemBlock) {
			filters.set(slot-5, stack);
		}
		if (world != null) {
			world.notifyBlockUpdate(pos, getState(), getState(), 3);
		}
		world.notifyBlockUpdate(pos, getState(), getState(), 3);
		world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}

	@Override
	public ItemStack decrStackSize(int slot, int num) {
		if (slot < 5) {
			return super.decrStackSize(slot, num);
		}
		if (filters.get(slot-5) != ItemStack.EMPTY) {
			//ItemStack itemstack;
			if (filters.get(slot-5).getCount() <= num) {
				//itemstack = filters[slot - 5];
				filters.set(slot-5, ItemStack.EMPTY);
				filterStates[slot - 5] = null;
				if(!this.world.isRemote) {
					World w = getFilterWorld();
					if(w == null) return null;
					w.setBlockToAir(pos);
				}
				//return itemstack;
			}
			else {
				//itemstack = filters[slot - 5].splitStack(num);
				if (filters.get(slot-5).getCount() == 0) {
					filters.set(slot-5, ItemStack.EMPTY);
					filterStates[slot - 5] = null;
					if(!this.world.isRemote) {
						World w = getFilterWorld();
						if(w == null) return null;
						w.setBlockToAir(pos);
					}
				}
				//return itemstack;
			}
			world.notifyBlockUpdate(pos, getState(), getState(), 3);
			world.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
			markDirty();
			return null;
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
		return world.getBlockState(pos);
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
		return new SPacketUpdateTileEntity(pos, 3, getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		NBTTagList nbttaglist = tag.getTagList("expindustry:filters", 10);
		filters = NonNullList.create();
		for(int i=0; i < 6; i++)
			filters.add(ItemStack.EMPTY);
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("expindustry:fslot") & 255;
			if (j >= 0 && j < filters.size()) {
				filters.set(j,new ItemStack(nbttagcompound1));
			}
		}
		acceptRule = EnumAcceptType.values()[tag.getInteger("expindustry:accepttype")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < filters.size(); ++i) {
			if (filters.get(i) != ItemStack.EMPTY) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("expindustry:fslot", (byte) i);
				filters.get(i).writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		tag.setTag("expindustry:filters", nbttaglist);
		tag.setInteger("expindustry:accepttype", acceptRule.ordinal());
		return tag;
	}

	@Override
	public void validate() {
		if(!this.world.isRemote && getFilterWorld() != null)
			ExpandedIndustryBase.forceChunkLoad(getFilterWorld(), new ChunkPos(pos));
		super.validate();
	}

	@Override
	public void invalidate() {
		if(!this.world.isRemote && getFilterWorld() != null)
			ExpandedIndustryBase.releaseChunkLoad(getFilterWorld(), new ChunkPos(pos));
		super.invalidate();
	}

	private boolean createAndCheckTE(World world, BlockPos pos, ItemStack stack, int slot) {
		if(world == null) return false;
		if (pos.getY() + slot < 255) {
			pos = pos.up(slot);
		}
		ItemBlock ib = (ItemBlock) stack.getItem();
		IBlockState state = ib.getBlock().getStateFromMeta(ib.getMetadata(stack));
		// World w =
		// FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(FilterDimension.DIMENSION_ID);
		if (world.getBlockState(pos) == filterStates[slot]) {
			return true;
		}
		if (filterStates[slot] != null) {
			world.setBlockState(pos, filterStates[slot], 2);
			TileEntity te = world.getTileEntity(pos);
			if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
				filterStates[slot] = state;
				return true;
			}
		}
		if (ib.getBlock().hasTileEntity(state)) {
			world.setBlockState(pos, state, 2);
			IBlockState somestate = world.getBlockState(pos);
			TileEntity te = world.getTileEntity(pos);
			EnumFacing side = getFacingForSlot(slot);
			if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
				filterStates[slot] = state;
				return true;
			}
			else if (ib.getMetadata(stack) == 0) {
				BlockStateContainer bsc = ib.getBlock().getBlockState();
				ImmutableList<IBlockState> values = bsc.getValidStates();
				for (IBlockState s : values) {
					world.setBlockState(pos, s, 2);
					//System.out.println(world.getBlockState(pos));
					te = world.getTileEntity(pos);
					if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
						filterStates[slot] = s;
						return true;
					}
				}
			}
		}
		else {
			BlockStateContainer bsc = ib.getBlock().getBlockState();
			ImmutableList<IBlockState> values = bsc.getValidStates();
			EnumFacing side = getFacingForSlot(slot - 5);
			for (IBlockState s : values) {
				world.setBlockState(pos, s, 2);
				// System.out.println("Checking..." + world.getBlockState(pos));
				TileEntity te = world.getTileEntity(pos);
				if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
					filterStates[slot] = s;
					return true;
				}
			}
		}
		filterStates[slot] = null;
		world.setBlockToAir(pos);
		return false;
	}

	private EnumFacing getFacingForSlot(int slot) {
		EnumFacing side = EnumFacing.DOWN;
		switch (slot) {
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
		return side;
	}

	public static enum EnumAcceptType {
		OR, AND, NONE, SOME;
	}

	/*====================================== Overrides for Filtering ======================================*/
	/*============================================ Hopper Stuff ===========================================*/

	private boolean isFull() {
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack itemstack = getStackInSlot(i);
			if (itemstack == null || itemstack.getCount() != itemstack.getMaxStackSize()) {
				return false;
			}
		}
		return true;
	}

	private boolean transferItemsOut() {
		if (net.minecraftforge.items.VanillaInventoryCodeHooks.insertHook(this)) {
			return true;
		}
		IInventory iinventory = getInventoryForHopperTransfer();

		if (iinventory == null) {
			return false;
		}
		else {
			EnumFacing enumfacing = BlockHopper.getFacing(getBlockMetadata()).getOpposite();

			if (isInventoryFull(iinventory, enumfacing)) {
				return false;
			}
			else {
				for (int i = 0; i < getSizeInventory(); ++i) {
					if (getStackInSlot(i) != null) {
						ItemStack itemstack = getStackInSlot(i).copy();
						ItemStack itemstack1 = putStackInInventoryAllSlots(this, iinventory, decrStackSize(i, 1), enumfacing);

						if (itemstack1 == null || itemstack1.getCount() == 0) {
							iinventory.markDirty();
							return true;
						}

						setInventorySlotContents(i, itemstack);
					}
				}

				return false;
			}
		}
	}

	public static boolean captureDroppedItems(TileEntityFilter hopper) {
		Boolean ret = extractHook(hopper);
		if (ret != null) {
			return ret;
		}
		IInventory iinventory = getSourceInventory(hopper);

		if (iinventory != null) {
			EnumFacing enumfacing = EnumFacing.DOWN;

			if (isInventoryEmpty(iinventory, enumfacing)) {
				return false;
			}

			if (iinventory instanceof ISidedInventory) {
				ISidedInventory isidedinventory = (ISidedInventory) iinventory;
				int[] aint = isidedinventory.getSlotsForFace(enumfacing);

				for (int i : aint) {
					if (pullItemFromSlot(hopper, iinventory, i, enumfacing)) {
						return true;
					}
				}
			}
			else {
				int j = iinventory.getSizeInventory();

				for (int k = 0; k < j; ++k) {
					if (pullItemFromSlot(hopper, iinventory, k, enumfacing)) {
						return true;
					}
				}
			}
		}
		else {
			for (EntityItem entityitem : getCaptureItems(hopper.getWorld(), hopper.getXPos(), hopper.getYPos(),
					hopper.getZPos())) {
				if (putDropInInventoryAllSlots((IInventory)null, hopper, entityitem)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isInventoryFull(IInventory inventoryIn, EnumFacing side) {
		if (inventoryIn instanceof ISidedInventory) {
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for (int k : aint) {
				ItemStack itemstack1 = isidedinventory.getStackInSlot(k);

				if (itemstack1 == null || itemstack1.getCount() != itemstack1.getMaxStackSize()) {
					return false;
				}
			}
		}
		else {
			int i = inventoryIn.getSizeInventory();

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = inventoryIn.getStackInSlot(j);

				if (itemstack == null || itemstack.getCount() != itemstack.getMaxStackSize()) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Returns false if the specified IInventory contains any items
	 */
	private static boolean isInventoryEmpty(IInventory inventoryIn, EnumFacing side) {
		if (inventoryIn instanceof ISidedInventory) {
			ISidedInventory isidedinventory = (ISidedInventory) inventoryIn;
			int[] aint = isidedinventory.getSlotsForFace(side);

			for (int i : aint) {
				if (isidedinventory.getStackInSlot(i) != null) {
					return false;
				}
			}
		}
		else {
			int j = inventoryIn.getSizeInventory();

			for (int k = 0; k < j; ++k) {
				if (inventoryIn.getStackInSlot(k) != null) {
					return false;
				}
			}
		}

		return true;
	}

	private IInventory getInventoryForHopperTransfer() {
		EnumFacing enumfacing = BlockHopper.getFacing(getBlockMetadata());
		/**
		 * Returns the IInventory (if applicable) of the TileEntity at the
		 * specified position
		 */
		return getInventoryAtPosition(getWorld(), getXPos() + enumfacing.getFrontOffsetX(),
				getYPos() + enumfacing.getFrontOffsetY(), getZPos() + enumfacing.getFrontOffsetZ());
	}

	private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, EnumFacing direction) {
		ItemStack itemstack = inventoryIn.getStackInSlot(index);

		if (itemstack != null && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1),
					(EnumFacing) null);

			if (itemstack2 == null || itemstack2.getCount() == 0) {
				inventoryIn.markDirty();
				return true;
			}

			inventoryIn.setInventorySlotContents(index, itemstack1);
		}

		return false;
	}

	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, EnumFacing side) {
		return !(inventoryIn instanceof ISidedInventory)
				|| ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
	}

	/*======================================= Vanilla Hooks Override ======================================*/
	
	private static Boolean extractHook(IHopper dest) {
		//IHopper l;
		TileEntity tileEntity = dest.getWorld()
				.getTileEntity(new BlockPos(dest.getXPos(), dest.getYPos() + 1, dest.getZPos()));

		if (tileEntity == null
				|| !tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
			return null;
		}

		IItemHandler handler = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

		for (int i = 0; i < handler.getSlots(); i++) {
			ItemStack extractItem = handler.extractItem(i, 1, true);
			if (extractItem != null) {
				for (int j = 0; j < dest.getSizeInventory(); j++) {
					ItemStack destStack = dest.getStackInSlot(j);
					if (destStack == null || destStack.getCount() < destStack.getMaxStackSize()
							&& destStack.getCount() < dest.getInventoryStackLimit()
							&& ItemHandlerHelper.canItemStacksStack(extractItem, destStack)) {
						// override! we need to check that the insert is valid!
						if (dest.isItemValidForSlot(j, extractItem)) {
							extractItem = handler.extractItem(i, 1, false);
							if (destStack == null) {
								dest.setInventorySlotContents(j, extractItem);
							}
							else {
								destStack.grow(1);
								dest.setInventorySlotContents(j, destStack);
							}
							dest.markDirty();
							return true;
						}
					}
				}
			}
		}

		return false;
	}
}
