package com.fangelo.craftinganddungeons.world.generator;

import com.fangelo.craftinganddungeons.savegame.SaveSlotData;
import com.fangelo.craftinganddungeons.world.generator.configuration.WorldGeneratorConfiguration;

public class WorldGeneratorParameters {
	
	public SaveSlotData slot;
	
	public WorldGeneratorConfiguration configuration;
	
	public WorldGeneratorParameters() {
		
	}
	
	public WorldGeneratorParameters(SaveSlotData slot, WorldGeneratorConfiguration configuration) {
		this.slot = slot;
		this.configuration = configuration;
	}
}
