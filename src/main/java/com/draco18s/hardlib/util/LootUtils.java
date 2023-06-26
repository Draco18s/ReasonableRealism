package com.draco18s.hardlib.util;

public class LootUtils {
	/*private static Field tablePools;
	private static Field poolEntries;
	private static Field entryItem;

	static {
		tablePools = ObfuscationReflectionHelper.findField(LootTable.class, "pools");
		tablePools.setAccessible(true);
		poolEntries = ObfuscationReflectionHelper.findField(LootPool.class, "lootEntries");
		poolEntries.setAccessible(true);
		entryItem = ObfuscationReflectionHelper.findField(ItemLootEntry.class, "item");
		entryItem.setAccessible(true);
	}
	/***
	 * Removes the specified item from the indicated loot table
	 * @param table
	 * @param toRemove
	 * @return returns if any entries were removed
	 */
	/*@SuppressWarnings("unchecked")
	public static boolean removeLootFromTable(LootTable table, Item toRemove) {
		List<LootPool> pools;
		try {
			pools = (List<LootPool>)tablePools.get(table);
			for(LootPool pool : pools) {
				List<LootEntry> entries = (List<LootEntry>)poolEntries.get(pool);
				Iterator<LootEntry> it = entries.iterator();
				while(it.hasNext()) {
					LootEntry entry = it.next();
					if(entry instanceof ItemLootEntry) {
						ItemLootEntry itementry = (ItemLootEntry)entry;
						Item i = (Item)entryItem.get(itementry);
						if(i == toRemove) {
							it.remove();
							return true;
						}
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}*/
}
