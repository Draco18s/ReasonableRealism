package com.draco18s.harderores.inventory;

import javax.annotation.Nonnull;

import com.draco18s.harderores.recipe.MachineRecipe;

import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/**
 * A machine menu with a recipe book (the sifter, the packager): the player's inventory, then two input slots and an
 * output. (The player's slots come first, like CommonContainerMenu's, so the recipe book's placement is done here
 * rather than by vanilla's, which expects the recipe's slots to be first.)
 */
public abstract class MachineRecipeBookMenu<R extends MachineRecipe> extends RecipeBookMenu<SingleRecipeInput, R> {
	/** The menu's slots: the player's 36, then these. */
	public static final int FIRST_INPUT = 36, INPUTS = 2, OUTPUT = FIRST_INPUT + INPUTS;

	protected final int invenSize;
	protected final int windowHeight;
	protected final Container container;
	private final RecipeType<R> recipeType;

	public MachineRecipeBookMenu(MenuType<?> type, int containerId, int height, Inventory playerInventory, Container container, RecipeType<R> recipeType) {
		super(type, containerId);
		this.container = container;
		this.recipeType = recipeType;
		invenSize = container.getContainerSize();
		windowHeight = height;
		container.startOpen(playerInventory.player);
		bindPlayerInventory(playerInventory);
	}

	public MachineRecipeBookMenu(MenuType<?> type, int containerId, int size, int height, RecipeType<R> recipeType) {
		super(type, containerId);
		this.recipeType = recipeType;
		invenSize = size;
		windowHeight = height;
		container = new SimpleContainer(size);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return container.stillValid(playerIn);
	}

	protected void bindPlayerInventory(Inventory playerInventory) {
		for(int l = 0; l < 3; ++l) {
			for(int k = 0; k < 9; ++k) {
				this.addSlot(new Slot(playerInventory, k + l * 9 + 9, 8 + k * 18, l * 18 + windowHeight - 82));
			}
		}

		for(int i1 = 0; i1 < 9; ++i1) {
			this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, windowHeight - 24));
		}
	}

	@Override
	@Nonnull
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			if (index >= this.slots.size() - this.container.getContainerSize()) {
				if (!this.moveItemStackTo(itemstack1, 0, this.slots.size()-this.container.getContainerSize(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, this.slots.size()-this.container.getContainerSize(), this.slots.size(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.getCount() == 0) {
				slot.setByPlayer(ItemStack.EMPTY);
			}
			else {
				slot.setChanged();
			}
		}

		return itemstack;
	}

	/**
	 * A recipe clicked in the book: what's in the inputs goes back to the player, then the ingredient comes out of
	 * their inventory, one job's worth (a stack's worth with shift). With none of it, the book shows a ghost instead.
	 */
	@Override
	public void handlePlacement(boolean placeAll, RecipeHolder<?> holder, ServerPlayer player) {
		if(!(holder.value() instanceof MachineRecipe recipe) || recipe.getType() != recipeType || !player.getRecipeBook().contains(holder)) return;
		Inventory inventory = player.getInventory();
		for(int i = FIRST_INPUT; i < OUTPUT; i++) {
			ItemStack stack = getSlot(i).getItem();
			if(!stack.isEmpty()) {
				getSlot(i).set(ItemStack.EMPTY);
				inventory.placeItemBackInInventory(stack);
			}
		}
		Ingredient ingredient = recipe.getIngredients().get(0);
		int wanted = placeAll ? Integer.MAX_VALUE : recipe.getIngredientQuantity();
		for(int i = 0; i < FIRST_INPUT && wanted > 0; i++) {
			ItemStack stack = getSlot(i).getItem();
			if(stack.isEmpty() || !ingredient.test(stack)) continue;
			for(int in = FIRST_INPUT; in < OUTPUT && wanted > 0 && !stack.isEmpty(); in++) {
				Slot input = getSlot(in);
				ItemStack there = input.getItem();
				if(!input.mayPlace(stack) || !there.isEmpty() && !ItemStack.isSameItemSameComponents(there, stack)) continue;
				int moved = Math.min(Math.min(wanted, stack.getCount()), input.getMaxStackSize(stack) - there.getCount());
				if(moved <= 0) continue;
				input.set(stack.copyWithCount(there.getCount() + moved));
				stack.shrink(moved);
				wanted -= moved;
			}
			getSlot(i).setChanged();
		}
		if(getSlot(FIRST_INPUT).getItem().isEmpty() && getSlot(FIRST_INPUT + 1).getItem().isEmpty()) {
			player.connection.send(new ClientboundPlaceGhostRecipePacket(containerId, holder));
		}
		broadcastChanges();
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents contents) {
		for(int i = FIRST_INPUT; i < OUTPUT; i++) {
			contents.accountSimpleStack(getSlot(i).getItem());
		}
	}

	@Override
	public void clearCraftingContent() {
		for(int i = FIRST_INPUT; i < OUTPUT; i++) {
			getSlot(i).set(ItemStack.EMPTY);
		}
	}

	@Override
	public boolean recipeMatches(RecipeHolder<R> recipe) {
		for(int i = FIRST_INPUT; i < OUTPUT; i++) {
			if(recipe.value().getIngredients().get(0).test(getSlot(i).getItem())) return true;
		}
		return false;
	}

	@Override
	public int getResultSlotIndex() {
		return OUTPUT;
	}

	@Override
	public int getGridWidth() {
		return 1;
	}

	@Override
	public int getGridHeight() {
		return 1;
	}

	@Override
	public int getSize() {
		return INPUTS + 1;
	}

	@Override
	public boolean shouldMoveToInventory(int slot) {
		return slot != OUTPUT;
	}
}
