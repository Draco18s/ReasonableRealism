package com.draco18s.farming.entity;

import com.draco18s.farming.HarderFarming;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.attachment.AttachmentType;

/**
 * A cow's milk (a mooshroom's stew), which only builds up while it grazes (1.12's CowStats and EntityAIMilking).
 * An adult near forage gains a point a tick and one without loses a point; a milking costs QUANTA.
 */
public class MilkSupply {
	/** One milking's worth of grazing (five minutes). */
	public static final int QUANTA = 6000;
	/** A cow needs a reserve beyond the milking itself. */
	public static final int MILKABLE = QUANTA + 4000;
	/** At most three milkings banked. */
	public static final int MAX = 3 * QUANTA + 5000;
	/** How often (in ticks) a cow looks for forage. */
	public static final int INTERVAL = 20;

	/** Mutated in place so ticking doesn't sync; only crossing MILKABLE is sent to clients (they gate the milking too). */
	public static class Milk {
		private int level;

		public Milk(int level) {
			this.level = level;
		}

		public int level() {
			return level;
		}

		public boolean isMilkable() {
			return level >= MILKABLE;
		}
	}

	/** Cows already about when this arrives (and new ones) can be milked once. */
	public static final AttachmentType<Milk> MILK = AttachmentType.builder(() -> new Milk(MILKABLE))
			.serialize(Codec.INT.fieldOf("level").xmap(Milk::new, Milk::level))
			.sync(ByteBufCodecs.VAR_INT.map(Milk::new, Milk::level))
			.build();

	public static boolean isMilkable(AbstractCow cow) {
		return cow.getData(MILK).isMilkable();
	}

	/** Every INTERVAL ticks on the server: grazing adds to the supply, going without uses it up. */
	public static void graze(AbstractCow cow) {
		Milk milk = cow.getData(MILK);
		boolean was = milk.isMilkable();
		if(hasForage(cow)) {
			if(cow.getAge() == 0) milk.level += INTERVAL;
		}
		else {
			milk.level -= INTERVAL;
		}
		milk.level = Math.clamp(milk.level, 0, MAX);
		if(was != milk.isMilkable()) cow.syncData(MILK);
	}

	/** On the server, when a milking goes ahead. */
	public static void milk(AbstractCow cow) {
		Milk milk = cow.getData(MILK);
		milk.level = Math.max(milk.level - QUANTA, 0);
		if(!milk.isMilkable()) cow.syncData(MILK);
	}

	/** Forage underfoot, or within 3 blocks across and 2 up or down. */
	public static boolean hasForage(AbstractCow cow) {
		TagKey<Block> forage = cow instanceof MushroomCow ? HarderFarming.Tags.Blocks.MOOSHROOM_FORAGE : HarderFarming.Tags.Blocks.COW_FORAGE;
		Level level = cow.level();
		BlockPos feet = cow.blockPosition();
		if(level.getBlockState(feet.below()).is(forage)) return true;
		for(BlockPos p : BlockPos.betweenClosed(feet.offset(-3, -2, -3), feet.offset(3, 2, 3))) {
			if(level.getBlockState(p).is(forage)) return true;
		}
		return false;
	}
}
