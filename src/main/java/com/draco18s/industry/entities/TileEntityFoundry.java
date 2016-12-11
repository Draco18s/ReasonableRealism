package com.draco18s.industry.entities;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;
import com.draco18s.hardlib.util.RecipesUtils;
import com.draco18s.industry.entities.capabilities.CastingItemStackHandler;
import com.draco18s.industry.entities.capabilities.MoldTemplateItemStackHandler;
import com.draco18s.ores.entities.capabilities.SiftableItemsHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class TileEntityFoundry extends TileEntity implements ITickable {
	protected ItemStackHandler templateSlot;
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	private float castingTime;
	private int lastCheckNumSticks;
	private int lastCheckNumIngots;
	private IRecipe lastCheckRecipe;
	private ItemStack lastCheckSticks;
	private ItemStack lastCheckIngots;
	private Random rand;
	
	public TileEntityFoundry() {
		inputSlot = new CastingItemStackHandler(2);
		outputSlot = new ItemStackHandler();
		templateSlot = new MoldTemplateItemStackHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		rand = new Random();
	}
	
	@Override
	public void update() {
		boolean craft = canCast();
		boolean canCast = craft && hasNearbyFurnace();
		boolean isRemote = worldObj.isRemote;
		if(castingTime > 0) {
			--castingTime;
			if(!canCast) {
				castingTime = 0;
				lastCheckRecipe = null;
			}
			else if (castingTime <= 0) {
				castItem();
			}
		}
		else if(canCast) {
			castingTime = 1599;
		}
		worldObj.markBlockRangeForRenderUpdate(pos, pos);
		worldObj.notifyBlockUpdate(pos, getState(), getState(), 3);
		worldObj.scheduleBlockUpdate(pos,this.getBlockType(),0,0);
		markDirty();
	}

	private IBlockState getState() {
		return worldObj.getBlockState(pos);
	}

	private boolean canCast() {
		//if any stack is null, return false: we can't craft
		if(inputSlot.getStackInSlot(1) == null || templateSlot.getStackInSlot(0) == null)
			return false;

		//get the recipe
		IRecipe recipe = getRecipeForTemplate(templateSlot.getStackInSlot(0));
		if(recipe == null) return false;
		ItemStack output = recipe.getRecipeOutput();
		boolean canFitOutput = outputSlot.insertItem(0, output, true) == null;
		//if they're the same as stored, check quantity
		//also check that the output slot is empty
		//if(lastCheckRecipe == recipe && compareItemStacks(lastCheckSticks,inputSlot.getStackInSlot(0)) && compareItemStacks(lastCheckIngots,inputSlot.getStackInSlot(1))) {
		//	return inputSlot.getStackInSlot(0).stackSize >= lastCheckNumSticks && inputSlot.getStackInSlot(1).stackSize >= lastCheckNumIngots && canFitOutput;
		//}
		int numSticks = 0;
		int numIngots = 0;
		boolean cannotCraft = false;
		
		//count sticks and ingots
		if(recipe instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes)recipe;
			for(ItemStack s : r.recipeItems) {
				int[] IDs = OreDictionary.getOreIDs(s);
				boolean foundMatch = false;
				for(int id : IDs) {
					String name = OreDictionary.getOreName(id);
					if(name.contains("stick")) {
						numSticks++;
						foundMatch = true;
						break;
					}
					else if(name.contains("ingot")) {
						numIngots++;
						foundMatch = true;
						break;
					}
				}
				if(!foundMatch) {
					cannotCraft = true;
				}
			}
		}
		else if(recipe instanceof ShapedOreRecipe) {
			Object[] inputs = ((ShapedOreRecipe)recipe).getInput();
			for(Object o : inputs) {
				if(o instanceof ItemStack) {
					int[] IDs = OreDictionary.getOreIDs((ItemStack)o);
					boolean foundMatch = false;
					for(int id : IDs) {
						String name = OreDictionary.getOreName(id);
						if(name.contains("stick")) {
							numSticks++;
							foundMatch = true;
							break;
						}
						else if(name.contains("ingot")) {
							numIngots++;
							foundMatch = true;
							break;
						}
					}
					if(!foundMatch) {
						cannotCraft = true;
					}
				}
				else if(o instanceof List) {
					List<ItemStack> list = (List<ItemStack>)o;
					boolean foundMatchAll = false;
					for(ItemStack it : list) {
						int[] IDs = OreDictionary.getOreIDs(it);
						boolean foundMatch = false;
						for(int id : IDs) {
							String name = OreDictionary.getOreName(id);
							if(name.contains("stick")) {
								numSticks++;
								foundMatch = true;
								break;
							}
							else if(name.contains("ingot")) {
								numIngots++;
								foundMatch = true;
								break;
							}
						}
						if(foundMatch) {
							foundMatchAll = true;
							break;
						}
					}
					if(!foundMatchAll) {
						cannotCraft = true;
					}
				}
			}
		}
		if(cannotCraft) return false;
		if(inputSlot.getStackInSlot(0) == null)
			lastCheckSticks = null;
		else
			lastCheckSticks = inputSlot.getStackInSlot(0).copy();
		lastCheckIngots = inputSlot.getStackInSlot(1).copy();
		lastCheckNumSticks = numSticks;
		lastCheckNumIngots = numIngots;
		lastCheckRecipe = recipe;
		return ((inputSlot.getStackInSlot(0) == null && numSticks == 0) || inputSlot.getStackInSlot(0).stackSize >= numSticks) && inputSlot.getStackInSlot(1).stackSize >= numIngots && canFitOutput;
	}
	
	private IRecipe getRecipeForTemplate(ItemStack stackInSlot) {
		NBTTagCompound nbt = stackInSlot.getTagCompound();
		if(nbt == null) return null;
		NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
		ItemStack result = ItemStack.loadItemStackFromNBT(itemTags);
		
		return RecipesUtils.getRecipeWithOutput(result);
	}

	private void castItem() {
		boolean qwert = false;
		IRecipe matching = RecipesUtils.getSimilarRecipeWithGivenInput(getRecipeForTemplate(templateSlot.getStackInSlot(0)), inputSlot.getStackInSlot(1).copy());
		
		//subtract ingredients
		inputSlot.extractItem(0, this.lastCheckNumSticks, false);
		inputSlot.extractItem(1, this.lastCheckNumIngots, false);
		//damage mold
		ItemStack template = templateSlot.getStackInSlot(0); 
		//template.damageItem(1, null);
		if(template.attemptDamageItem(1, rand)) {
			templateSlot.extractItem(0, 1, false);
		}
		//produce output
		NBTTagCompound nbt = template.getTagCompound();
		NBTTagCompound itemTags = nbt.getCompoundTag("expindustry:item_mold");
		ItemStack result = matching.getRecipeOutput(); //ItemStack.loadItemStackFromNBT(itemTags);
		
		outputSlot.insertItem(0, result.copy(), false);
	}
	
	private boolean hasNearbyFurnace() {
		for(EnumFacing dir : EnumFacing.values()) {
			IBlockState state = worldObj.getBlockState(pos.offset(dir));
			if(state.getBlock() == Blocks.LIT_FURNACE || state.getBlock() == Blocks.FLOWING_LAVA) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return this.getCapability(capability, facing) != null;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			this.markDirty();
			if(worldObj != null && worldObj.getBlockState(pos).getBlock() != getBlockType()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return (T) new CombinedInvWrapper(inputSlot, templateSlot, outputSlotWrapper);
			}
			if(facing == null) {
				return (T) new CombinedInvWrapper(inputSlot, templateSlot, outputSlotWrapper);
			}
			else if(facing == EnumFacing.UP) {
				return (T) inputSlot;
			}
			else if(facing == EnumFacing.DOWN) {
				return (T) outputSlotWrapper;
			}
			else {
				//any side
				return (T) templateSlot;
			}
		}
		return super.getCapability(capability, facing);
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
		compound.setTag("expindustry:inputSlot", inputSlot.serializeNBT());
		compound.setTag("expindustry:outputSlot", outputSlot.serializeNBT());
		compound.setTag("expindustry:templateSlot", templateSlot.serializeNBT());
		compound.setFloat("expindustry:castingTime", castingTime);
		return compound;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if(inputSlot == null) {
			inputSlot = new CastingItemStackHandler(2);
			outputSlot = new ItemStackHandler();
			templateSlot = new MoldTemplateItemStackHandler();
			outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		}
		if(compound.hasKey("expindustry:inputSlot")) {
			inputSlot.deserializeNBT((NBTTagCompound) compound.getTag("expindustry:inputSlot"));
			outputSlot.deserializeNBT((NBTTagCompound) compound.getTag("expindustry:outputSlot"));
			templateSlot.deserializeNBT((NBTTagCompound) compound.getTag("expindustry:templateSlot"));
		}
		castingTime = compound.getFloat("expindustry:castingTime");
	}

	public float getTime() {
		return castingTime;
	}
	
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}
	
	private static boolean compareItemStacks(ItemStack stack1, ItemStack stack2) {
		return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
	}
	
	/*private ItemStack combineStacks(ItemStack existing, ItemStack stack)
	{
		if (stack == null || stack.stackSize == 0)
			return null;

		int limit = stack.getMaxStackSize();

		if (existing != null)
		{
			if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
				return stack;

			limit -= existing.stackSize;
		}

		if (limit <= 0)
			return stack;

		boolean reachedLimit = stack.stackSize > limit;

		if (existing == null)
		{
			existing = reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack;
		}
		else
		{
			existing.stackSize += reachedLimit ? limit : stack.stackSize;
		}

		return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.stackSize - limit) : null;
	}*/
}
