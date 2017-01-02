package com.fangelo.craftinganddungeons.world.generator;

public class WorldGeneratorFactory {
	static public WorldGenerator getWorldGenerator(WorldGeneratorType type) {
		switch(type) {
		case Biomes:
			return new WorldGeneratorBiomes();
		case TestInfinite:
			return new WorldGeneratorTestInfinite();
		case TestPerlin:
			return new WorldGeneratorTestPerlin();
		}
		return null;
	}
}
