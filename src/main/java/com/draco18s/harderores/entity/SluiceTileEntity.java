package com.draco18s.harderores.entity;

import javax.annotation.Nullable;

import com.draco18s.harderores.HarderOres;
import com.draco18s.harderores.block.SluiceBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelDataMap.Builder;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

public class SluiceTileEntity extends TileEntity implements ITickableTileEntity {
	//public static final ModelProperty<Integer> WATER = new ModelProperty<Integer>();
	private int waterAmount = -1;
	//private int downstreamWaterAmount = -1;
	//private int upstreamWaterAmount = -1;
	protected ItemStackHandler inputSlot;
	protected LazyOptional<ItemStackHandler> inputHandler = LazyOptional.of(() -> inputSlot);
	public static ModelProperty<Float> LEVEL_CORNERS_0 = new ModelProperty<Float>();
	public static ModelProperty<Float> LEVEL_CORNERS_1 = new ModelProperty<Float>();
	public static ModelProperty<Float> LEVEL_CORNERS_2 = new ModelProperty<Float>();
	public static ModelProperty<Float> LEVEL_CORNERS_3 = new ModelProperty<Float>();
	public static ModelProperty<Integer> WATER_COLOR = new ModelProperty<Integer>();
	public static ModelProperty<Float> FLOW_DIRECTION = new ModelProperty<Float>();

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
		//int prevWater = waterAmount;
		//int prevDownStream = downstreamWaterAmount;
		//int prevUpStream = upstreamWaterAmount;
		waterAmount = 0;
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
		dir = this.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);//.getOpposite();
		pos = getPos();
		/*TileEntity dstate = world.getTileEntity(pos.offset(dir));
		if(dstate instanceof SluiceTileEntity) {
			downstreamWaterAmount = ((SluiceTileEntity)dstate).getWaterAmount();
		}
		else {
			downstreamWaterAmount = -1;
		}
		TileEntity ustate = world.getTileEntity(pos.offset(dir.getOpposite()));
		if(ustate instanceof SluiceTileEntity) {
			upstreamWaterAmount = ((SluiceTileEntity)ustate).getWaterAmount();
		}
		else {
			upstreamWaterAmount = -1;
		}*/
		//if (prevWater != waterAmount || prevDownStream != downstreamWaterAmount || prevUpStream != upstreamWaterAmount || getWorld().getGameTime() % (2) == 0) {
			if(world.isRemote)
				ModelDataManager.requestModelDataRefresh(this);
			world.func_225319_b(pos, this.getBlockState(), this.getBlockState());
			world.setBlockState(pos, getBlockState().with(SluiceBlock.FLOWING, waterAmount > 0));
			//world.markBlockRangeForRenderUpdate(pos, pos);
		//}
		if(waterAmount > 0 && !world.isRemote) {
			Material mat = world.getBlockState(pos.offset(dir).down()).getMaterial();
			if(mat == Material.AIR) {
				world.setBlockState(pos.offset(dir).down(), HarderOres.ModBlocks.sluice_output.getDefaultState(), 3);
			}
		}
	}
	
	public void updateNeighborChanged() {
		if(world.isRemote)
			ModelDataManager.requestModelDataRefresh(this);
		world.func_225319_b(pos, this.getBlockState(), this.getBlockState());
	}

	protected int getWaterAmount() {
		return this.waterAmount;
	}

	@Override
    public IModelData getModelData() {
		Builder state = new ModelDataMap.Builder();
		//boolean hasWater = this.getWaterAmount() > 0;
		float dir = 0;//(float) getFlowDirection(oldState);
		state = state.withInitial(FLOW_DIRECTION, dir);
		//float waterHeight = getWaterAmount() * 14f / 16;

		float[][] corner = new float[2][2];
		corner[0][0] = getCorner(getBlockState(), world,pos,Direction.NORTH, Direction.WEST);
		corner[0][1] = getCorner(getBlockState(), world,pos,Direction.SOUTH, Direction.WEST);
		corner[1][1] = getCorner(getBlockState(), world,pos,Direction.SOUTH, Direction.EAST);
		corner[1][0] = getCorner(getBlockState(), world,pos,Direction.NORTH, Direction.EAST);
		
		boolean anyZero = false;
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				if(corner[i][j] == 0) {
					anyZero = true;
				}
			}
		}
		if(anyZero) {
			for(int i = 0; i < 2; i++) {
				for(int j = 0; j < 2; j++) {
					if(corner[i][j] == 0) {
						corner[i][j] = 0;
					}
				}
			}
		}
		
		state = state.withInitial(LEVEL_CORNERS_0, corner[0][0]);
		state = state.withInitial(LEVEL_CORNERS_1, corner[0][1]);
		state = state.withInitial(LEVEL_CORNERS_2, corner[1][1]);
		state = state.withInitial(LEVEL_CORNERS_3, corner[1][0]);
		
		state = state.withInitial(WATER_COLOR, BiomeColors.getWaterColor(world, pos));
		return state.build();
        //return new ModelDataMap.Builder().withInitial(WATER, this.waterAmount).build();
    }

	private static float getCorner(BlockState state, World world, BlockPos pos, Direction NS, Direction EW) {
		Direction dir = state.get(BlockStateProperties.HORIZONTAL_FACING);
		SluiceTileEntity teSelf = (SluiceTileEntity)world.getTileEntity(pos);
		if(teSelf.getWaterAmount() <= 0) return 0.001f;
		if(dir != NS && dir != EW) {
			BlockState upstream = world.getBlockState(pos.offset(dir.getOpposite(), 1));
			if(upstream.getBlock() == HarderOres.ModBlocks.sluice) {
				SluiceTileEntity teUp = (SluiceTileEntity) world.getTileEntity(pos.offset(dir.getOpposite(),1));
				//if(teUp.getWaterAmount()-1 <= 0) return 0;
				return ((teUp.getWaterAmount()-1) * 3f/32f);
			}
			else if(upstream.getFluidState().getFluid() == Fluids.WATER) {
				return 0.885f;
			}
			else if(upstream.getFluidState().getFluid() == Fluids.FLOWING_WATER) {
				return upstream.getFluidState().getLevel() * 2f/16f * 0.885f;
			}
		}
		else {
			BlockState downstream = world.getBlockState(pos.offset(dir, 1));
			if(downstream.getBlock() == HarderOres.ModBlocks.sluice) {
				SluiceTileEntity teDown = (SluiceTileEntity) world.getTileEntity(pos.offset(dir, 1));
				if(teDown.getWaterAmount() == 0) return 2f/16f;
				
				return (Math.min((teDown.getWaterAmount()+1),teSelf.getWaterAmount()+1) * 3f/32f);
			}
			else if(Block.hasSolidSide(downstream, world, pos, dir)) {
				return 2f/16f;
			}
			else if(!Block.hasSolidSide(downstream, world, pos, dir.getOpposite())) {
				return 2f/16f;
			}
		}
		return 0;
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
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
		read(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		tag = super.write(tag);
		tag.put("harderores:inputslot", inputSlot.serializeNBT());
		tag.putInt("harderores:waterAmount", waterAmount);
		//tag.putFloat("harderores:siftTime", siftTime);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag) {
		super.read(tag);
		inputSlot.deserializeNBT(tag.getCompound("harderores:inputslot"));
		waterAmount = tag.getInt("harderores:waterAmount");
		//siftTime = tag.getFloat("harderores:siftTime");
	}
	
	@Override
	public void remove() {
		super.remove();
		inputHandler.invalidate();
		if(waterAmount > 0 && !world.isRemote) {
			Direction dir = this.getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
			Block blk = world.getBlockState(pos.offset(dir).down()).getBlock();
			if(blk == HarderOres.ModBlocks.sluice_output) {
				world.setBlockState(pos.offset(dir).down(), Blocks.AIR.getDefaultState(), 3);
			}
		}
	}
}
