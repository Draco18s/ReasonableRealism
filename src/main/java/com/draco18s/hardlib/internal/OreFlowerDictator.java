package com.draco18s.hardlib.internal;

public class OreFlowerDictator {
	public final int spawnChance;
	public final int spawnDistance;
	
	public OreFlowerDictator(int chance, int dist) {
		if(chance < 1) chance = 1;
		spawnChance = chance;
		spawnDistance = dist;
	}
	
	public OreFlowerDictator clone() {
		return new OreFlowerDictator(this.spawnChance, this.spawnDistance);
	}
}
