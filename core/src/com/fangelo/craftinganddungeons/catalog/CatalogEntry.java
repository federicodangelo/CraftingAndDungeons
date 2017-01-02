package com.fangelo.craftinganddungeons.catalog;

import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class CatalogEntry {
	protected String id;
	
	public String getId() {
		return id;
	}
	
	//Called after deserialization to initialize any remaining values
	public void init(Catalog catalog, WorldTiles gameTiles) {
		
	}
}
