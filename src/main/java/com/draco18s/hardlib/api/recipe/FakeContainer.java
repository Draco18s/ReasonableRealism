package com.draco18s.hardlib.api.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface FakeContainer extends Container {
	@Override
	default void clearContent() {

	}

	@Override
	default int getContainerSize() {
		return 1;
	}

	@Override
	default boolean isEmpty() {
		return false;
	}

	@Override
	default ItemStack getItem(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	default ItemStack removeItem(int slot, int amt) {
		return ItemStack.EMPTY;
	}

	@Override
	default ItemStack removeItemNoUpdate(int slot) {
		return ItemStack.EMPTY;
	}

	@Override
	default void setItem(int slot, ItemStack stack) {

	}

	@Override
	default void setChanged() {

	}

	@Override
	default boolean stillValid(Player p_18946_) {
		return false;
	}
}
