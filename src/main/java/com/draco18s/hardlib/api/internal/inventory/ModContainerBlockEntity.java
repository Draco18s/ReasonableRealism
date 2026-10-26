package com.draco18s.hardlib.api.internal.inventory;

import java.util.List;

import javax.annotation.Nullable;

import com.draco18s.hardlib.api.interfaces.ICustomContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.EmptyResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public abstract class ModContainerBlockEntity extends BaseContainerBlockEntity implements ICustomContainer {
	public static final ResourceHandler<ItemResource> EMPTY_HANDLER = EmptyResourceHandler.instance();
	
	protected ModContainerBlockEntity(BlockEntityType<?> p_155076_, BlockPos p_155077_, BlockState p_155078_) {
		super(p_155076_, p_155077_, p_155078_);
	}

	/** The handlers holding the inventory; the Container's slots are theirs, in this order. */
	protected abstract List<ItemStacksResourceHandler> getInventories();
	
	@Override
	public void openGUI(ServerPlayer player) {
		if (!level.isClientSide()) {
			player.openMenu(this, getBlockPos());
		}
	}
	
	/** The item handler capability on a side (null for all of it), or null for none; see HardLib's capability registration. */
	@Nullable
	public abstract ResourceHandler<ItemResource> getItemHandler(@Nullable Direction side);

	public double getLevelX() {
		return (double)worldPosition.getX() + 0.5D;
	}

	public double getLevelY() {
		return (double)worldPosition.getY() + 0.5D;
	}

	public double getLevelZ() {
		return (double)worldPosition.getZ() + 0.5D;
	}

	/** A container slot's handler and its index there. */
	private record HandlerIndex(ItemStacksResourceHandler handler, int index) { }

	private HandlerIndex locate(int slot) {
		for(ItemStacksResourceHandler handler : getInventories()) {
			if(slot < handler.size()) {
				return new HandlerIndex(handler, slot);
			}
			slot -= handler.size();
		}
		throw new IndexOutOfBoundsException(slot);
	}

	/** The inventory lives in the item handlers; these satisfy BaseContainerBlockEntity with a copy of it. */
	@Override
	protected NonNullList<ItemStack> getItems() {
		NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		for(int i = 0; i < items.size(); i++) {
			items.set(i, getItem(i));
		}
		return items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		for(int i = 0; i < getContainerSize() && i < items.size(); i++) {
			setItem(i, items.get(i), true);
		}
	}

	@Override
	public int getContainerSize() {
		return getInventories().stream().mapToInt(ItemStacksResourceHandler::size).sum();
	}

	@Override
	public boolean isEmpty() {
		return getInventories().stream().allMatch(ResourceHandlerUtil::isEmpty);
	}

	@Override
	public ItemStack getItem(int slot) {
		HandlerIndex at = locate(slot);
		return ItemUtil.getStack(at.handler(), at.index());
	}

	@Override
	public ItemStack removeItem(int slot, int amt) {
		HandlerIndex at = locate(slot);
		ItemResource resource = at.handler().getResource(at.index());
		if(resource.isEmpty() || amt <= 0) return ItemStack.EMPTY;
		try (Transaction tx = Transaction.openRoot()) {
			int extracted = at.handler().extract(at.index(), resource, amt, tx);
			tx.commit();
			if(extracted > 0) setChanged();
			return resource.toStack(extracted);
		}
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack stack = getItem(slot);
		setItem(slot, ItemStack.EMPTY, true);
		return stack;
	}

	@Override
	public void setItem(int slot, ItemStack stack, boolean insideTransaction) {
		HandlerIndex at = locate(slot);
		at.handler().set(at.index(), ItemResource.of(stack), stack.getCount());
		if(!insideTransaction) {
			setChanged();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public void clearContent() {
		for(ItemStacksResourceHandler handler : getInventories()) {
			for(int i = 0; i < handler.size(); i++) {
				handler.set(i, ItemResource.EMPTY, 0);
			}
		}
	}

	public static boolean isInventoryEmpty(ResourceHandler<ItemResource> inven) {
		return ResourceHandlerUtil.isEmpty(inven);
	}

	public static boolean isInventoryFull(ResourceHandler<ItemResource> inven) {
		return ResourceHandlerUtil.isFull(inven);
	}
	
	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		modSave(output);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		modLoad(input);
	}

	/** The client gets what is saved; NeoForge's default onDataPacket and handleUpdateTag load it. */
	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket(){
	    return ClientboundBlockEntityDataPacket.create(this);
	}
	
	protected abstract void modSave(ValueOutput output);

	protected abstract void modLoad(ValueInput input);
}
