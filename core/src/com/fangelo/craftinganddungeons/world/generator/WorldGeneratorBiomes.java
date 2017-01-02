package com.fangelo.craftinganddungeons.world.generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Json;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.world.WorldBounds;
import com.fangelo.craftinganddungeons.world.generator.biome.Biome;
import com.fangelo.craftinganddungeons.world.generator.biome.Biomes;
import com.fangelo.craftinganddungeons.world.generator.utils.HashXX;
import com.fangelo.craftinganddungeons.world.generator.utils.PerlinNoise;
import com.fangelo.craftinganddungeons.world.map.WorldMap;

public class WorldGeneratorBiomes extends WorldGenerator {
	
	static private final int WORLD_TO_DUNGEON_CHANCE = 10;
	static private final int DUNGEON_TO_DUNGEON_CHANCE = 50;
	
	static private final class EncodedDungeonInfo {
		
		public int mapX;
		public int mapY;
		public int depth;
		public long seed;
		public String biome;
				
		public int upX; //upstairs
		public int upY; //upstairs
		
		public int downX; //downstairs (-1 if disabled)
		public int downY; //downstairs (-1 if disabled)
		
		public boolean decode(String mapId) {
			String[] strs = mapId.split("_");
			
			if (strs.length == 10) {
				try {
					//Skip dungeon prefix
					mapX = Integer.parseInt(strs[1]);
					mapY = Integer.parseInt(strs[2]);
					depth = Integer.parseInt(strs[3]);
					seed = Long.parseLong(strs[4]);
					biome = strs[5];
					
					upX = Integer.parseInt(strs[6]);
					upY = Integer.parseInt(strs[7]);
					
					downX = Integer.parseInt(strs[8]);
					downY = Integer.parseInt(strs[9]);
					
					return true;
				}catch(Exception ex) {
					Gdx.app.error("GENERATOR", ex.toString());
				}
			}
			
			return false;
		}
		
		public String encode() {
			return String.format("dungeon_%d_%d_%d_%d_%s_%d_%d_%d_%d",
					mapX, mapY, depth, seed, biome,
					upX, upY,
					downX, downY );
		}
	}
	
	static private final int WORLD_SIZE = 16384;
	
	private int startingMapX;
	private int startingMapY;
	
	private PerlinNoise generatorTemperature;
	private PerlinNoise generatorRainfall;
	private PerlinNoise generatorHeight;
	private PerlinNoise generatorEntities;
	
	private Biomes biomes;
	
	protected void onInit() {
		
		worldBounds = new WorldBounds(MAP_SIZE_TILE * WORLD_SIZE - 1, MAP_SIZE_TILE * WORLD_SIZE - 1);
		
		startingMapX = WORLD_SIZE / 2;
		startingMapY = WORLD_SIZE / 2;
		
		//Starting position: middle of starting map
		startingPosition = new GridPoint2(startingMapX * MAP_SIZE_TILE + MAP_SIZE_TILE / 2, startingMapY * MAP_SIZE_TILE + MAP_SIZE_TILE / 2);
		
		biomes = new Json().fromJson(Biomes.class, Gdx.files.internal("data/biomes.txt"));
		biomes.init(world.getCatalog());
	}
	
	@Override
	protected void initRandomGenerators() {
		super.initRandomGenerators();
		
		generatorTemperature = new PerlinNoise(configuration.seed + 1, random.nextInt(99999), random.nextInt(99999));
		generatorRainfall = new PerlinNoise(configuration.seed - 1, random.nextInt(99999), random.nextInt(99999));
		generatorHeight = new PerlinNoise(configuration.seed - 2, random.nextInt(99999), random.nextInt(99999));
		generatorEntities = new PerlinNoise(configuration.seed - 3, random.nextInt(99999), random.nextInt(99999));
	}

	@Override
	protected void onGenerateMap(WorldMap map) {
		
		if (map.getMapId().length() > 0)
			generateDungeon(map);
		else
			generateBiome(map);
	}
	
