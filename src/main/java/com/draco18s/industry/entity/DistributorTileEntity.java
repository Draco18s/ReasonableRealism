package com.draco18s.industry.entity;

import com.draco18s.industry.ExpandedIndustry;
import com.draco18s.industry.inventory.DistributorContainer;

import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DistributorTileEntity extends AbstractHopper {
	private int delay = 4;

	public DistributorTileEntity() {
		super(ExpandedIndustry.ModTileEntities.machine_distributor);
	}
	
	@Override
	public void tick() {
		BlockState state = world.getBlockState(pos);
		Direction face = state.get(HopperBlock.FACING);
		if(face == Direction.DOWN || face == Direction.UP) {
			world.setBlockState(this.getPos(), state.with(HopperBlock.FACING, Direction.NORTH), 3);
		}
		else {
			delay--;
			if(delay <= 0) {
				face = face.rotateY();
				world.setBlockState(this.getPos(), state.with(HopperBlock.FACING, face), 2);
				delay = 4;
				setTransferCooldown(0);
				super.tick();
				//updateHopper();
				world.getPendingBlockTicks().scheduleTick(pos,this.getBlockState().getBlock(),0);
				this.markDirty();
			}
		}
	}

	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity player) {
		return new DistributorContainer(windowID, playerInventory, inventory, this);
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("expindustry:distributor.name");
	}

	/*@Override
	public void openGUI(ServerPlayerEntity player) {
		if (!world.isRemote) {
			NetworkHooks.openGui(player, this, getPos());
		}
	}*/
}
