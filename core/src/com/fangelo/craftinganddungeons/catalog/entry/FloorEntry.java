package com.fangelo.craftinganddungeons.catalog.entry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.world.map.WorldMap;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class FloorEntry extends EntityEntry {
	
	static public final String FLOOR_TO_ENTITY_PREFIX = "floor-to-entity-";
	
	protected String[] imageVariations;
	protected FloorTransition[] transitions;
	
	private EntityEntry floorToEntityEntry;
	
	private AtlasRegion[] imageVariationsTextures;
	
	public String getEntityId() {
		return floorToEntityEntry.getId();
	}
	
	public EntityEntry getEntityEntry() {
		return floorToEntityEntry;
	}
	
	public FloorEntry() {
		solid = false;
		solidDiagonal = false;
		tag = "floor";
	}
	
	public final String[] getImageVariations() {
		return imageVariations;
	}
	
	public final AtlasRegion[] getImageVariationsTextures() {
		return imageVariationsTextures;
	}
	
	public final FloorTransition[] getTransitions() {
		return transitions;
	}
	
	public final AtlasRegion getFloorTexture(int x, int y, WorldMap map) {
		
		//This is wayyyyy toooo sloooww... find a better way...
		if (transitions != null && map != null) {
			for (int i = 0; i < transitions.length; i++) {
				
				FloorTransition transition = transitions[i];
				
				FloorEntry toFloorEntry = transitions[i].getToFloorEntry();
				
				if (map.getFloorTile(x - 1, y) == toFloorEntry) {
					
					if (map.getFloorTile(x, y - 1) == toFloorEntry)
						return transition.getBottomRightTexture();
					
					if (map.getFloorTile(x, y + 1) == toFloorEntry)
						return transition.getTopRightTexture();
					
					return transition.getRightTexture();
				}
				
				if (map.getFloorTile(x + 1, y) == toFloorEntry) {
					
					if (map.getFloorTile(x, y - 1) == toFloorEntry)
						return transition.getBottomLeftTexture();
					
					if (map.getFloorTile(x, y + 1) == toFloorEntry)
						return transition.getTopLeftTexture();
					
					return transition.getLeftTexture();
				}
				
				if (map.getFloorTile(x, y - 1) == toFloorEntry) {
					return transition.getBottomTexture();
				}
				
				if (map.getFloorTile(x, y + 1) == toFloorEntry) {
					return transition.getTopTexture();
				}
				
				if (map.getFloorTile(x - 1, y - 1) == toFloorEntry) {
					return transition.getBottomRightCornerTexture();
				}
				
				if (map.getFloorTile(x - 1, y + 1) == toFloorEntry) {
					return transition.getTopRightCornerTexture();
				}
				
				if (map.getFloorTile(x + 1, y - 1) == toFloorEntry) {
					return transition.getBottomLeftCornerTexture();
				}
				
				if (map.getFloorTile(x + 1, y + 1) == toFloorEntry) {
					return transition.getTopLeftCornerTexture();
				}
			}
		}
		
		if (imageVariations != null && imageVariations.length == 1) {
			if ((x + y) % 2 == 0)
				return imageTexture;
			else
				return imageVariationsTextures[0];
		}
		
		return imageTexture;
	}
	
	@Override
	public void init(Catalog catalog, WorldTiles gameTiles) {
		super.init(catalog, gameTiles);
		
		//It doesn't make any sense to have a floor with a different value of "solidDiagonal" than "solid"
		solidDiagonal = solid;
		
		if (imageVariations != null) {
			imageVariationsTextures = new AtlasRegion[imageVariations.length];
			for (int i = 0; i < imageVariationsTextures.length; i++)
				imageVariationsTextures[i] = gameTiles.getTileByName(imageVariations[i]);
		}
		
		if (transitions != null)
			for (int i = 0; i < transitions.length; i++)
				transitions[i].init(catalog, gameTiles);
		
		floorToEntityEntry = createFloorToEntityEntry(this);
		floorToEntityEntry.init(catalog, gameTiles);
	}
}
