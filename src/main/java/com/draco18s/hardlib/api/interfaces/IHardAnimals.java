package com.draco18s.hardlib.api.interfaces;

import net.minecraft.entity.passive.EntityAnimal;

public interface IHardAnimals {
	public void addHerbivore(Class<? extends EntityAnimal> animal);

	public boolean isHerbivore(Class<? extends EntityAnimal> animal);

	public void addUnaging(Class<? extends EntityAnimal> animal);

	public boolean isUnaging(Class<? extends EntityAnimal> animal);

	public void addAgeSpeed(Class<? extends EntityAnimal> animal, int rate);

	public int getAgeSpeed(Class<? extends EntityAnimal> animal);
}
