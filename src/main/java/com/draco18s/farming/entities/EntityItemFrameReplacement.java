package com.draco18s.farming.entities;

import com.draco18s.hardlib.interfaces.IItemFrameOutput;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityItemFrameReplacement extends EntityItemFrame implements IEntityAdditionalSpawnData {
	public EntityItemFrameReplacement(World worldIn) {
		super(worldIn);
	}

	public EntityItemFrameReplacement(World worldIn, BlockPos p_i45852_2_, EnumFacing p_i45852_3_) {
		super(worldIn, p_i45852_2_, p_i45852_3_);
	}

	@Override
	public int getAnalogOutput()
	{
		if(this.getDisplayedItem() == null) return 0;
		int rot = this.getRotation() % 8 + 1;
		Item item = this.getDisplayedItem().getItem();
		if(item instanceof IItemFrameOutput) return ((IItemFrameOutput)item).getRedstoneOutput(this, rot);
		if(item == Items.CLOCK) {
            double d0;

            if (worldObj.provider.isSurfaceWorld())
            {
                d0 = (double)worldObj.getCelestialAngle(1.0F);
            }
            else
            {
                return 13;
            }
            d0 = MathHelper.positiveModulo((float)d0, 1.0F);
            int t = (int)Math.floor(d0 * 24);
            t = t % 12;
            ItemCompass l;
            return t+1;
		}
		/*if(item == Items.COMPASS) {
            double d0;
			if (worldObj.provider.isSurfaceWorld())
            {
                double d1 = this.getRotation();
                d1 = d1 % 360.0D;
                //what entity?
                double d2 = this.getSpawnToAngle(worldObj, entity);
                d0 = Math.PI - ((d1 - 90.0D) * 0.01745329238474369D - d2);
            }
            else
            {
                d0 = 13;
            }
		}*/
		return rot;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		PacketBuffer pack = new PacketBuffer(buffer);
		pack.writeBlockPos(getHangingPosition());
		pack.writeEnumValue(this.getHorizontalFacing());
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		PacketBuffer pack = new PacketBuffer(buffer);
		hangingPosition = pack.readBlockPos();
		updateFacingWithBoundingBox(pack.readEnumValue(EnumFacing.class));
	}
}