	private EncodedDungeonInfo tmpGenerateDungeonInfo = new EncodedDungeonInfo();
	
	private void generateDungeon(WorldMap map) {
		
		FloorEntry floorEmpty = catalog.getFloorById("floor-dungeon-rock-empty");
		
		tmpGenerateDungeonInfo.decode(map.getMapId());
		
		//If it's not the dungeon map coordinates, it's empty space
		if (map.getMapX() != tmpGenerateDungeonInfo.mapX || map.getMapY() != tmpGenerateDungeonInfo.mapY) {
			for (int y = 0; y < MAP_SIZE_TILE; y++) {
				int realY = y + map.getOffsetY();
				int realX = map.getOffsetX();
				for (int x = 0; x < MAP_SIZE_TILE; x++) {
					map.setFloorTile(realX, realY, floorEmpty);
					realX++;
				}
			}
			return;
		}
		
		//Add floor
		FloorEntry floor = catalog.getFloorById("floor-dungeon-dirt");
		map.fillFloorTiles(0, 0, MAP_SIZE_TILE, MAP_SIZE_TILE, floor);
		
		//Add house.. TEST!!
		//map.fillEntityTiles9Slice(64, 64, 3, 3, "house1", true);
		//map.setEntityTile(65 + map.getOffsetX(), 66 + map.getOffsetY(), catalog.getEntityById("house1-bottom-door"));
		
		//map.fillEntityTiles9Slice(70, 64, 3, 3, "house2", true);
		//map.setEntityTile(71 + map.getOffsetX(), 66 + map.getOffsetY(), catalog.getEntityById("house2-bottom-door"));
		
		//Add walls surrounding dungeon
		map.fillEntityTiles9Slice(0, 0, MAP_SIZE_TILE, MAP_SIZE_TILE, "wall-dungeon-rock", false);
		
		//Empty floor, so it matches transition to empty floor tiles..
		for (int x = 0; x < MAP_SIZE_TILE; x++)
			map.setFloorTile(x + map.getOffsetX(), map.getOffsetY() + MAP_SIZE_TILE - 1, floorEmpty); 
		
		//Add upstairs 
		Biome biome = biomes.getBiome(tmpGenerateDungeonInfo.biome);
		if (biome != null && biome.getDungeonExit() != null)
			map.setEntityTile(tmpGenerateDungeonInfo.upX, tmpGenerateDungeonInfo.upY, biome.getDungeonExit());
		
		//Add downstairs
		if (tmpGenerateDungeonInfo.downX != -1 && tmpGenerateDungeonInfo.downY != -1) {
			if (biome != null && biome.getDungeonEntrance() != null)
				map.setEntityTile(tmpGenerateDungeonInfo.downX, tmpGenerateDungeonInfo.downY, biome.getDungeonEntrance());
		}
	}
	
	private GridPoint2 getDungeonEntranceCoordinates(int mapX, int mapY, GridPoint2 toReturn) {
		
		if (HashXX.range((int) configuration.seed, 0, 100, mapX, mapY) < 100 - WORLD_TO_DUNGEON_CHANCE)
			return null;
		
		int x = HashXX.range((int) configuration.seed + 1, 5, MAP_SIZE_TILE - 5, mapX, mapY);
		int y = HashXX.range((int) configuration.seed - 1, 5, MAP_SIZE_TILE - 5, mapX, mapY);
		
		int realX = x + mapX * MAP_SIZE_TILE;
		int realY = y + mapY * MAP_SIZE_TILE;
		Biome biome = getBiome(realX, realY);
		
		if (biome.getDungeonEntrance() == null)
			return null;
		
		return toReturn.set(realX, realY);
	}
	
	private EncodedDungeonInfo tmpOnGetEnterMapIdDungeonInfo = new EncodedDungeonInfo();
	
