package com.draco18s.farming.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.Level;

import com.draco18s.farming.FarmingBase;
import com.draco18s.hardlib.api.interfaces.IHardAnimals;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class AnimalUtil implements IHardAnimals {
	private HashMap<Class<? extends EntityAnimal>, Integer> ageRates = new HashMap<Class<? extends EntityAnimal>, Integer>();
	private ArrayList<Class<? extends EntityAnimal>> herbivores = new ArrayList<Class<? extends EntityAnimal>>();
	private ArrayList<Class<? extends EntityAnimal>> immortals = new ArrayList<Class<? extends EntityAnimal>>();
	private ImmutableSet<ClassInfo> classesInPassive;
	
	public static int animalGlobalAgeRate;
	public static int animalMaxAge;
	//public static int grassFrequency;
	public static int milkQuanta = 6000;
	
	public AnimalUtil() {
		ClassLoader classloader = ClassLoader.getSystemClassLoader();
		ClassPath p;
		try {
			p = ClassPath.from(classloader);
			classesInPassive = p.getTopLevelClasses("net.minecraft.entity.passive");
		} catch (IOException e) {
			classesInPassive = null;
		}
	}
	
	@Override
	public int getAgeSpeed(Class<? extends EntityAnimal> animal) {
		if(ageRates.containsKey(animal))
			return ageRates.get(animal);
		return 2;
	}
	
	@Override
	public void addUnaging(Class<? extends EntityAnimal> animal) {
		immortals.add(animal);
	}
	
	@Override
	public boolean isUnaging(Class<? extends EntityAnimal> animal) {
		return immortals.contains(animal);
	}
	
	@Override
	public void addHerbivore(Class<? extends EntityAnimal> animal) {
		herbivores.add(animal);
	}
	
	@Override
	public boolean isHerbivore(Class<? extends EntityAnimal> animal) {
		return herbivores.contains(animal);
	}
	
	@Override
	public void addAgeSpeed(Class<? extends EntityAnimal> animal, int rate) {
		ageRates.put(animal, rate);
	}
	
	public void parseConfig(Configuration config) {
		animalMaxAge = config.getInt("animalMaxAge", "ANIMALS", 24000, 5000, 2000000, "Maximum age of animals, in seconds.\nNote: Animals may have an accelerated aging rate, this value is the general-case value.\n") * 40;
		animalGlobalAgeRate = config.getInt("GlobalAgeRate", "ANIMALS", 1, 0, 10, "This is a global aging rate.  It causes animals to age more quickly or not at all.\n");
		Property listOfAnimals = config.get("ANIMALS", "AcceleratedAging", new String[]{"Chicken"}, "List of animals with special age scaling.\nAnimal is looked up by class name, capitalize first letter.\nYou must pass a fully qualified class name for mod animals (JD Gui can help locate that).\nDefault age for all unspecified animals is 2.\n");
		String[] list = listOfAnimals.getStringList();
		config.getInt("AgeFactorChicken", "ANIMALS", 3, 0, 100, "Lower's Chickens maximum age by this factor (maxAge = defaultMaxAge / ageFactor).\n");

		//grassFrequency = config.getInt("HerbivoreEatFrequency", "ANIMALS", 3000, 100, 20000, "How often herbivores will kill a block of grass or leaves, in ticks, reducing food supply.\n");
		
		for(String s:list) {
			if(s.indexOf(".") >= 0) {
				String[] st = s.split("\\.");
				String s2 = st[st.length-1];
				int speed = config.getInt("AgeFactor"+s2, "ANIMALS", 2, 0, 100, "Lower's " + s2 + " maximum age by this factor (maxAge = defaultMaxAge / ageFactor).\n");
				try {
					Class clz = Class.forName(s);
					addAgeSpeed(clz, speed);
				}
				catch (Exception e) {
					FarmingBase.logger.log(Level.ERROR, "No mod entity found for " + s);
					ClassLoader classloader = ClassLoader.getSystemClassLoader();
					ClassPath p;
					String st2 = "";
					for(int si=0; si<st.length-1; si++) {
						st2 += st[si];
						if(si != st.length-2) {
							st2+=".";
						}
					}
					ImmutableSet<ClassInfo> classes;
					try {
						p = ClassPath.from(classloader);
						classes = p.getTopLevelClasses(st2);
					} catch (IOException e2) {
						classes = null;
					}
					if(classes != null) {
						for(ClassInfo info : classes) {
							String i = info.getSimpleName().substring(6);
							int ld = LevenshteinDistance(s, s.length(), i, i.length());
							if(ld <= Math.max(i.length()/3,5)) {
								FarmingBase.logger.log(Level.INFO, "Did you perhaps mean '" + i + "'?");
							}
						}
					}
					else {
						FarmingBase.logger.log(Level.INFO, "Unable to get top level class names for " + st2 + ", check your spelling.");
					}
				}
			}
			else {
				int speed = config.getInt("AgeFactor"+s, "ANIMALS", 2, 0, 100, "Lower's " + s + " maximum age by this factor (maxAge = defaultMaxAge / ageFactor).\n");
				try {
					Class clz = Class.forName("net.minecraft.entity.passive.Entity"+s);
					addAgeSpeed(clz, speed);
				}
				catch (ClassNotFoundException e) {
					FarmingBase.logger.log(Level.ERROR, "No entity found for " + s); //net.minecraft.entity.passive.Entity
					if(classesInPassive != null) {
						for(ClassInfo info : classesInPassive) {
							String i = info.getSimpleName().substring(6);
							int ld = LevenshteinDistance(s, s.length(), i, i.length());
							if(ld <= Math.max(i.length()/3,5)) {
								FarmingBase.logger.log(Level.INFO, "Did you perhaps mean '" + i + "'?");
							}
						}
					}
				}
			}
		}
		listOfAnimals = config.get("ANIMALS", "AnimalIsHerbivore", new String[]{}, "List of mod-added animals which should be treated as herbivores.\nYou must use fully qualified names.\nNote that some animals may extend from vanilla classes; these will already be handled.\n");
		list = listOfAnimals.getStringList();
		for(String s:list) {
			if(s.indexOf(".") >= 0) {
				try {
					Class clz = Class.forName(s);
					addHerbivore(clz);
				}
				catch (Exception e) {
					FarmingBase.logger.log(Level.ERROR, "No mod entity found for " + s);
				}
			}
		}
		config.addCustomCategoryComment("ANIMALS", "Fully qualified class names include the full package declaration; e.g. com.draco18s.wildlife.entity.EntityGoat\nString lists have 1 entry per line with no commas. Eg:\n	S:AcceleratedAging <\n		Chicken\n		com.draco18s.wildlife.entity.EntityGoat\n	 >");
		config.save();
	}
	
	private static int LevenshteinDistance(String s, int len_s, String t, int len_t) {
	  int cost = -1;
		/* base case: empty strings */
	  if (len_s == 0) return len_t;
	  if (len_t == 0) return len_s;
	 
	  /* test if last characters of the strings match */
	  if (s.charAt(len_s-1) == t.charAt(len_t-1))
		  cost = 0;
	  else
		  cost = 1;
	 
	  /* return minimum of delete char from s, delete char from t, and delete char from both */
	  return minimum(LevenshteinDistance(s, len_s - 1, t, len_t	) + 1,
					 LevenshteinDistance(s, len_s	, t, len_t - 1) + 1,
					 LevenshteinDistance(s, len_s - 1, t, len_t - 1) + cost);
	}

	private static int minimum(int i, int j, int k) {
		int a = Math.min(i, j);
		int b = Math.min(j, k);
		return Math.min(a, b);
	}
}
