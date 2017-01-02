package com.fangelo.craftinganddungeons.world.generator.biome;

import com.badlogic.gdx.Gdx;
import com.fangelo.craftinganddungeons.catalog.Catalog;

public class Biomes {
	
	private int step = 25;
	private Biome[] biomes;
	
	//Initialized in init()
	private int steps;
	private Biome[] lookupTable;
	
	public Biome[] getBiomes() {
		return biomes;
	}
	
	public void init(Catalog catalog) {
		if (biomes == null)
			biomes = new Biome[0];
		
		for (int i = 0; i < biomes.length; i++)
			biomes[i].init(catalog, step);
		
		if (step <= 0 || 100 % step != 0) {
			Gdx.app.error("BIOME", "The value of step must be above 5, and 100 / step must have no remaining value");
			return;
		}
		
		steps = (100 / step) + 1;
		
		lookupTable = new Biome[steps * steps * steps];
		
		for (int temperature = 0; temperature < steps; temperature++)
			for (int rainfall = 0; rainfall < steps; rainfall++)
				for (int height = 0; height < steps; height++)
					lookupTable[temperature * (steps * steps) + rainfall * steps + height] = 
						getBiomeWithoutLookupTable(temperature * step, rainfall * step, height * step);
	}
	
	public Biome getBiome(String id) {
		for (int i = 0; i < biomes.length; i++)
			if (biomes[i].getId().equals(id))
				return biomes[i];
		
		return null;
	}
	
	public Biome getBiome(int temperature, int rainfall, int height) {
		temperature /= step;
		rainfall /= step;
		height /= step;
		
		return lookupTable[temperature * (steps * steps) + rainfall * steps + height];
	}
	
	private Biome getBiomeWithoutLookupTable(int temperature, int rainfall, int height) {
		for (int i = 0; i < biomes.length; i++)
			if (biomes[i].fits(temperature, rainfall, height))
				return biomes[i];
		
		//First biome is also default biome if something fails
		return biomes[0];
	}
}