	@Override
	protected boolean onGetEnterMapId(String mapId, int x, int y, WorldGeneratorTransitionInfo transitionInfo) {
		
		EncodedDungeonInfo dungeonInfo = null;
		
		if (mapId.length() == 0) {
			dungeonInfo = getDungeonInfo(-1, x / MAP_SIZE_TILE, y / MAP_SIZE_TILE, getBiome(x, y).getId());
		} else if (tmpOnGetEnterMapIdDungeonInfo.decode(mapId)) {
			dungeonInfo = getDungeonInfo(tmpOnGetEnterMapIdDungeonInfo.depth - 1, tmpOnGetEnterMapIdDungeonInfo.mapX, tmpOnGetEnterMapIdDungeonInfo.mapY, tmpOnGetEnterMapIdDungeonInfo.biome);
		}
		
		if (dungeonInfo != null) {
			transitionInfo.mapId = dungeonInfo.encode();
			transitionInfo.x = dungeonInfo.upX;
			transitionInfo.y = dungeonInfo.upY;
			return true;
		}
		
		return false;
	}
	
	private EncodedDungeonInfo getDungeonInfo(int depth, int mapX, int mapY, String biome) {
		
		EncodedDungeonInfo dungeonInfo = new EncodedDungeonInfo();
		
		dungeonInfo.biome = biome;
		dungeonInfo.depth = depth;
		dungeonInfo.seed = HashXX.getRandom((int) configuration.seed + depth, mapX, mapY);
		dungeonInfo.mapX = mapX;
		dungeonInfo.mapY = mapY;
		
		if (depth + 1 == 0) {
			//World above
			GridPoint2 coord = new GridPoint2();
			if (getDungeonEntranceCoordinates(mapX, mapY, coord) != null) {
				dungeonInfo.upX = coord.x;
				dungeonInfo.upY = coord.y;
			}
		} else {
			//Another dungeon above
			dungeonInfo.upX = dungeonInfo.mapX * MAP_SIZE_TILE + HashXX.range((int) configuration.seed + (depth + 1) * 7 + 1, 5, MAP_SIZE_TILE - 5, mapX, mapY);
			dungeonInfo.upY = dungeonInfo.mapY * MAP_SIZE_TILE + HashXX.range((int) configuration.seed + (depth + 1) * 7 - 1, 5, MAP_SIZE_TILE - 5, mapX, mapY);
		}
		
		if (HashXX.range((int) configuration.seed + depth * 7, 0, 100, mapX, mapY) < 100 - DUNGEON_TO_DUNGEON_CHANCE) {
			dungeonInfo.downX = -1;
			dungeonInfo.downY = -1;
		} else {
			dungeonInfo.downX = dungeonInfo.mapX * MAP_SIZE_TILE + HashXX.range((int) configuration.seed + (depth + 0) * 7 + 1, 5, MAP_SIZE_TILE - 5, mapX, mapY);
			dungeonInfo.downY = dungeonInfo.mapY * MAP_SIZE_TILE + HashXX.range((int) configuration.seed + (depth + 0) * 7 - 1, 5, MAP_SIZE_TILE - 5, mapX, mapY);
			
			if (dungeonInfo.downX == dungeonInfo.upX && dungeonInfo.downY == dungeonInfo.upY) {
				//Collision, no downstairs
				dungeonInfo.downX = -1;
				dungeonInfo.downY = -1;
			}
		}
				
		return dungeonInfo;
	}
	
	private EncodedDungeonInfo tmpOnGetExitMapIdDungeonInfo = new EncodedDungeonInfo();
	
