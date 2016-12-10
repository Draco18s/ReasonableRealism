package com.draco18s.ores.client;

import com.draco18s.hardlib.blockproperties.Props;
import com.draco18s.hardlib.blockproperties.ores.MillstoneOrientation;
import com.draco18s.ores.entities.TileEntityMillstone;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;

public class SoundWindmill extends PositionedSound implements ITickableSound {
	protected TileEntityMillstone millstone;
	protected BlockPos position;
	protected boolean donePlaying;

	protected SoundWindmill(ResourceLocation soundId, SoundCategory categoryIn, TileEntityMillstone mill) {
		super(soundId, categoryIn);
        this.repeat = true;
        millstone = mill;
        position = millstone.getPos();
        xPosF = position.getX();
        yPosF = position.getY();
        zPosF = position.getZ();
	}

	@Override
	public void update() {
		TileEntity te = millstone.getWorld().getTileEntity(position);
		MillstoneOrientation orient = millstone.getWorld().getBlockState(position).getValue(Props.MILL_ORIENTATION);
        if (!(te != null && te instanceof TileEntityMillstone) || !millstone.canGrind(orient) || millstone.getGrindTime() <= 0) {
        	donePlaying = true;
        }
	}

	@Override
	public boolean isDonePlaying() {
		return donePlaying;
	}
}
