package com.draco18s.farming.entities.capabilities;

import com.draco18s.farming.util.AnimalUtil;

public interface IMilking {
	int getMilkLevel();
	void setMilkLevel(int milkLevel);
	void doMilking();
	boolean getIsMilkable();
}
