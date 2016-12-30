package com.draco18s.ores.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.draco18s.ores.entities.TileEntityBasicSluice;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockSluice extends Block {
	//public static final IUnlistedProperty<Integer> LEVEL = Properties.toUnlisted(PropertyInteger.create("level", 0, 15));
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	private static final AxisAlignedBB PARTIAL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2f/16f, 1.0D);

	public BlockSluice() {
		super(Material.WOOD);

		setHardness(2.0f);
		setHarvestLevel("axe", 1);
		setResistance(2.0f);
		setCreativeTab(CreativeTabs.DECORATIONS);
		setDefaultState(this.getDefaultState().withProperty(FACING, EnumFacing.NORTH));//.withProperty(LEVEL, 0));
	}

	@Override
	protected BlockStateContainer createBlockState() {	
		return new ExtendedBlockState(this, new IProperty[] { FACING }, BlockFluidBase.FLUID_RENDER_PROPS.toArray(new IUnlistedProperty<?>[0]) );
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		//int lv = state.getValue(LEVEL);
		int fc = state.getValue(FACING).getHorizontalIndex();
		return fc; //(fc << 2) | lv;
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta) {
		//int lv = meta & 3;
		int fc = (meta);// >> 2;
		return this.getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[fc]);
	}

	@Override
	public IBlockState getExtendedState(IBlockState oldState, IBlockAccess worldIn, BlockPos pos) {
		IExtendedBlockState state = (IExtendedBlockState) oldState;
		TileEntityBasicSluice te = (TileEntityBasicSluice) worldIn.getTileEntity(pos);
		boolean hasWater = te.getWaterAmount() > 0;

		float dir = (float) getFlowDirection(oldState);
		state = state.withProperty(BlockFluidBase.FLOW_DIRECTION, dir);

		float waterHeight = te.getWaterAmount() * 14f / 16;

		float[][] corner = new float[2][2];
		
		corner[0][0] = getCorner(worldIn,pos,oldState,EnumFacing.NORTH, EnumFacing.WEST);
		corner[0][1] = getCorner(worldIn,pos,oldState,EnumFacing.SOUTH, EnumFacing.WEST);
		corner[1][1] = getCorner(worldIn,pos,oldState,EnumFacing.SOUTH, EnumFacing.EAST);
		corner[1][0] = getCorner(worldIn,pos,oldState,EnumFacing.NORTH, EnumFacing.EAST);
		
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
		
		state = state.withProperty(BlockFluidBase.LEVEL_CORNERS[0], corner[0][0]);
		state = state.withProperty(BlockFluidBase.LEVEL_CORNERS[1], corner[0][1]);
		state = state.withProperty(BlockFluidBase.LEVEL_CORNERS[2], corner[1][1]);
		state = state.withProperty(BlockFluidBase.LEVEL_CORNERS[3], corner[1][0]);
		return state;
	}

	private float getCorner(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing NS, EnumFacing EW) {
		EnumFacing dir = state.getValue(FACING);
		TileEntityBasicSluice teSelf = (TileEntityBasicSluice) world.getTileEntity(pos);
		if(teSelf.getWaterAmount() <= 0) return 0.001f;
		if(dir != NS && dir != EW) {
			IBlockState upstream = world.getBlockState(pos.offset(dir.getOpposite(), 1));
			if(upstream.getBlock() == this) {
				TileEntityBasicSluice teUp = (TileEntityBasicSluice) world.getTileEntity(pos.offset(dir.getOpposite(),1));
				//if(teUp.getWaterAmount()-1 <= 0) return 0;
				return ((teUp.getWaterAmount()-1) * 3f/32f);
			}
			else if(upstream.getBlock() == Blocks.WATER) {
				return (1 - (upstream.getValue(BlockDynamicLiquid.LEVEL) * 2f/16f)) * 0.885f;
			}
			else if(upstream.getBlock() == Blocks.FLOWING_WATER) {
				return (1 - (upstream.getValue(BlockDynamicLiquid.LEVEL) * 2f/16f)) * 0.885f;
			}
		}
		else {
			IBlockState downstream = world.getBlockState(pos.offset(dir, 1));
			if(downstream.getBlock() == this) {
				TileEntityBasicSluice teDown = (TileEntityBasicSluice) world.getTileEntity(pos.offset(dir, 1));
				if(teDown.getWaterAmount() == 0) return 2f/16f;
				
				return ((teDown.getWaterAmount()+1) * 3f/32f);
			}
			else if(downstream.isSideSolid(world, pos, dir)) {
				return 2f/16f;
			}
			else if(!downstream.isSideSolid(world, pos, dir.getOpposite())) {
				return 2f/16f;
			}
		}
		return 0;
	}

	public static Vec3d getFlowVec(IBlockState blockState) {
		EnumFacing dir = blockState.getValue(FACING);
		return new Vec3d(dir.getFrontOffsetX() * 1, 0, dir.getFrontOffsetZ() * 1);
	}

	public static float getFlowDirection(IBlockState blockState) {
		Vec3d vec = getFlowVec(blockState);
		return (float)(Math.atan2(vec.zCoord, vec.xCoord) - Math.PI / 2D);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isFullyOpaque(IBlockState state) {
		return false;
	}

	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityBasicSluice();
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getUpdatedState(worldIn, pos,this.getDefaultState());
	}

	private IBlockState getUpdatedState(IBlockAccess world, BlockPos pos, IBlockState thisState) {
		for(EnumFacing face : EnumFacing.HORIZONTALS) {
			IBlockState bl = world.getBlockState(pos.offset(face,1));
			if(bl.getBlock() == Blocks.WATER) {
				if(bl.getValue(BlockLiquid.LEVEL) == 0)
					return thisState.withProperty(FACING, face.getOpposite());
			}
		}
		for(EnumFacing face : EnumFacing.HORIZONTALS) {
			IBlockState bl = world.getBlockState(pos.offset(face,1));
			if(bl.getBlock() == Blocks.FLOWING_WATER) {
				return thisState.withProperty(FACING, face.getOpposite());
			}
			if(bl.getBlock() == Blocks.WATER) {
				return thisState.withProperty(FACING, face.getOpposite());
			}
		}
		for(EnumFacing face : EnumFacing.HORIZONTALS) {
			IBlockState bl = world.getBlockState(pos.offset(face,1));
			if(bl.getBlock() == this && bl.getValue(FACING) == face.getOpposite()) {
				return thisState.withProperty(FACING, face.getOpposite());
			}
		}
		
		return thisState.withProperty(FACING, EnumFacing.NORTH);
	}

	@Override
	@Deprecated
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
		return null;
	}
	
	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return PARTIAL_AABB;
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn) {
		if(!world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP)) {
			if (!world.isRemote)  {
				dropBlockAsItem(world, pos, state, 0);
				world.setBlockToAir(pos);
				return;
			}
		}
		world.setBlockState(pos, getUpdatedState(world, pos, world.getBlockState(pos)), 3);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		EnumFacing dir = state.getValue(BlockSluice.FACING);
		if(!worldIn.isRemote && worldIn.getBlockState(pos.offset(dir).down()).getMaterial() == Material.WATER) {
			IBlockState st = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockDynamicLiquid.LEVEL, 1);
			worldIn.setBlockState(pos.offset(dir).down(), st, 3);
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		//TODO: all this would do is maybe drop a block of sand/gravel
		/*TileEntity tileentity = worldIn.getTileEntity(pos);

		IItemHandler inventory = worldIn.getTileEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
				null);
		for (int i = 0; i < inventory.getSlots(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			EntityItem entityIn;
			if (stack != null) {
				entityIn = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
				entityIn.setDefaultPickupDelay();
				worldIn.spawnEntityInWorld(entityIn);
			}
		}*/
		return super.removedByPlayer(state, worldIn, pos, player, willHarvest);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntityBasicSluice te = (TileEntityBasicSluice) worldIn.getTileEntity(pos);
		if (te.getWaterAmount() > 0 && te.getTime() <= 0) {
			this.spawnParticles(worldIn, pos);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP);
	}

	private void spawnParticles(World worldIn, BlockPos pos) {
		Random random = worldIn.rand;

		for (int i = 0; i < 5; ++i) {
			double d1 = (double) ((float) pos.getX() + random.nextFloat() / 2 + 0.25);
			double d2 = (double) ((float) pos.getY() + random.nextFloat());
			double d3 = (double) ((float) pos.getZ() + random.nextFloat() / 2 + 0.25);
			worldIn.spawnParticle(EnumParticleTypes.WATER_SPLASH, d1, d2, d3, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}
	
	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		IBlockState stateAtPos = world.getBlockState(pos.offset(face,1));
		Block bl = stateAtPos.getBlock();
		if(bl == Blocks.WATER || bl == Blocks.FLOWING_WATER || bl == this) {
			if(((TileEntityBasicSluice)world.getTileEntity(pos)).getWaterAmount() == 0) return false;
			return true;
		}
		if(face == EnumFacing.UP) return true;
		return state.isOpaqueCube();
	}
}
