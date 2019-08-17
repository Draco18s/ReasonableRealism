package com.draco18s.industry.entity;

import javax.annotation.Nullable;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.inventory.FilterContainer;
import com.draco18s.industry.inventory.FilterStackHandler;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.BlastFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.SmokerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class FilterTileEntity extends AbstractHopper {
	private BlockState[] filterStates = new BlockState[6];
	private ItemStackHandler filters = new FilterStackHandler(6, this);
	private ItemStackHandler filteredInventory;
	private final LazyOptional<IItemHandler> everything = LazyOptional.of(() -> new CombinedInvWrapper(filteredInventory, filters));
	private final LazyOptional<IItemHandler> filteredInventoryHolder = LazyOptional.of(() -> filteredInventory);
	private EnumAcceptType acceptRule = EnumAcceptType.OR;
	
	public FilterTileEntity() {
		super(ExpandedIndustry.ModTileEntities.machine_filter);
		filteredInventory = new FilteredStacksHandler(inventory,this);
	}
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(facing == null) {
				if(this.getWorld().getBlockState(this.getPos()).getBlock() == ExpandedIndustry.ModBlocks.machine_filter) {
					return everything.cast();
				}
			}
			return filteredInventoryHolder.cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
		return new FilterContainer(windowID, playerInventory, everything.orElse(null), this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("expindustry:filter.name");
	}

	public boolean allowableFilter(ItemStack stack, int slot) {
		if (stack.getItem() instanceof BlockItem) {
			World w = getFilterWorld();
			if(w == null) return false;
			boolean ret = createAndCheckTE(w, pos, stack, slot);
			world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
			//world.scheduleBlockUpdate(pos,this.getBlockState().getBlock(),0,0);
			markDirty();
			return ret;
		}
		return false;
	}

	private boolean createAndCheckTE(World w, BlockPos pos, ItemStack stack, int slot) {
		if (pos.getY() + slot + 10 < 255) {
			pos = pos.up(slot+10);
		}
		if(world == null || stack.isEmpty()) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			return false;
		}
		BlockItem ib = (BlockItem)stack.getItem();
		BlockState state = ib.getBlock().getStateForPlacement(ib.getBlock().getDefaultState(), Direction.NORTH, Blocks.AIR.getDefaultState(), w, pos, BlockPos.ZERO, Hand.MAIN_HAND);
		Direction side = getFacingForSlot(slot);
		if (state.hasTileEntity()) {
			w.setBlockState(pos, state, 0);
			TileEntity te = w.getTileEntity(pos);
			IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
			if (cap != null) {
				filterStates[slot] = state;
				return true;
			}
			else {
				StateContainer<Block, BlockState> bsc = ib.getBlock().getStateContainer();
				ImmutableList<BlockState> values = bsc.getValidStates();
				for (BlockState s : values) {
					world.setBlockState(pos, s, 2);
					te = world.getTileEntity(pos);
					cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
					if (cap != null) {
						filterStates[slot] = state;
						return true;
					}
				}
			}
		}
		else {
			StateContainer<Block, BlockState> bsc = ib.getBlock().getStateContainer();
			ImmutableList<BlockState> values = bsc.getValidStates();
			for (BlockState s : values) {
				if(s.hasTileEntity()) {
					world.setBlockState(pos, s, 2);
					TileEntity te = world.getTileEntity(pos);
					IItemHandler cap = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
					if (cap != null) {
						filterStates[slot] = state;
						return true;
					}
				}
			}
		}
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
		return false;
	}

	private Direction getFacingForSlot(int slot) {
		Direction side = Direction.DOWN;
		switch (slot) {
			case 0:
			case 1:
				side = Direction.UP;
				break;
			case 2:
			case 3:
				side = Direction.EAST;
				break;
			case 4:
			case 5:
				side = Direction.DOWN;
				break;
		}
		return side;
	}

	private World getFilterWorld() {
		return ExpandedIndustry.PROXY.getFilterWorld(this.getWorld());
	}

	public boolean canInsert(ItemStack stack) {
		if (doIHaveFilters()) {
			switch (acceptRule) {
				case OR:
					return acceptOr(stack);
				case AND:
					return acceptAnd(stack);
				case NONE:
					return !acceptOr(stack);
				case SOME:
					return acceptOr(stack) && !acceptAnd(stack);
			}
		}
		return false;
	}

	private boolean acceptAnd(ItemStack stack) {
		return false;
	}

	private boolean acceptOr(ItemStack stack) {
		boolean ret = false;
		for (int i = filters.getSlots() - 1; i >= 0; i--) {
			if (!filters.getStackInSlot(i).isEmpty()) {
				World w = getFilterWorld();
				if(!createAndCheckTE(w, pos, filters.getStackInSlot(i), i)) continue;
				TileEntity te = w.getTileEntity(pos.up(10+i));
				Direction side = getFacingForSlot(i);
				IItemHandler inven = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side).orElse(null);
				for (int fakeSlot = 0; inven != null && fakeSlot < inven.getSlots(); fakeSlot++) {
					ItemStack input = stack.copy();
					input.setCount(1);
					//int numSlots = inven.getSlots();
					ret |= doesAccept(te, inven, fakeSlot, input, i);
				}
			}
		}
		return ret;
	}

	private boolean doesAccept(TileEntity te, IItemHandler inven, int slot, ItemStack stack, int filterSlot) {
		if (te instanceof AbstractFurnaceTileEntity) {
			// have to check filterSlot because the Furnace slots aren't
			// restricted
			if (/* slot == 1 && */(filterSlot == 2 || filterSlot == 3)) {
				if(stack.isEmpty()) return false;
				int t = FurnaceTileEntity.getBurnTimes().get(stack.getItem());
				return t > 0;
			}
			else if (/* slot == 0 && */(filterSlot == 0 || filterSlot == 1)) {
				AbstractFurnaceTileEntity furn = (AbstractFurnaceTileEntity)te;
				furn.setInventorySlotContents(0, stack.copy());
				IRecipe<?> recipeIn = null;
				if(furn instanceof BlastFurnaceTileEntity) {
					recipeIn = this.world.getRecipeManager().getRecipe(IRecipeType.BLASTING, furn, this.world).orElse(null);
				}
				else if(furn instanceof FurnaceTileEntity) {
					recipeIn = this.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, furn, this.world).orElse(null);
				}
				else if(furn instanceof SmokerTileEntity) {
					recipeIn = this.world.getRecipeManager().getRecipe(IRecipeType.SMOKING, furn, this.world).orElse(null);
				}
				else return false;
	            return (recipeIn != null && !recipeIn.getRecipeOutput().isEmpty());
			}
			return false;
		}
		else {
			ItemStack remain = inven.insertItem(slot, stack, true);
			return (remain.isEmpty());
		}
	}

	public boolean doIHaveFilters() {
		for (int i = 0; i < filters.getSlots(); i++) {
			if (!filters.getStackInSlot(i).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public static enum EnumAcceptType {
		OR, AND, NONE, SOME;
	}

	public void dirtyFilters(int changedSlot) {
		World w = getFilterWorld();
		if(w == null) return;
		//for (int i = 0; i < filters.getSlots(); i++) {
			createAndCheckTE(w, pos, filters.getStackInSlot(changedSlot), changedSlot);
		//}
	}

	public EnumAcceptType getAcceptType() {
		return this.acceptRule;
	}

	public void setEnumType(EnumAcceptType acceptType) {
		acceptRule  =acceptType;
	}
	
	@Override
	public void remove() {
		super.remove();
		everything.invalidate();
		filteredInventoryHolder.invalidate();
	}
	
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		tag.put("expindustry:filters", filters.serializeNBT());
		tag.putInt("expindustry:acceptRule", this.acceptRule.ordinal());
		return tag;
	}
	
	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		filters.deserializeNBT(tag.getCompound("expindustry:filters"));
		acceptRule = EnumAcceptType.values()[tag.getInt("expindustry:acceptRule")];
	}
}