	@Override
	protected boolean onGetExitMapId(String mapId, int x, int y, WorldGeneratorTransitionInfo transitionInfo) {
		
		if (tmpOnGetExitMapIdDungeonInfo.decode(mapId)) {
			if (tmpOnGetExitMapIdDungeonInfo.depth + 1 == 0) {
				
				transitionInfo.mapId = "";
				transitionInfo.x = tmpOnGetExitMapIdDungeonInfo.upX;
				transitionInfo.y = tmpOnGetExitMapIdDungeonInfo.upY;
				
			} else {
				
				EncodedDungeonInfo dungeon2 = getDungeonInfo(tmpOnGetExitMapIdDungeonInfo.depth + 1, tmpOnGetExitMapIdDungeonInfo.mapX, tmpOnGetExitMapIdDungeonInfo.mapY, tmpOnGetExitMapIdDungeonInfo.biome);
				
				transitionInfo.mapId = dungeon2.encode();
				transitionInfo.x = dungeon2.downX;
				transitionInfo.y = dungeon2.downY;
			}
			
			return true;
		}
		
		return false;
	}
	
	
	private GridPoint2 tmpGenerateBiomeDungeonEntrancePosition = new GridPoint2();
	private void generateBiome(WorldMap map) {
		
		for (int y = 0; y < MAP_SIZE_TILE; y++) {
			int realY = y + map.getOffsetY();
			int realX = map.getOffsetX();
			for (int x = 0; x < MAP_SIZE_TILE; x++) {
				Biome biome = getBiome(realX, realY);
				float floorColor = getFloorColor(biome, realX, realY);
				
				FloorEntry floor = biome.getFloor(realX, realY, configuration.seed);
				map.setFloorTile(realX, realY, floor);
				map.setFloorColor(realX, realY, floorColor);
				
				EntityEntry entity = biome.getEntity(realX, realY, generatorEntities, configuration.seed);
				
				map.setEntityTile(realX, realY, entity);
				map.setEntityColor(realX, realY, floorColor);
				
				realX++;
			}
		}

		//Add dungeon entrance
		GridPoint2 dungeonEntrancePosition = getDungeonEntranceCoordinates(map.getMapX(), map.getMapY(), tmpGenerateBiomeDungeonEntrancePosition);
		if (dungeonEntrancePosition != null) {
			Biome biome = getBiome(dungeonEntrancePosition.x, dungeonEntrancePosition.y);
			map.setEntityTile(dungeonEntrancePosition.x, dungeonEntrancePosition.y, biome.getDungeonEntrance());
		}
		
		if (map.getMapX() == startingMapX && map.getMapY() == startingMapY) {
			//Starting map, clear tiles surrounding starting position
			for (int x = -2; x <= 2; x++)
				for (int y = -2; y <= 2; y++)
					map.setEntityTile(startingPosition.x + x, startingPosition.y + y, null);
		}
	}
	
	//private Palette palette = new Palette();
	
	private float getFloorColor(Biome biome, int x, int y) {
		
		//int height = generatorHeight.fractalNoise2D(x, y, 4, 1024, 1);
		
		//return palette.get(height / 255.0f).toFloatBits();
		
		int temperature = generatorTemperature.fractalNoise2D(x, y, 2, 1024, 1);
		  
		//Higher temperature -> more red and green
		int r = 200 + (255 - 200) * temperature / 255;
		int g = 200 + (255 - 200) * temperature / 255;
		
		//Lower temperature -> more blue
		int b = 200 + (255 - 200) * (255 - temperature) / 255;
		
		return Color.toFloatBits(r, g, b, 255);
	}
	
	private Biome getBiome(int x, int y) {
		int temperature = generatorTemperature.fractalNoise2D(x, y, 2, 1024, 1);
		int rainfall = generatorRainfall.fractalNoise2D(x, y, 4, 512, 1);
		int height = generatorHeight.fractalNoise2D(x, y, 4, 1024, 1);
		
		temperature = temperature * 100 / 255; //Normalized to 0 - 100
		rainfall = rainfall * 100 / 255; //Normalized to 0 - 100
		height = height * 100 / 255; //Normalized to 0 - 100
		
		return biomes.getBiome(temperature, rainfall, height);
	}
}
