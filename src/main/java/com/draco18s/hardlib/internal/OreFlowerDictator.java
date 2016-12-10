package com.draco18s.hardlib.internal;

/**
 * Static dictators available for ease of use.  Most things use default, redstone uses close.
 *
 */
public class OreFlowerDictator {
	public static OreFlowerDictator defaultDictator = new OreFlowerDictator(200, 30);
	public static OreFlowerDictator closeDictator = new OreFlowerDictator(200, 15);
	public static OreFlowerDictator rareDictator = new OreFlowerDictator(400, 30);
	public static OreFlowerDictator closeRareDictator = new OreFlowerDictator(400, 15);
	public static OreFlowerDictator commonDictator = new OreFlowerDictator(100, 30);
	public static OreFlowerDictator closeCommonDictator = new OreFlowerDictator(100, 15);
	
	public final int spawnChance;
	public final int spawnDistance;
	
	/**
	 * Proability of flower spawning and distance away from the ore block
	 * @param chance
	 * @param dist
	 */
	public OreFlowerDictator(int chance, int dist) {
		if(chance < 1) chance = 1;
		spawnChance = chance;
		spawnDistance = dist;
	}
	
	public OreFlowerDictator clone() {
		return new OreFlowerDictator(this.spawnChance, this.spawnDistance);
	}
}
