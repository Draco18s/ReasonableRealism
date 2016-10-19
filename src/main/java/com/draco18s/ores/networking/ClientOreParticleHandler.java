package com.draco18s.ores.networking;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.draco18s.hardlib.interfaces.IBlockMultiBreak;
import com.draco18s.ores.client.ProspectorParticle;
import com.draco18s.ores.client.ProspectorParticleDust;

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

public class ClientOreParticleHandler implements IMessageHandler<ToClientMessageOreParticles, IMessage> {

	private static int RADAR = 0;
	private static int DUST = 1;
	
    public IMessage onMessage(final ToClientMessageOreParticles message, final MessageContext ctx) {
        IThreadListener mainThread = Minecraft.getMinecraft();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
            	EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                //System.out.println(String.format("Received %s from %s", message.oreAt, p.getDisplayName()));
            	drawParticle(p.worldObj,getParticle(p.worldObj, message.oreAt, message.eventAt, RADAR, 0));
            	drawParticle(p.worldObj,getParticle(p.worldObj, message.oreAt, message.eventAt, DUST, 0));
            	drawParticle(p.worldObj,getParticle(p.worldObj, message.oreAt, message.eventAt, DUST, -4));
            }
        });
        return null;
    }
    
    private static void drawParticle(World worldObj, Particle particle) {
    	if(particle != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }
    
    //@SideOnly(Side.CLIENT)
    private static Particle getParticle(World worldObj, BlockPos oreAt, BlockPos eventAt, int id, int startingAge) {
		Particle particle = null;
		if(id == RADAR) {
			float x, y, z;
			x = ((int)((float)Math.random() * 4f))/5f + 0.1f;
			y = 0.5f + (float)Math.random() * 0.75f;
			z = ((int)((float)Math.random() * 4f))/5f + 0.1f;
			particle = new ProspectorParticle(worldObj, oreAt.getX()+x, oreAt.getY()+y, oreAt.getZ()+z, 0, 0, 0);//3, 20
			IBlockState state = worldObj.getBlockState(oreAt);
			Block block = state.getBlock();
			if(block instanceof IBlockMultiBreak) {
				Color c = ((IBlockMultiBreak)block).getProspectorParticleColor(worldObj, oreAt, state);
				particle.setRBGColorF(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
			}
		}
		if(id == DUST) {
			float x, y, z;
			x = ((int)((float)Math.random() * 8f))/10f + 0.1f;
			y = 0.5f + (float)Math.random() * 0.5f;
			z = ((int)((float)Math.random() * 8f))/10f + 0.1f;
			particle = new ProspectorParticleDust(worldObj, eventAt.getX()+x, eventAt.getY()+y, eventAt.getZ()+z, 0, 0, 0,startingAge);//3, 20
			IBlockState state = worldObj.getBlockState(oreAt);
			Block block = state.getBlock();
			if(block instanceof IBlockMultiBreak) {
				Color c = ((IBlockMultiBreak)block).getProspectorParticleColor(worldObj, oreAt, state);
				particle.setRBGColorF(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
			}
		}
		return particle;
    }
}
