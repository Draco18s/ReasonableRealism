package com.draco18s.harderores.entity;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.entity.capability.PackableItemsHandler;
import com.draco18s.harderores.entity.capability.PackagerMechanicalPowerHandler;
import com.draco18s.harderores.inventory.PackagerContainer;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.ores.AxelOrientation;
import com.draco18s.hardlib.api.capability.CapabilityMechanicalPower;
import com.draco18s.hardlib.api.capability.RawMechanicalPowerHandler;
import com.draco18s.hardlib.api.interfaces.ICustomContainer;
import com.draco18s.hardlib.api.interfaces.capability.IMechanicalPower;
import com.draco18s.hardlib.api.internal.inventory.OutputItemStackHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public class PackagerTileEntity extends TileEntity implements ITickableTileEntity, ICustomContainer {
	protected ItemStackHandler inputSlot;
	protected ItemStackHandler outputSlot;
	private ItemStackHandler outputSlotWrapper;
	private LazyOptional<ItemStackHandler> inputSlotholder = LazyOptional.of(() -> inputSlot);
	private LazyOptional<ItemStackHandler> outputSlotWrapperholder = LazyOptional.of(() -> inputSlot);
	private float packTime;
	private float timeMod;
	protected RawMechanicalPowerHandler powerUser;
	private LazyOptional<RawMechanicalPowerHandler> powerUserholder = LazyOptional.of(() -> powerUser);
	private int activeSlot = -1;
	
	public PackagerTileEntity() {
		super(HarderOres.ModTileEntities.packager);
		inputSlot = new PackableItemsHandler();
		outputSlot = new ItemStackHandler();
		powerUser = new PackagerMechanicalPowerHandler();
		outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		packTime = 0;
		timeMod = 1;
	}
	
	@Override
	public void tick() {
		if(world.getBlockState(pos).getBlock() != this.getBlockState().getBlock()) return;
		
		if(packTime > 0) {
			float pow = calcAndGetPower()/timeMod;
			packTime -= pow;
			boolean canAnyPack = false;
			int resetTime = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canPackage(s)) {
					canAnyPack = true;
					resetTime = s;
				}
			}
			if(resetTime != activeSlot) {
				activeSlot = resetTime;
				packTime = 100;
			}
			if(!canAnyPack) {
				packTime = 0;
			}
			else if (packTime <= 0) {
				packItem();
			}
		}
		else {
			activeSlot = -1;
			for(int s = 0; s < inputSlot.getSlots(); s++) {
				if(canPackage(s)) {
					activeSlot = s;
					packTime = 100;
					ItemStack nextResult = HardLibAPI.oreMachines.getPressurePackResult(inputSlot.getStackInSlot(s),true);
					float inMod = 1;
					float outMod = 1;
					if(inputSlot.getStackInSlot(s).getItem() instanceof BlockItem) {
						try {
							Block block = Block.getBlockFromItem(inputSlot.getStackInSlot(s).getItem());
							
							BlockState state;
							state = block.getDefaultState().getStateForPlacement(Direction.DOWN, Blocks.AIR.getDefaultState(), world, BlockPos.ZERO, BlockPos.ZERO, Hand.MAIN_HAND);
							inMod = state.getBlockHardness(null, BlockPos.ZERO) * 2;
							if(block.getHarvestTool(state).equals("pickaxe") && block.getHarvestLevel(state) >= 0) {
								inMod *= (block.getHarvestLevel(state) + 2);
							}
						}
						catch(NullPointerException e) {
							inMod = 1;
						}
					}
					if(!nextResult.isEmpty() && nextResult.getItem() instanceof BlockItem) {
						try {
							Block block = Block.getBlockFromItem(nextResult.getItem());
							
							BlockState state;
							state = block.getDefaultState().getStateForPlacement(Direction.DOWN, Blocks.AIR.getDefaultState(), world, BlockPos.ZERO, BlockPos.ZERO, Hand.MAIN_HAND);
							outMod = state.getBlockHardness(null, BlockPos.ZERO);
							if(block.getHarvestTool(state).equals("shovel") && block.getHarvestLevel(state) >= 0) {
								outMod *= 2;
							}
						}
						catch(NullPointerException e) {
							outMod = 1;
						}
					}
					timeMod = Math.max(inMod, outMod);
				}
			}
		}
	}

	private float calcAndGetPower() {
		if(world.isRemote) return powerUser.getScaledPower(powerUser.getRawPower());
		int numBlocksOut = 0;
		BlockPos p = pos;
		Direction searchDir = Direction.UP;
		do {
			p = p.offset(searchDir,1);
			if(world.getBlockState(p).getBlock() == HarderOres.ModBlocks.axel) {
				if(world.getBlockState(p).get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.UP) {
					searchDir = Direction.UP;
				}
				else {
					searchDir = world.getBlockState(p).get(BlockStateProperties.FACING);
				}
				numBlocksOut++;
			}
			else {
				p = p.offset(searchDir.getOpposite());
				numBlocksOut = 999;
			}
		} while(numBlocksOut <= 8);
		BlockState s = world.getBlockState(p);
		if(s.getBlock() == HarderOres.ModBlocks.axel && s.get(BlockProperties.AXEL_ORIENTATION) == AxelOrientation.HUB) {
			TileEntity te = world.getTileEntity(p);
			IMechanicalPower pow = te.getCapability(CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY, searchDir).orElse(null);
			if(pow != null) {
				powerUser.setRawPower(pow.getRawPower());
				return powerUser.getScaledPower(powerUser.getRawPower());
			}
		}
		this.markDirty();
		return 0;
	}

	private boolean canPackage(int slot) {
		if(inputSlot.getStackInSlot(slot).isEmpty()) return false;
		ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(inputSlot.getStackInSlot(slot), true);
		if(result.isEmpty()) return false;
		if(!outputSlot.insertItem(0, result, true).isEmpty()) return false;
		return true;
	}

	private void packItem() {
		for(int s = 0; s < inputSlot.getSlots(); s++) {
			ItemStack stack = inputSlot.getStackInSlot(s);
			if(stack.isEmpty()) continue;
			ItemStack result = HardLibAPI.oreMachines.getPressurePackResult(stack, true);
			if(result.isEmpty()) continue;
			if(!outputSlot.insertItem(0, result, true).isEmpty()) continue;
			inputSlot.extractItem(s, HardLibAPI.oreMachines.getPressurePackAmount(stack), false);
			outputSlot.insertItem(0, result.copy(), false);
		}
		this.markDirty();
	}
	
	//@Override
	//public boolean hasCapability(Capability<?> capability, Direction facing) {
	//	return this.getCapability(capability, facing) != null;
	//}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			this.markDirty();
			if(world != null && world.getBlockState(pos).getBlock() != getBlockState().getBlock()) {//if the block at myself isn't myself, allow full access (Block Broken)
				return LazyOptional.of(() -> new CombinedInvWrapper(inputSlot, outputSlotWrapper)).cast();
			}
			if(facing == null) {
				return LazyOptional.of(() -> new CombinedInvWrapper(inputSlot, outputSlotWrapper)).cast();
			}
			if(facing == Direction.UP) {
				return inputSlotholder.cast();
			}
			if(facing == Direction.DOWN) {
				return outputSlotWrapperholder.cast();
			}
		}
		if(capability == CapabilityMechanicalPower.MECHANICAL_POWER_CAPABILITY) {
			return powerUserholder.cast();
		}
		return super.getCapability(capability, facing);
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
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("harderores:power", powerUser.serializeNBT());
		compound.put("harderores:inputSlot", inputSlot.serializeNBT());
		compound.put("harderores:outputSlot", outputSlot.serializeNBT());
		compound.putFloat("harderores:packTime", packTime);
		compound.putFloat("harderores:timeMod", timeMod);
		return compound;
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		if(inputSlot == null) {
			//inputSlot = new PackableItemsHandler();
			outputSlot = new ItemStackHandler();
			outputSlotWrapper = new OutputItemStackHandler(outputSlot);
		}
		if(compound.hasUniqueId("harderores:inputSlot")) {
			inputSlot.deserializeNBT((CompoundNBT) compound.getCompound("harderores:inputSlot"));
		}
		if(compound.hasUniqueId("harderores:outputSlot")) {
			outputSlot.deserializeNBT((CompoundNBT) compound.getCompound("harderores:outputSlot"));
		}
		if(compound.hasUniqueId("harderores:power")) {
			powerUser.deserializeNBT((CompoundNBT) compound.getCompound("harderores:power"));
		}
		packTime = compound.getFloat("harderores:packTime");
		timeMod = compound.getFloat("harderores:timeMod");
	}

	public float getTime() {
		return packTime;
	}

	@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!world.isRemote) {
			NetworkHooks.openGui(player, this, getPos());
		}
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
		return new PackagerContainer(windowID, playerInventory, new CombinedInvWrapper(inputSlot, outputSlotWrapper), this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("harderores:packager.name");
	}
}