package com.fangelo.craftinganddungeons.catalog.entry;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.catalog.CatalogEntry;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class EntityEntry extends CatalogEntry {
	
	static public final String TOP_LEFT_SUFFIX = "-top-left";
	static public final String TOP_SUFFIX = "-top";
	static public final String TOP_RIGHT_SUFFIX = "-top-right";
	
	static public final String MIDDLE_LEFT_SUFFIX = "-left";
	static public final String MIDDLE_SUFFIX = "-middle";
	static public final String MIDDLE_RIGHT_SUFFIX = "-right";
	
	static public final String BOTTOM_LEFT_SUFFIX = "-bottom-left";
	static public final String BOTTOM_SUFFIX = "-bottom";
	static public final String BOTTOM_RIGHT_SUFFIX = "-bottom-right";
	
	static public final String TAG_DUNGEON_ENTRANCE = "dungeon-entrance";
	static public final String TAG_DUNGEON_EXIT = "dungeon-exit";
	
	protected int width = 1;
	protected int height = 1;
	protected String image;
	protected boolean solid = true;
	protected boolean solidDiagonal = true;
	protected String tag;
	protected AtlasRegion imageTexture;
	protected EntityExtraTile[] extraTiles;
	protected boolean overlay;
	protected int maxInventoryStack = 99;
	
	public final int getWidth() {
		return width;
	}
	
	public final int getHeight() {
		return height;
	}
	
	public final String getImage() {
		return image;
	}
	
	public final boolean isOverlay() {
		return overlay;
	}
	
	public final AtlasRegion getImageTexture() {
		return imageTexture;
	}
	
	public final boolean isSolid() {
		return solid;
	}
	
	public final boolean isSolidDiagonal() {
		return solidDiagonal;
	}
	
	public boolean isFloor() {
		return id.startsWith(FloorEntry.FLOOR_TO_ENTITY_PREFIX);
	}
	
	public String getFloorId() {
		return id.substring(FloorEntry.FLOOR_TO_ENTITY_PREFIX.length());
	}
	
	public final String getTag() {
		return tag;
	}
	
	public final EntityExtraTile[] getExtraTiles() {
		return extraTiles;
	}
	
	public final int getMaxInventoryStack() {
		return maxInventoryStack;
	}

	public void init(Catalog catalog, WorldTiles gameTiles) {
		super.init(catalog, gameTiles);
		
		//If the item is non-solid, then the diagonal is never solid, but the contrary can be true (solid
		//item with non-solid diagonal)
		if (!solid)
			solidDiagonal = false;
		
		if (image != null && image.length() > 0)
			imageTexture = gameTiles.getTileByName(image);
		
		if (extraTiles != null)
			for (int i = 0; i < extraTiles.length; i++)
				extraTiles[i].init(catalog, gameTiles);
	}
	
	static public EntityEntry createFloorToEntityEntry(FloorEntry floorEntry) {
		 EntityEntry floorToEntityEntry = new EntityEntry();
		 
		floorToEntityEntry.id = FloorEntry.FLOOR_TO_ENTITY_PREFIX + floorEntry.getId();
		floorToEntityEntry.image = floorEntry.getImage();
		floorToEntityEntry.tag = floorEntry.tag;
		 
		 return floorToEntityEntry;
	}
}
