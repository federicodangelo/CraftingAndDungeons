package com.fangelo.craftinganddungeons.world.generator.configuration;

import com.badlogic.gdx.utils.Json;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorType;

public class WorldGeneratorConfiguration {
	
	public long seed;
	
	public WorldGeneratorType generatorType;
	
	//Used by some generators
	public WorldGeneratorExtraConfiguration extraConfiguration;
	
	static public Json getJsonParser() {
		Json json = new Json();
		return json;
	}
}
