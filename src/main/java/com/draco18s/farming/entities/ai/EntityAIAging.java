package com.draco18s.farming.entities.ai;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import com.draco18s.farming.util.AnimalUtil;
import com.draco18s.hardlib.api.HardLibAPI;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class EntityAIAging extends EntityAIBase {
	private final WeakReference<EntityAnimal> entity;
	private final WeakReference<EntityAIMate> breedTask;
	private final EntityAgeTracker age;
	private final Class species;
	private final Random rand;
	
	//private static Field targetMate = ReflectionHelper.findField(EntityAIMate.class, "targetMate","field_75391_e");
	
	public EntityAIAging(Random random, EntityAnimal ent, EntityAgeTracker ageTracker) {
		rand = random;
		entity = new WeakReference<EntityAnimal>(ent);
		species = ent.getClass();
		age = ageTracker;
		age.ageFactor = HardLibAPI.animalManager.getAgeSpeed(species);
		EntityAIMate t = null;
		for(EntityAITaskEntry a : entity.get().tasks.taskEntries) {
			if(a.action instanceof EntityAIMate) {
				t = (EntityAIMate) a.action;
				break;
			}
		}
		if(t != null) {
			breedTask = new WeakReference<EntityAIMate>(t);
		}
		else {
			breedTask = null;
		}
	}

	@Override
	public boolean shouldExecute() {
		if(AnimalUtil.animalGlobalAgeRate == 0) {
			return false;
		}
		if(entity.get() instanceof EntityHorse) {
			return !((EntityHorse)entity.get()).isTame();
		}
		else if(entity.get() instanceof IEntityOwnable) {
			return ((IEntityOwnable)entity.get()).getOwner() == null;
		}
		return true;
	}

	@Override
	public void updateTask() {
		//setup initial data
		if(age.deathAge <= 0) {
			age.entityAge = entity.get().getEntityData().getInteger("AnimalAge");
			age.deathAge = entity.get().getEntityData().getInteger("AnimalDeathAge");
			if(age.deathAge <= 0) {
				if(HardLibAPI.animalManager.isUnaging(species)) {
					age.entityAge = (int)(AnimalUtil.animalMaxAge * 2.5f) + 1000;
					age.deathAge = age.entityAge * 5;
				}
				else {
					if(entity.get().isChild()) {
						age.entityAge = 0;
					}
					else {
						age.entityAge = (48000 + rand.nextInt(120000));
					}
					age.deathAge = (AnimalUtil.animalMaxAge + rand.nextInt(60000)) / age.ageFactor;
				}
			}
		}
		
		if(HardLibAPI.animalManager.isUnaging(species) || breedTask == null) {
			/*Unaging animals do not age, do not die, and do not procreate*/
			return;
		}
		for(int a=0;a<AnimalUtil.animalGlobalAgeRate;a++) {
			age.entityAge++;
	
			if(age.entityAge % (100) == 0) {
				entity.get().getEntityData().setInteger("AnimalAge", age.entityAge);
				entity.get().getEntityData().setInteger("AnimalDeathAge", age.deathAge);
			}
			if(age.entityAge > age.deathAge && entity.get().hurtResistantTime <= 0) {
				entity.get().attackEntityFrom(DamageSource.WITHER, 0.5f);
				if(age.entityAge > age.deathAge+1000*age.ageFactor) {
					entity.get().attackEntityFrom(DamageSource.WITHER, 1.5f);
				}
				if(age.entityAge > age.deathAge+10000*age.ageFactor) {
					entity.get().attackEntityFrom(DamageSource.WITHER, 15f);
				}
			}
			else if(age.entityAge % (150) == 0) {
				List ents = entity.get().world.getEntitiesWithinAABB(species, getAABB(entity.get().posX, entity.get().posY, entity.get().posZ));
				String bioName = entity.get().world.getBiomeForCoordsBody(new BlockPos(entity.get().posX, entity.get().posY, entity.get().posZ)).getRegistryName().toString();
				int extraCountAllowed = 0;
				if(bioName.contains("rainbow") && bioName.contains("forest")) {
					extraCountAllowed = 25;
				}
				if(!entity.get().isInLove() && entity.get().getGrowingAge() == 0) {
					if(ents.size() <= 5 && rand.nextInt(200) == 0) {
						entity.get().setInLove(null);
						//entity.get().func_146082_f(null);
					}
					else if(ents.size() <= 12 && rand.nextInt(450) == 0) {
						entity.get().setInLove(null);
						//entity.get().func_146082_f(null);
					}
					else if(rand.nextInt(600) == 0) {
						entity.get().setInLove(null);
						//entity.get().func_146082_f(null);
					}
				}
	
				if(ents.size() > 20+extraCountAllowed) {
					if(entity.get().isChild()) {
						age.deathAge -= 2 * (ents.size()-20-extraCountAllowed);
					}
					else {
						age.deathAge -= 15 * (ents.size()-20-extraCountAllowed);//20 or 25 again?
					}
				}
			}
			
			EntityPlayer player = entity.get().world.getClosestPlayerToEntity(entity.get(), 32);
			if(entity.get().isInLove()) {
				if(player == null && rand.nextInt(100) == 0) {
					List ents = entity.get().world.getEntitiesWithinAABB(species, getAABB(entity.get().posX, entity.get().posY, entity.get().posZ));
					if(ents.size() > 0) {
						EntityAnimal mate = null;
						EntityAnimal anim;
						for(Object obj : ents) {
							if(obj != entity) {
								anim = (EntityAnimal)obj;
								if(anim != entity.get() && mate == null && anim.isInLove()) {
									mate = anim;
								}
							}
						}
						if(mate != null) {
							procreate();
							mate.resetInLove();
						}
					}
				}
			}
			//this sort of mucking about with other AI classes
			//to get entities to move towards each other and clump up
			//just causes things to crash :(
			/*else if(ReflectionHelper.getPrivateValue(EntityAIMate.class, breedTask.get(), "targetMate","field_75391_e") == null) {
				if(rand.nextInt(200) == 0) {
					List ents = entity.get().world.getEntitiesWithinAABB(species, getAABB(entity.get().posX, entity.get().posY, entity.get().posZ));
					if(ents.size() > 0) {
						EntityAnimal animal = (EntityAnimal) ents.get(rand.nextInt(ents.size()));
						
						ReflectionHelper.setPrivateValue(EntityAIMate.class, breedTask.get(), animal, "targetMate","field_75391_e");
						//this.entity.get().setAttackTarget(animal);
					}
				}
			}
			else if(ReflectionHelper.getPrivateValue(EntityAIMate.class, breedTask.get(), "targetMate","field_75391_e") instanceof EntityAnimal) {
				EntityAnimal animal = ReflectionHelper.getPrivateValue(EntityAIMate.class, breedTask.get(), "targetMate","field_75391_e");
				double dx = entity.get().posX - animal.posX;
				double dy = entity.get().posY - animal.posY;
				double dz = entity.get().posZ - animal.posZ;
				dx *= dx;
				dy *= dy;
				dz *= dz;
				dx += dy + dz;
				if(dx < 100) {
					//this.entity.get().setAttackTarget(null);
					ReflectionHelper.setPrivateValue(EntityAIMate.class, breedTask.get(), null, "targetMate","field_75391_e");
				}
			}*/
		}
	}

	private AxisAlignedBB getAABB(double x, double y, double z) {
		return new AxisAlignedBB(x, y, z, x+1, y+1, z+1).expand(48, 16, 48);
	}
	
	private void procreate() {
		EntityAgeable entityageable = entity.get().createChild(entity.get());

		if (entityageable != null) {
			entity.get().setGrowingAge(6000);
			entity.get().setGrowingAge(6000);
			entity.get().resetInLove();
			entityageable.setGrowingAge(-24000);
			entityageable.setLocationAndAngles(entity.get().posX, entity.get().posY, entity.get().posZ, entity.get().rotationYaw, entity.get().rotationPitch);

			for (int i = 0; i < 7; ++i) {
				double d0 = rand.nextGaussian() * 0.02D;
				double d1 = rand.nextGaussian() * 0.02D;
				double d2 = rand.nextGaussian() * 0.02D;
				entity.get().world.spawnParticle(EnumParticleTypes.HEART, entity.get().posX + (double)(rand.nextFloat() * entity.get().width * 2.0F) - (double)entity.get().width, entity.get().posY + 0.5D + (double)(rand.nextFloat() * entity.get().height), entity.get().posZ + (double)(rand.nextFloat() * entity.get().width * 2.0F) - (double)entity.get().width, d0, d1, d2);
			}

			entity.get().world.spawnEntity(entityageable);
		}
	}
}
