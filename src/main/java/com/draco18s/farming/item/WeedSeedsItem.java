package com.draco18s.farming.item;

import com.draco18s.farming.block.WeedyFarmlandBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Sows weeds into farmland: clicked directly, or through the crop growing on it. */
public class WeedSeedsItem extends Item {
	public WeedSeedsItem(Item.Properties props) {
		super(props);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if(!world.getBlockState(pos).is(Blocks.FARMLAND)) {
			pos = pos.below();
			if(!world.getBlockState(pos).is(Blocks.FARMLAND)) return InteractionResult.PASS;
		}
		if(!world.isClientSide()) {
			WeedyFarmlandBlock.plantWeeds(world, pos);
			world.playSound(null, pos, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1, 1);
			context.getItemInHand().consume(1, context.getPlayer());
		}
		return InteractionResult.SUCCESS;
	}
}
