package com.fangelo.craftinganddungeons.world.generator.biome;

import com.badlogic.gdx.Gdx;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.world.generator.entity.EntityGenerator;
import com.fangelo.craftinganddungeons.world.generator.utils.HashXX;
import com.fangelo.craftinganddungeons.world.generator.utils.PerlinNoise;

public class Biome {
	
	private String id;
	
	private String floorId;
	private FloorEntry floor;
	
	private EntityGenerator[] entities;
	
	private String dungeonEntranceId;
	private EntityEntry dungeonEntrance; 
	
	private String dungeonExitId;
	private EntityEntry dungeonExit; 
	
	private int minHeight = -1;
	private int maxHeight = -1;
	
	private int minTemperature = -1;
	private int maxTemperature = -1;
	
	private int minRainfall = -1;
	private int maxRainfall = -1;
	
	
	public String getId() {
		return id;
	}
	
	public EntityEntry getEntity(int x, int y, PerlinNoise itemGenerator, long seed) {
		
		float chance = HashXX.range((int) seed, 0, 1000000 + 1, x, y) / 1000000.0f;
		
		//We do this so all the items have (on average) the same chance of being selected first if they all
		//have the same probability
		int offset = x + y;
		if (offset < 0)
			offset = -offset;
		
		for (int i = 0; i <entities.length; i++) {
			EntityEntry entity = entities[(i + offset) % entities.length].evaluate(x, y, itemGenerator, chance);
			if (entity != null)
				return entity;
		}
		
		return null;
	}
	
	public FloorEntry getFloor(int x, int y, long seed) {
		return floor;
	}
	
	public EntityEntry getDungeonEntrance() {
		return dungeonEntrance;
	}
	
	public EntityEntry getDungeonExit() {
		return dungeonExit;
	}
	
	public void init(Catalog catalog, int step) {
		floor = catalog.getFloorById(floorId);
		if (entities == null)
			entities = new EntityGenerator[0];
		
		if (dungeonEntranceId != null && dungeonEntranceId.length() > 0 &&
			dungeonExitId != null && dungeonExitId.length() > 0) {
			
			dungeonEntrance = catalog.getEntityById(dungeonEntranceId);
			
			if (dungeonEntrance != null && !dungeonEntrance.getTag().equals(EntityEntry.TAG_DUNGEON_ENTRANCE))
				Gdx.app.error("BIOME", "Biome " +  id + ": The value of dungeonEntrance is not a valid dungeon entrance");
			
			dungeonExit = catalog.getEntityById(dungeonExitId);
			if (dungeonExit != null && !dungeonExit.getTag().equals(EntityEntry.TAG_DUNGEON_EXIT))
				Gdx.app.error("BIOME", "Biome " +  id + ": The value of dungeonExit is not a valid dungeon exit");
		}
		
		for (int i = 0; i < entities.length; i++)
			entities[i].init(catalog);
		
		if (minHeight > 0 && minHeight % step != 0)
			Gdx.app.error("BIOME", "Biome " +  id + ": The value of minHeight must be a multiple of " + step);
		
		if (maxHeight > 0 && maxHeight % step != 0)
			Gdx.app.error("BIOME", "Biome " +  id + ": The value of maxHeight must be a multiple of " + step);
		
		if (minTemperature > 0 && minTemperature % step != 0)
			Gdx.app.error("BIOME", "Biome " +  id + ": The value of minTemperature must be a multiple of " + step);
		
		if (maxTemperature > 0 && maxTemperature % step != 0)
			Gdx.app.error("BIOME", "Biome " +  id + ": The value of maxTemperature must be a multiple of " + step);
		
		if (minRainfall > 0 && minRainfall % step != 0)
			Gdx.app.error("BIOME", "Biome " +  id + ": The value of minRainfall must be a multiple of " + step);
		
		if (maxRainfall > 0 && maxRainfall % step != 0)
			Gdx.app.error("BIOME", "Biome " +  id + ": The value of maxRainfall must be a multiple of " + step);
	}
	
	public boolean fits(int temperature, int rainfall, int height) {
		return 
				(minTemperature < 0 || temperature >= minTemperature) &&
				(maxTemperature < 0 || temperature < maxTemperature) &&
				
				(minRainfall < 0 || rainfall >= minRainfall) &&
				(maxRainfall < 0 || rainfall < maxRainfall) &&
				
				(minHeight < 0 || height >= minHeight) &&
				(maxHeight < 0 || height < maxHeight);
	}
}
