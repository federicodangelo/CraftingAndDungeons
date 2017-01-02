package com.fangelo.craftinganddungeons.world.generator;

import com.badlogic.gdx.math.GridPoint2;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.world.WorldBounds;
import com.fangelo.craftinganddungeons.world.generator.utils.Palette;
import com.fangelo.craftinganddungeons.world.generator.utils.PerlinNoise;
import com.fangelo.craftinganddungeons.world.map.WorldMap;

public class WorldGeneratorTestPerlin extends WorldGenerator {
	
	static private final int TEST_SIZE = 16384;
	
	private int startingMapX;
	private int startingMapY;
	
	private FloorEntry floorWhite;
	
	private PerlinNoise generator;
	
	private float[] palette;
	
	protected void onInit() {
		
		worldBounds = new WorldBounds(MAP_SIZE_TILE * TEST_SIZE - 1, MAP_SIZE_TILE * TEST_SIZE - 1);
		
		startingMapX = TEST_SIZE / 2;
		startingMapY = TEST_SIZE / 2;
		
		startingPosition = new GridPoint2(startingMapX * MAP_SIZE_TILE, startingMapY * MAP_SIZE_TILE);
		
		floorWhite = catalog.getFloorById("floor-white");
		
		palette = new Palette().getLookupTable();
	}
	
	@Override
	protected void initRandomGenerators() {
		super.initRandomGenerators();
		generator = new PerlinNoise(configuration.seed, 0, 0);
	}

	@Override
	protected void onGenerateMap(WorldMap map) {
		
		for (int x = 0; x < MAP_SIZE_TILE; x++) {
			for (int y = 0; y < MAP_SIZE_TILE; y++) {
				map.setFloorTile(x + map.getOffsetX(), y + map.getOffsetY(), floorWhite);
				map.setFloorColor(x + map.getOffsetX(), y + map.getOffsetY(), getFloorColor(x + map.getOffsetX(), y + map.getOffsetY()));
			}
		}
	}
	
	private float getFloorColor(int x, int y) {
		
		int value = generator.fractalNoise2D(x, y, 4, 512, 1);
		
		return palette[value];
	}

	@Override
	protected boolean onGetEnterMapId(String mapId, int x, int y, WorldGeneratorTransitionInfo transitionInfo) {
		return false;
	}

	@Override
	protected boolean onGetExitMapId(String mapId, int x, int y, WorldGeneratorTransitionInfo transitionInfo) {
		return false;
	}
}
