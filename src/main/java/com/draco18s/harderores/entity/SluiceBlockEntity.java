package com.draco18s.harderores.entity;

import java.util.List;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class SluiceBlockEntity extends ModBlockEntity {
	private static final int cycleLength = 25;
	private static final Ingredient CHUNKS = Ingredient.of(HarderOres.Tags.Items.ORE_CHUNKS);
	private static final Ingredient DUSTS = Ingredient.of(HarderOres.Tags.Items.TINY_ORE_DUSTS);
	
	protected ItemStackHandler inputSlot = new ItemStackHandler(1);
	protected LazyOptional<ItemStackHandler> inputHandler = LazyOptional.of(() -> inputSlot);
	protected int timer;
	protected RandomSource rand;

	public SluiceBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(HarderOres.ModBlockEntities.sluice, p_155229_, p_155230_);
		rand = RandomSource.create();
	}
	
	public static void tick(Level world, BlockPos pos, BlockState state, SluiceBlockEntity sluice) {
		sluice.suckItems();
		if(sluice.getBlockState().getValue(SluiceBlock.LEVEL) < 1) {
			sluice.timer = 0;
			return;
		}
		
		if(sluice.timer > 0) {
			sluice.timer--;
			if(sluice.timer % (cycleLength*5) == 0) {
				sluice.doFilter();
			}
			if(sluice.timer == 0) {
				sluice.subtractDirt();
			}
		}
	}

	private void subtractDirt() {
		inputSlot.extractItem(0, 1, false);
	}

	private void doFilter() {
		int r = rand.nextInt(100);
		ItemStack toSpawn = ItemStack.EMPTY;
		if(r < 3) {
			int v = rand.nextInt(CHUNKS.getItems().length);
			toSpawn = CHUNKS.getItems()[v].copy();
		}
		else if(r < 10) {
			int v = rand.nextInt(DUSTS.getItems().length);
			toSpawn = CHUNKS.getItems()[v].copy();
		}
		else if(r < 15 && inputSlot.getStackInSlot(0).is(Blocks.GRAVEL.asItem())) {
			toSpawn = new ItemStack(Items.FLINT);
		}
		mergeStacks(toSpawn);
	}

	private void mergeStacks(ItemStack stack) {
		if(stack.isEmpty()) return;
		
		if(level.isClientSide) return;
		float rx = 0.4F + rand.nextFloat()*0.2f;
		float ry = rand.nextFloat() * 0.25F + 0.25F;
		float rz = 0.4F + rand.nextFloat()*0.2f;

		ItemEntity entityItem = new ItemEntity(level,worldPosition.getX() + rx, worldPosition.getY() + ry, worldPosition.getZ() + rz, stack);

		entityItem.setDeltaMovement(Vec3.ZERO);
		entityItem.setPickUpDelay(10);
		level.addFreshEntity(entityItem);
	}

	private void suckItems() {
		if(!inputSlot.getStackInSlot(0).isEmpty()) return;
		List<ItemEntity> ents = level.getEntitiesOfClass(ItemEntity.class, getAABB(worldPosition));
		for(ItemEntity ent : ents) {
			ItemStack stack = ent.getItem();
			if(!stack.is(HarderOres.Tags.Items.GRANULAR)) {
				if(stack.is(HarderOres.Tags.Items.ORE_CHUNKS) || stack.is(HarderOres.Tags.Items.TINY_ORE_DUSTS)) {
					// >:C
				}
				continue;
			}
			ItemStack stack2 = stack.split(1);
			inputSlot.insertItem(0, stack2, false);
			break;
		}
		if(!inputSlot.getStackInSlot(0).isEmpty()) {
			timer = 25 * cycleLength;
		}
	}

	private AABB getAABB(BlockPos p) {
		double height = this.getBlockState().getShape(level, p).bounds().maxY;
		return new AABB(p.getX(), p.getY(), p.getZ(), p.getX()+height+0.1, p.getY()+1, p.getZ()+1);
	}

	@Override
	protected void modSave(CompoundTag tag) {
		tag.put("harderores:inputslot", inputSlot.serializeNBT());
	}

	@Override
	protected void modLoad(CompoundTag tag) {
		inputSlot.deserializeNBT(tag.getCompound("harderores:inputslot"));
	}
}
