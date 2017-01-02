package com.fangelo.craftinganddungeons.catalog.entry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class FloorTransition {
	private String toFloorId;
	private String imagePrefix;
	
	private FloorEntry toFloorEntry;
	
	private AtlasRegion topLeftTexture;
	private AtlasRegion topTexture;
	private AtlasRegion topRightTexture;
	private AtlasRegion bottomLeftTexture;
	private AtlasRegion bottomTexture;
	private AtlasRegion bottomRightTexture;
	private AtlasRegion leftTexture;
	private AtlasRegion rightTexture;
	
	private AtlasRegion topLeftCornerTexture;
	private AtlasRegion topRightCornerTexture;
	private AtlasRegion bottomLeftCornerTexture;
	private AtlasRegion bottomRightCornerTexture;
	
	
	public String getToFloorId() {
		return toFloorId;
	}
	
	public FloorEntry getToFloorEntry() {
		return toFloorEntry;
	}
	
	public String getImagePrefix() {
		return imagePrefix;
	}
	
	public AtlasRegion getTopLeftTexture() {
		return topLeftTexture;
	}
	
	public AtlasRegion getTopTexture() {
		return topTexture;
	}
	
	public AtlasRegion getTopRightTexture() {
		return topRightTexture;
	}
	
	public AtlasRegion getBottomLeftTexture() {
		return bottomLeftTexture;
	}
	
	public AtlasRegion getBottomTexture() {
		return bottomTexture;
	}
	
	public AtlasRegion getBottomRightTexture() {
		return bottomRightTexture;
	}
	
	public AtlasRegion getLeftTexture() {
		return leftTexture;
	}
	
	public AtlasRegion getRightTexture() {
		return rightTexture;
	}
	
	public AtlasRegion getTopLeftCornerTexture() {
		return topLeftCornerTexture;
	}
	
	public AtlasRegion getTopRightCornerTexture() {
		return topRightCornerTexture;
	}
	
	public AtlasRegion getBottomLeftCornerTexture() {
		return bottomLeftCornerTexture;
	}
	
	public AtlasRegion getBottomRightCornerTexture() {
		return bottomRightCornerTexture;
	}	
	
	public void init(Catalog catalog, WorldTiles gameTiles) {
		
		toFloorEntry = catalog.getFloorById(toFloorId);
		
		topLeftTexture  = gameTiles.getTileByName(imagePrefix + "-top-left");
		topTexture  = gameTiles.getTileByName(imagePrefix + "-top");
		topRightTexture  = gameTiles.getTileByName(imagePrefix + "-top-right");
		
		bottomLeftTexture  = gameTiles.getTileByName(imagePrefix + "-bottom-left");
		bottomTexture  = gameTiles.getTileByName(imagePrefix + "-bottom");
		bottomRightTexture  = gameTiles.getTileByName(imagePrefix + "-bottom-right");
		
		leftTexture  = gameTiles.getTileByName(imagePrefix + "-left");
		rightTexture  = gameTiles.getTileByName(imagePrefix + "-right");
		
		topLeftCornerTexture  = gameTiles.getTileByName(imagePrefix + "-top-left-corner");
		topRightCornerTexture  = gameTiles.getTileByName(imagePrefix + "-top-right-corner");
		bottomLeftCornerTexture  = gameTiles.getTileByName(imagePrefix + "-bottom-left-corner");
		bottomRightCornerTexture  = gameTiles.getTileByName(imagePrefix + "-bottom-right-corner");
	}
}
