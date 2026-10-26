package com.draco18s.farming.block;

import javax.annotation.Nullable;

import com.draco18s.farming.HarderFarming;
import com.draco18s.farming.entity.TannerBlockEntity;
import com.draco18s.hardlib.api.block.state.BlockProperties;
import com.draco18s.hardlib.api.blockproperties.farming.LeatherStatus;
import com.draco18s.hardlib.api.internal.block.ModEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * Tanning rack: hang a rawhide on either side to cure it into leather, and salt the top to speed it up.
 * Each side holds one hide; the rack only faces south or west, so its two sides are its front and back.
 */
public class TannerBlock extends ModEntityBlock {
	public static final VoxelShape TANNER_AABB_SOUTH = box(0, 0, 3.2, 16, 16, 12.8);
	public static final VoxelShape TANNER_AABB_WEST = box(3.2, 0, 0, 12.8, 16, 16);

	public TannerBlock(Properties props) {
		super(props.mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).ignitedByLava().strength(2, 0).sound(SoundType.WOOD).noOcclusion());
		registerDefaultState(this.stateDefinition.any()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
				.setValue(BlockProperties.LEFT_LEATHER_STATE, LeatherStatus.NONE)
				.setValue(BlockProperties.RIGHT_LEATHER_STATE, LeatherStatus.NONE)
				.setValue(BlockProperties.SALT_LEVEL, 0));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
		builder.add(BlockProperties.LEFT_LEATHER_STATE);
		builder.add(BlockProperties.RIGHT_LEATHER_STATE);
		builder.add(BlockProperties.SALT_LEVEL);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction face = context.getHorizontalDirection();
		if(face == Direction.NORTH || face == Direction.EAST) face = face.getOpposite();
		return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, face);
	}

	@Override
	@Deprecated
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if(state.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.SOUTH) {
			return TANNER_AABB_SOUTH;
		}
		return TANNER_AABB_WEST;
	}

	/**
	 * Clicking a side hangs the held hide there (or takes the finished leather down); clicking the top adds the
	 * held salt.
	 */
	@Override
	@Deprecated
	protected InteractionResult useItemOn(ItemStack heldItem, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if(world.isClientSide()) return InteractionResult.SUCCESS;
		BlockEntity be = world.getBlockEntity(pos);
		if(!(be instanceof TannerBlockEntity tanner)) return InteractionResult.TRY_WITH_EMPTY_HAND;
		ResourceHandler<ItemResource> inven = tanner.getItemHandler(hit.getDirection());
		if(inven != null && inven.size() > 0) {
			if(inven.getResource(0).isEmpty() && !heldItem.isEmpty()) {
				try (Transaction tx = Transaction.openRoot()) {
					heldItem.shrink(inven.insert(0, ItemResource.of(heldItem), heldItem.getCount(), tx));
					tx.commit();
				}
			}
			else if((heldItem.isEmpty() || heldItem.getItem() == Items.LEATHER) && hit.getDirection() != Direction.UP) {
				ItemResource resource = inven.getResource(0);
				ItemStack item;
				try (Transaction tx = Transaction.openRoot()) {
					item = resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(inven.extract(0, resource, 1, tx));
					tx.commit();
				}
				if(!player.getInventory().add(item)) {
					player.drop(item, false, Prediction.SERVER_ONLY);
				}
			}
		}
		tanner.setChanged();
		tanner.updateBlockState();
		return InteractionResult.CONSUME;
	}

	// Its contents drop from TannerBlockEntity.preRemoveSideEffects.

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TannerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide() ? null : createTickerHelper(type, HarderFarming.ModBlockEntities.tanner, TannerBlockEntity::tick);
	}
}
