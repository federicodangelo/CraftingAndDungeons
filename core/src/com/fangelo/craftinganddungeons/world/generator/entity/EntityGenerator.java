package com.fangelo.craftinganddungeons.world.generator.entity;

import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.world.generator.utils.PerlinNoise;

public class EntityGenerator {
	
	public enum Type {
		Random,
		Perlin
	}
	
	private String entityId;
	private EntityEntry entity;
	private Type type = Type.Random;
	
	private int chanceIn = 1; //as in "one chance in N"
	
	private int octNum = 4; //Used if type = Perlin
	private int frq = 512; //Used if type = Perlin
	private int minValue = -1; //Used if type = Perlin
	private int maxValue = -1; //Used if type = Perlin
	private int modX = 1;
	private int modY = 1;
	private int modXY = 2; //entity placement looks nicer with this default value (two entities will never be next to each others)
	
	//Initialized from chanceIn in init()
	private float chance;
	
	public String getEntityId() {
		return entityId;
	}
	
	public EntityEntry getEntity() {
		return entity;
	}
	
	public int getChanceIn() {
		return chanceIn;
	}
	
	public void init(Catalog catalog) {
		entity = catalog.getEntityById(entityId);
		
		if (chanceIn > 0)
			chance = 1.0f / chanceIn;
		else
			chance = -1.0f;
	}		
	
	public EntityEntry evaluate(int x, int y, PerlinNoise itemGenerator, float chance) {
		
		if (chance > this.chance)
			return null;
		
		if (x % modX != 0 || y % modY != 0 || (x + y) % modXY != 0)
			return null;
		
		switch(type) {
		case Random:
			return entity;
			
		case Perlin:
			int val = itemGenerator.fractalNoise2D(x, y, octNum, frq, 1);
			if ((minValue < 0 || val >= minValue) && (maxValue < 0 || val <= maxValue))
				return entity;
			break;
		}
		
		return null;
	}
	
		
}
