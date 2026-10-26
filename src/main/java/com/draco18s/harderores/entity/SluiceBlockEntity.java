package com.draco18s.harderores.entity;

import java.util.List;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.SluiceBlock;
import com.draco18s.hardlib.api.HardLibAPI;
import com.draco18s.hardlib.api.internal.BlockWrapper;
import com.draco18s.hardlib.api.internal.inventory.ModBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class SluiceBlockEntity extends ModBlockEntity {
	private static final int cycleLength = 125;
	/** How many 8-block slices below the sluice count as "nearby" ore (ore flowers' default bonemeal depth). */
	private static final int SCAN_SLICES = 4;
	/** Weight of a gravel (flint) result, for gravel only: about a quarter of all rolls, as in 1.12. */
	private static final int GRAVEL_WEIGHT = 4;

	/** An ore the sluice can wash out: its weight, the blocks it counts as (ore data is per block), and its drop. */
	private record SluiceOre(int weight, List<Block> blocks, Item drop) { }

	/** 1.12's sluice list (iron and gold 3, diamond 1, redstone 2), plus copper. Built on use: the holders fill in late. */
	private static List<SluiceOre> ores() {
		return List.of(
				new SluiceOre(3, List.of(HarderOres.ModBlocks.ore_hardiron, HarderOres.ModBlocks.ore_harddeepslate_iron), Items.RAW_IRON),
				new SluiceOre(3, List.of(HarderOres.ModBlocks.ore_hardgold, HarderOres.ModBlocks.ore_harddeepslate_gold), Items.RAW_GOLD),
				new SluiceOre(3, List.of(HarderOres.ModBlocks.ore_hardcopper, HarderOres.ModBlocks.ore_harddeepslate_copper), Items.RAW_COPPER),
				new SluiceOre(1, List.of(HarderOres.ModBlocks.ore_harddiamond, HarderOres.ModBlocks.ore_harddeepslate_diamond), HarderOres.ModItems.orechunk_diamond),
				new SluiceOre(2, List.of(Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE), Items.REDSTONE));
	}
	
	protected ItemStacksResourceHandler inputSlot = new ItemStacksResourceHandler(1);
	protected ItemStacksResourceHandler inputHandler = inputSlot;
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
		if(inputSlot.getResource(0).isEmpty()) return;
		try (Transaction tx = Transaction.openRoot()) {
			inputSlot.extract(0, inputSlot.getResource(0), 1, tx);
			tx.commit();
		}
	}

	/**
	 * Rolls an ore (or flint, for gravel) and washes some out if the chunks around the sluice hold that ore: usually
	 * its milled dust, sometimes the raw drop or a double, depending on what's being sluiced (1.12's rules).
	 */
	private void doFilter() {
		if(level.isClientSide()) return;
		ItemStack input = ItemUtil.getStack(inputSlot, 0);
		boolean gravel = input.is(Items.GRAVEL);
		boolean sand = input.is(ItemTags.SAND);
		List<SluiceOre> ores = ores();
		int roll = rand.nextInt(ores.stream().mapToInt(SluiceOre::weight).sum() + (gravel ? GRAVEL_WEIGHT : 0));
		SluiceOre ore = null;
		for(SluiceOre o : ores) {
			if(roll < o.weight()) {
				ore = o;
				break;
			}
			roll -= o.weight();
		}
		if(ore == null) {
			mergeStacks(new ItemStack(Items.FLINT));
			return;
		}
		if(HardLibAPI.oreData == null || HardLibAPI.oreFlowers == null) return;

		// The richest of the 3x3 chunks around the sluice, counting ore down to SCAN_SLICES below it.
		int best = 0;
		BlockPos bestPos = null;
		BlockWrapper bestOre = null;
		for(int j = -1; j <= 1; j++) {
			for(int k = -1; k <= 1; k++) {
				BlockPos look = worldPosition.offset(16 * j, 0, 16 * k);
				for(Block block : ore.blocks()) {
					BlockWrapper wrapper = HardLibAPI.oreFlowers.getOreWrapper(block);
					if(wrapper == null) continue;
					int count = 0;
					for(int slice = 0; slice < SCAN_SLICES; slice++) {
						count += HardLibAPI.oreData.getOreData(level, look.below(slice * 8), wrapper);
					}
					if(count > best) {
						best = count;
						bestPos = look;
						bestOre = wrapper;
					}
				}
			}
		}
		if(best <= 0) return;
		// Washing it out uses up a little of that ore (1.12 added 1 here by mistake). Taken from the slice at the
		// sluice's level, or the ones below it if that slice has none.
		HardLibAPI.oreData.adjustOreData(level, bestPos, bestOre, -1);

		ItemStack basicDrop = new ItemStack(ore.drop());
		ItemStack milled = HardLibAPI.oreMachines.getMillResult(basicDrop);
		ItemStack toSpawn = milled.isEmpty() ? basicDrop : milled.copyWithCount(1);
		if(!milled.isEmpty()) {
			if(gravel) {
				if(rand.nextInt(10) == 0) toSpawn = basicDrop;
			}
			else if(sand) {
				if(rand.nextInt(10) == 0) toSpawn.setCount(2);
			}
			else {
				int r = rand.nextInt(20);
				if(r == 0) toSpawn = basicDrop;
				else if(r == 1) toSpawn.setCount(2);
			}
		}
		mergeStacks(toSpawn);
	}

	private void mergeStacks(ItemStack stack) {
		if(stack.isEmpty()) return;
		
		if(level.isClientSide()) return;
		float rx = 0.4F + rand.nextFloat()*0.2f;
		float ry = rand.nextFloat() * 0.25F + 0.25F;
		float rz = 0.4F + rand.nextFloat()*0.2f;

		// Doesn't float off in the sluice's water.
		ItemEntity entityItem = new MotionlessItemEntity(level,worldPosition.getX() + rx, worldPosition.getY() + ry, worldPosition.getZ() + rz, stack);

		entityItem.setDeltaMovement(Vec3.ZERO);
		entityItem.setPickUpDelay(10);
		level.addFreshEntity(entityItem);
	}

	private void suckItems() {
		if(!inputSlot.getResource(0).isEmpty()) return;
		List<ItemEntity> ents = level.getEntitiesOfClass(ItemEntity.class, getAABB(worldPosition));
		for(ItemEntity ent : ents) {
			ItemStack stack = ent.getItem();
			if(!stack.is(HarderOres.Tags.Items.GRANULAR)) {
				continue;
			}
			ItemStack stack2 = stack.split(1);
			inputSlot.set(0, ItemResource.of(stack2), stack2.getCount());
			break;
		}
		if(!inputSlot.getResource(0).isEmpty()) {
			timer = 25 * cycleLength;
		}
	}

	private AABB getAABB(BlockPos p) {
		double height = this.getBlockState().getShape(level, p).bounds().maxY;
		return new AABB(p.getX(), p.getY(), p.getZ(), p.getX()+height+0.1, p.getY()+1, p.getZ()+1);
	}

	@Override
	protected void modSave(ValueOutput output) {
		inputSlot.serialize(output.child("harderores:inputslot"));
	}

	@Override
	protected void modLoad(ValueInput input) {
		inputSlot.deserialize(input.childOrEmpty("harderores:inputslot"));
	}
}
