package com.draco18s.hardlib.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.draco18s.hardlib.HardLib;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementTreeNode;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AdvancementUtils {
	private static Field listField = ReflectionHelper.findField(AdvancementManager.class, "ADVANCEMENT_LIST","field_192784_c");
	private static Field allAdvField = ReflectionHelper.findField(AdvancementList.class, "advancements","field_192092_b");
	private static Field nonRootsField = ReflectionHelper.findField(AdvancementList.class, "nonRoots","field_192094_d");
	private static Field parentField = ReflectionHelper.findField(Advancement.class, "parent","field_192076_a");
	/**
	 * Will asign a given target advancement a new parent and rebuild the layout.<br>
	 * Note: not intended to result in advancements changing to a new root.
	 * @param targetRL - the advancement to change
	 * @param newParentRL - the new parent
	 * @param rebuildLayout - whether or not to rebuild the layout
	 */
	public static void reparentAdvancement(ResourceLocation targetRL, ResourceLocation newParentRL, String modID, boolean rebuildLayout) {
		//AdvancementManager manager = event.getWorld().getMinecraftServer().getAdvancementManager();
		//listField.setAccessible(true);
		AdvancementList theList = null;
		Map<ResourceLocation, Advancement> allAdv = null;
		Set<Advancement> nonRoots = null;
		Advancement targParent = null;
		Advancement target = null;
		try {
			theList = (AdvancementList) listField.get(null);
			allAdv = (Map<ResourceLocation, Advancement>) allAdvField.get(theList);
			nonRoots = (Set<Advancement>) nonRootsField.get(theList);
			target = allAdv.get(targetRL);
			targParent = allAdv.get(newParentRL);
			if(targParent == null) {
				HardLib.logger.log(Level.INFO, modID + ": failed to reparent achievements!"); 
				return;
			}
			boolean didAnything = false;
			for(Advancement obj : nonRoots) {
				if(obj != null && obj.getId().equals(targetRL)) {
					HardLib.logger.log(Level.INFO, modID + ": " + obj.getId() + " reparented to " + targParent.getId());
					if(obj.getParent() != targParent) {
						if(obj.getParent() != null) {
							Advancement p = obj.getParent();
							Iterator<Advancement> it = p.getChildren().iterator();
							while(it.hasNext()) {
								Advancement adv = it.next();
								if(adv == obj) {
									it.remove();
									break;
								}
							}
						}
						parentField.set(obj, targParent);
						targParent.addChild(obj);
						didAnything = true;
					}
				}
			}
			if(didAnything && rebuildLayout) {
				while(target.getParent() != null) {
					target = target.getParent();
				}
				AdvancementTreeNode.layout(target);
			}
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void removeAdvancement(ResourceLocation targetRL, String modID) {
		AdvancementList theList = null;
		Map<ResourceLocation, Advancement> allAdv = null;
		Set<Advancement> nonRoots = null;
		try {
			theList = (AdvancementList) listField.get(null);
			allAdv = (Map<ResourceLocation, Advancement>) allAdvField.get(theList);
			nonRoots = (Set<Advancement>) nonRootsField.get(theList);
			Advancement adv = allAdv.get(targetRL);
			if(adv == null) {
				HardLib.logger.log(Level.WARN, "Attempted to remove an advancement " + targetRL +", but it doesn't exist");
				return;
			}
			if(!adv.getChildren().iterator().hasNext()) {
				nonRoots.remove(adv);
				allAdv.remove(targetRL);
				HardLib.logger.log(Level.INFO, modID + ": removed " + targetRL);
			}
			else {
				HardLib.logger.log(Level.WARN, "Attempted to remove an advancement " + targetRL +", but it has children!");
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
