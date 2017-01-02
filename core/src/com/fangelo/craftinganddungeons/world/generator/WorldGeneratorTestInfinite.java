package com.fangelo.craftinganddungeons.world.generator;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.world.WorldBounds;
import com.fangelo.craftinganddungeons.world.map.WorldMap;

public class WorldGeneratorTestInfinite extends WorldGenerator {

	static private final int TEST_SIZE = 16384;
	
	private int startingMapX;
	private int startingMapY;
	
	protected void onInit() {
		
		worldBounds = new WorldBounds(MAP_SIZE_TILE * TEST_SIZE - 1, MAP_SIZE_TILE * TEST_SIZE - 1);
		
		startingMapX = TEST_SIZE / 2;
		startingMapY = TEST_SIZE / 2;
		
		startingPosition = new GridPoint2(startingMapX * MAP_SIZE_TILE, startingMapY * MAP_SIZE_TILE);
	}

	@Override
	protected void onGenerateMap(WorldMap map) {
		map.fillFloor(catalog.getFloorById("floor-grass"));
		
		if (map.getMapX() == startingMapX && map.getMapY() == startingMapY) {
			//Starting map!
			
			for (int x = 3; x <= 6; x++)
				for (int y = 3; y <= 6; y++)
					map.setFloorTile(x + map.getOffsetX(), y + map.getOffsetX(), catalog.getFloorById("floor-water"));
			
			for (int i = 0; i < 300; i++) {
				
				int x = random.nextInt(MAP_SIZE_TILE - 10) + 5 + map.getOffsetX(); //leave borders walkable
				int y = random.nextInt(MAP_SIZE_TILE - 10) + 5 + map.getOffsetX();
				EntityEntry entity = catalog.getEntities().get(MathUtils.random(catalog.getEntities().size() - 1)); 
			
				map.setEntityTile(x, y, entity);
			}
			
		} else {
			
			for (int i = 0; i < 300; i++) {
				
				int x = random.nextInt(MAP_SIZE_TILE - 1) + map.getOffsetX();
				int y = random.nextInt(MAP_SIZE_TILE - 1) + map.getOffsetY();
				EntityEntry entity = catalog.getEntities().get(MathUtils.random(catalog.getEntities().size() - 1)); 
			
				map.setEntityTile(x, y, entity);
			}
		}
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
