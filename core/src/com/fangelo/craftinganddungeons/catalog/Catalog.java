package com.fangelo.craftinganddungeons.catalog;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class Catalog {
	
	private ArrayList<FloorEntry> floors;
	private ObjectMap<String, FloorEntry> floorsById;
	private FloorEntry emptyFloor;
	
	private ArrayList<EntityEntry> entities;
	private ObjectMap<String, EntityEntry> entitiesById;
		
	public ArrayList<FloorEntry> getFloors() {
		return floors;
	}
	
	public FloorEntry getFloorById(String id) {
		
		FloorEntry floor = floorsById.get(id);
		
		if (floor == null) {
			Gdx.app.error("CATALOG", "Floor not found: " + id);
			return null;
		}
		
		return floor;
	}
	
	public FloorEntry getEmptyFloor() {
		return emptyFloor;
	}

	public ArrayList<EntityEntry> getEntities() {
		return entities;
	}
	
	public EntityEntry getEntityById(String id) {
		
		EntityEntry entity = entitiesById.get(id);
		
		if (entity == null) {
			Gdx.app.error("CATALOG", "Entity not found: " + id);
			return null;
		}
		
		return entity;
	}
	
	//Called after deserialization to initialize any remaining values
	public void init(WorldTiles gameTiles) {
		
		floorsById = new ObjectMap<String, FloorEntry>();
		for (int i = 0; i < floors.size(); i++)
			floorsById.put(floors.get(i).id, floors.get(i));
			
		entitiesById = new ObjectMap<String, EntityEntry>();
		for (int i = 0; i < entities.size(); i++)
			entitiesById.put(entities.get(i).id, entities.get(i));
		
		for (int i = 0; i < floors.size(); i++)
			floors.get(i).init(this, gameTiles);
		
		for (int i = 0; i < entities.size(); i++)
			entities.get(i).init(this, gameTiles);
		
		//Add floor <-> tile entities
		for (int i = 0; i < floors.size(); i++) {
			EntityEntry floorToEntity = floors.get(i).getEntityEntry();
			entities.add(floorToEntity);
			entitiesById.put(floorToEntity.id, floorToEntity);
		}
			
		emptyFloor = getFloorById("floor-dirt");
	}
}
