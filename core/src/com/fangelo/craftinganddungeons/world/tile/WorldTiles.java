package com.fangelo.craftinganddungeons.world.tile;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.ObjectMap;

public class WorldTiles {
	
	static public final String ATLAS_1X_NAME = "packed";
	static public final String ATLAS_2X_NAME = "packed2x";
	static public final String ATLAS_4X_NAME = "packed4x";
		
	private TextureAtlas atlas;
	private AtlasRegion[] tiles;
	private ObjectMap<String, AtlasRegion> tilesByName;
	
	private ArrayList<AtlasRegion> tilesNotUsed;
	
	public AtlasRegion[] getTiles() {
		return tiles;
	}
	
	public ArrayList<AtlasRegion> getTilesNotUsed() {
		return tilesNotUsed;
	}
	
	public AtlasRegion getTileByName(String name) {
		AtlasRegion tile = tilesByName.get(name);
		
		if (tile == null) {
			Gdx.app.error("WORLD_TILES", "Tile not found: " + name);
			return null;
		}
		
		tilesNotUsed.remove(tile);
		
		return tile;
	}
	
	public WorldTiles(int scale) {
		this(getAtlasFile(scale));
	}
	
	static private FileHandle getAtlasFile(int scale) {
		switch(scale) {
		case 1:
			return Gdx.files.internal("tiles/" + ATLAS_1X_NAME + ".atlas");
		case 2:
			return Gdx.files.internal("tiles/" + ATLAS_2X_NAME + ".atlas");
		case 4:
			return Gdx.files.internal("tiles/" + ATLAS_4X_NAME + ".atlas");
		}
		
		return null;
	}
	
	public WorldTiles(FileHandle atlasFile) {
		
		atlas = new TextureAtlas(atlasFile, true);
		
		tiles = atlas.getRegions().toArray(AtlasRegion.class);
		tilesByName = new ObjectMap<String, AtlasRegion>();
		tilesNotUsed = new ArrayList<AtlasRegion>();
		
		for (int i = 0; i < tiles.length; i++) {
			tilesByName.put(tiles[i].name, tiles[i]);
			tilesNotUsed.add(tiles[i]);
		}
	}	
	
	public void dispose() {
		atlas.dispose();
	}
}
