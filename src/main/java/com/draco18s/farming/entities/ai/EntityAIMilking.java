package com.draco18s.farming.entities.ai;

import java.util.Random;

import com.draco18s.farming.FarmingBase;
import com.draco18s.farming.entities.capabilities.IMilking;
import com.draco18s.farming.util.AnimalUtil;

import net.minecraft.block.Block;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIMilking extends EntityAIBase {
	private final EntityAnimal entity;
	private final IMilking milk;
	private int ticks = 0;
	private boolean wasGrassNearRecently;
	
	public EntityAIMilking(EntityAnimal ent) {
		entity = ent;
		milk = FarmingBase.getMilkData(ent);
		//milk = new CowMilkLevel();
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}

	public boolean shouldCheckGrass() {
		if(AnimalUtil.animalGlobalAgeRate == 0) {
			return false;
		}
		return true;
	}

	@Override
	public void updateTask() {
		boolean hasGrass = false;
		if(shouldCheckGrass()) {
			Vec3d close = new Vec3d(0, -10, 0);
			hasGrass = checkGrass(close, entity);
		}
		else {
			hasGrass = wasGrassNearRecently;
		}
		if(hasGrass) {
			if(entity.getGrowingAge() == 0)
				milk.setMilkLevel(milk.getMilkLevel() + 1);
		}
		else {
			milk.setMilkLevel(milk.getMilkLevel() - 1);
			if(milk.getMilkLevel() % AnimalUtil.milkQuanta == 0){
				//agingTask.avoidStarving();
			}
		}
		
		milk.setMilkLevel(Math.max(Math.min(milk.getMilkLevel(), 3 * AnimalUtil.milkQuanta + 5000), 0));
		if(milk.getMilkLevel() % (600) == 0) {
			sendUpdatePacket(entity, milk);
		}
	}

	private static void sendUpdatePacket(EntityAnimal entity, IMilking milk) {
		
	}
	
	public boolean grassNearby() {
		return wasGrassNearRecently;
	}
	
	public static boolean checkGrass(Vec3d close, EntityAnimal entity) {
		boolean grassNear = false;
		//Vec3 close= Vec3.createVectorHelper(0, -10, 0);
		//Vec3 here= Vec3.createVectorHelper(0, -10, 0);
		boolean moo = entity instanceof EntityMooshroom;
		Vec3d here = new Vec3d(0,-10,0);
		Vec3d animpos= new Vec3d((int)entity.posX, (int)entity.posY, (int)entity.posZ);
		int d = 3;//(entity instanceof EntityGoat?9:3);
		int e = 2;//(entity instanceof EntityGoat?4:2);
		Block b = entity.world.getBlockState(new BlockPos(animpos.x, animpos.y-1, animpos.z)).getBlock();
		if(checkBlock(b, moo)) {
			close = animpos;
			return true;
		}
		
		
		for(int ox=(int)entity.posX-d;ox<=(int)entity.posX+d;ox++) {
			for(int oz=(int)entity.posZ-d;oz<=(int)entity.posZ+d;oz++) {
				for(int oy=(int)entity.posY-e;oy<=(int)entity.posY+e;oy++) {
					b = entity.world.getBlockState(new BlockPos(ox, oy, oz)).getBlock();
					if(checkBlock(b, moo)) {
						here = new Vec3d(ox,oy,oz);
						grassNear = true;
						if(here.squareDistanceTo(animpos) <= close.squareDistanceTo(animpos)) {
							close = new Vec3d(ox,oy,oz);
						}
					}
				}
			}
		}
		return grassNear;
	}

	private static boolean checkBlock(Block b, boolean moo) {
		return (b == Blocks.GRASS || b == Blocks.LEAVES || b == Blocks.LEAVES2 || (moo && b == Blocks.MYCELIUM));
	}
}
