package com.draco18s.harderores.entity;

import com.draco18s.harderores.HarderOres;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class SluiceTileEntity extends TileEntity implements ITickable {
	private static final ModelProperty<Integer> WATER = new ModelProperty<Integer>();
	private int waterAmount;
	protected ItemStackHandler inputSlot;
	protected LazyOptional<ItemStackHandler> inputHandler = LazyOptional.of(() -> inputSlot);

	public SluiceTileEntity() {
		super(HarderOres.ModTileEntities.sluice);
		inputSlot = new ItemStackHandler(1);
	}

	@Override
	public void tick() {
		BlockPos p = pos;
		Direction dir = world.getBlockState(p).get(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
		do {
			p=p.offset(dir,1);
		} while(world.getBlockState(p).getBlock() == HarderOres.ModBlocks.sluice);
		p = p.offset(dir.getOpposite(), 1);
		updateWater();
	}

	private void updateWater() {
		int prevWater = waterAmount;
		Direction dir = this.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING).getOpposite();

		BlockState source = this.world.getBlockState(pos.offset(dir));

		if (source.getFluidState().getFluid() == Fluids.WATER || source.getFluidState().getFluid() == Fluids.FLOWING_WATER) {
			waterAmount = /*8 - */source.getFluidState().getLevel();
		} else if (source.getBlock() == HarderOres.ModBlocks.sluice) {
			waterAmount = ((SluiceTileEntity)world.getTileEntity(pos.offset(dir))).getWaterAmount() - 2;
		}
		dir = dir.rotateY();
		Material left = world.getBlockState(pos.offset(dir)).getMaterial();
		Material right = world.getBlockState(pos.offset(dir.getOpposite())).getMaterial();

		if (left == Material.AIR || left == Material.EARTH || left == Material.ORGANIC || left == Material.SAND || right == Material.AIR || right == Material.EARTH || right == Material.ORGANIC || right == Material.SAND) {
			waterAmount = 0;
		}
		if (prevWater != waterAmount) {
			ModelDataManager.requestModelDataRefresh(this);
			//world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	protected int getWaterAmount() {
		return this.waterAmount;
	}

	@Override
    public IModelData getModelData() {
        return new ModelDataMap.Builder().withInitial(WATER, this.waterAmount).build();
    }
}
