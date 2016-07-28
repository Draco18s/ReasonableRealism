package com.draco18s.ores.networking;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.draco18s.hardlib.interfaces.IBlockMultiBreak;
import com.draco18s.ores.client.ProspectorParticle;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketHandlerClient implements IMessageHandler<ToClientMessage, IMessage> {

    public IMessage onMessage(final ToClientMessage message, final MessageContext ctx) {
        IThreadListener mainThread = Minecraft.getMinecraft();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
            	EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                System.out.println(String.format("Received %s from %s", message.pos, p.getDisplayName()));
            	drawParticle(p.worldObj, message.pos, "radar", 0);
            }
        });
        return null;
    }
    
    //@SideOnly(Side.CLIENT)
    private static void drawParticle(World worldObj, BlockPos pos, String par1Str, int age) {
		Particle particle = null;
		if(par1Str.equals("radar")) {
			//worldObj.spawnParticle(EnumParticleTypes.SPELL_INSTANT, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, 0.0D, 0.0D, 0.0D, new int[0]);
			particle = new ProspectorParticle(worldObj, pos.getX()+0.5f, pos.getY()+0.5f, pos.getZ()+0.5f, 0, 0, 0);//3, 20
			IBlockState state = worldObj.getBlockState(pos);
			Block block = state.getBlock();
			if(block instanceof IBlockMultiBreak) {
				Color c = ((IBlockMultiBreak)block).getProspectorParticleColor(worldObj, pos, state);
				particle.setRBGColorF(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
			}
		}
		if(particle != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }
}
