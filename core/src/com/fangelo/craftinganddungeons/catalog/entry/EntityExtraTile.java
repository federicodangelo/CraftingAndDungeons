package com.fangelo.craftinganddungeons.catalog.entry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class EntityExtraTile {
	protected String image;
	protected int offsetX;
	protected int offsetY;
	protected AtlasRegion imageTexture;
	protected boolean overlay;
	
	public String getImage() {
		return image;
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
	
	public boolean isOverlay() {
		return overlay;
	}
	
	public AtlasRegion getImageTexture() {
		return imageTexture;
	}
	
	public void init(Catalog catalog, WorldTiles gameTiles) {
		imageTexture = gameTiles.getTileByName(image);
	}
}
