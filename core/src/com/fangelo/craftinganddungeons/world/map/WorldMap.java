package com.fangelo.craftinganddungeons.world.map;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DataInput;
import com.badlogic.gdx.utils.DataOutput;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.EntityExtraTile;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldCamera;

public class WorldMap implements Poolable {
	
	static private final int SERIALIZATION_VERSION = 6;
	
	//Used to draw entities that are still on screen, but their base is offscreen (tall trees)
	static private final int EXTRA_TILES_TO_DRAW = 2; 
	
	private World world;
	
	private boolean ready;
	private boolean dirty;
	private int width;
	private int height;
	private int offsetX;
	private int offsetY;
	private int mapX; //Map generator identifier
	private int mapY; //Map generator identifier
	private String mapId; //Map generator identifier
	private WorldMap mapDown;
	private WorldMap mapLeft;
	private WorldMap mapRight;
	private WorldMap mapUp;
	
	private FloorEntry[] floorEntries;
	private float[] floorColors;
	private EntityEntry[] entityEntries;
	private float[] entityColors;
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getOffsetX() {
		return offsetX;
	}
	
	public int getOffsetY() {
		return offsetY;
	}
	
	public int getCenterX() {
		return offsetX + width / 2;
	}
	
	public int getCenterY() {
		return offsetY + height / 2;
	}
	
	public int getMapX() {
		return mapX;
	}
	
	public int getMapY() {
		return mapY;
	}
	
	public String getMapId() {
		return mapId;
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public WorldMap getMapDown() {
		return mapDown;
	}
	
	public WorldMap getMapLeft() {
		return mapLeft;
	}
	
	public WorldMap getMapRight() {
		return mapRight;
	}
	
	public WorldMap getMapUp() {
		return mapUp;
	}
	
	public boolean isValidTile(int x, int y) {
		x -= offsetX;
		y -= offsetY;
		
		return x >= 0 && y >= 0 && x < width && y < height;
	}
	
	public boolean isValidTile(GridPoint2 coord) {
		return isValidTile(coord.x, coord.y);
	}
	
	public FloorEntry getFloorTile(int x, int y) {
		
		if (x < offsetX) {
			if (mapLeft != null)
				return mapLeft.getFloorTile(x, y);
			else
				return null;
		}
		
		if (y < offsetY) {
			if (mapUp != null)
				return mapUp.getFloorTile(x, y);
			else
				return null;
		}
		
		if (x >= offsetX + width) {
			if (mapRight != null)
				return mapRight.getFloorTile(x, y);
			else
				return null;
		}
		
		if (y >= offsetY + height) {
			if (mapDown != null)
				return mapDown.getFloorTile(x, y);
			else
				return null;
		}
		
		return floorEntries[(y - offsetY) * width + (x - offsetX) ];
	}
	
	public boolean isWalkableTile(int x, int y) {
		FloorEntry floor = getFloorTile(x, y);
		
		if (floor == null || floor.isSolid())
			return false;
		
		EntityEntry entity = getEntityTile(x, y);
		
		if (entity != null && entity.isSolid())
			return false;
		
		return true;
	}
	
	public void setFloorTile(int x, int y, FloorEntry floorEntry) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		floorEntries[y * width + x] = floorEntry;
		dirty = true;
	}
	
	public void fillFloor(FloorEntry floor) {
		Arrays.fill(floorEntries, floor);
		dirty = true;
	}
	
	public void setFloorColor(int x, int y, float color) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		floorColors[y * width + x] = color;
		dirty = true;
	}
	
	public float getFloorColor(int x, int y) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return NumberUtils.intToFloatColor(0xFFFFFFFF);
		return floorColors[y * width + x];
	}
	
	public EntityEntry getEntityTile(int x, int y) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return null;
		return entityEntries[y * width + x];
	}
	
	
	public void setEntityTile(int x, int y, EntityEntry entityEntry) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		entityEntries[y * width + x] = entityEntry;
		dirty = true;
	}
	
	public float getEntityColor(int x, int y) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return NumberUtils.intToFloatColor(0xFFFFFFFF);
		return entityColors[y * width + x];
	}
	
	public void setEntityColor(int x, int y, float color) {
		x -= offsetX;
		y -= offsetY;
		if (x < 0 || x >= width || y < 0 || y >= height) return;
		entityColors[y * width + x] = color;
		dirty = true;
	}
	
	public WorldMap() {
	}
	
	public void init(World world, int width, int height, int mapX, int mapY, String mapId, int offsetX, int offsetY) {
		
		this.world = world;
		this.width = width;
		this.height = height;
		this.mapX = mapX;
		this.mapY = mapY;
		this.mapId = mapId;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		
		if (floorEntries == null || width * height != floorEntries.length) {
			floorEntries = new FloorEntry[width * height];
			floorColors = new float[width * height];
			
			entityEntries = new EntityEntry[width * height];
			entityColors = new float[width * height];
			
			Arrays.fill(floorColors, NumberUtils.intToFloatColor(0xFFFFFFFF));
			Arrays.fill(entityColors, NumberUtils.intToFloatColor(0xFFFFFFFF));
		}
	}
	
	public void setReady() {
		ready = true;
	}
	
	static private Rectangle viewBounds = new Rectangle();
	
	static public void drawFloorMaps(Array<WorldMap> maps, WorldCamera camera) {
		
		//Setup batch
		float viewPortWidth = camera.getViewportWidth() * camera.getZoom() + EXTRA_TILES_TO_DRAW * 2;
		float viewPortHeight = camera.getViewportHeight() * camera.getZoom() + EXTRA_TILES_TO_DRAW * 2;
		float cameraPositionX = camera.getX() - EXTRA_TILES_TO_DRAW;
		float cameraPositionY = camera.getY() + EXTRA_TILES_TO_DRAW;
		String cameraMapId = camera.getMapId();
		
		//Draw floors
		camera.disableBlending(); //No blending on floor layer
		
		for (int i = 0; i < maps.size; i++) {
			WorldMap map = maps.get(i);
			
			if (!map.isReady() || !map.getMapId().equals(cameraMapId))
				continue;
			
			final int offsetX = map.getOffsetX();
			final int offsetY = map.getOffsetY();
			
			final float offsetXscreen = offsetX;
			final float offsetYscreen = offsetY;
			
			final int width = map.width;
			final int height = map.height;
			
			viewBounds.set(
					(float) (cameraPositionX - offsetXscreen - viewPortWidth / 2), 
					(float) (cameraPositionY - offsetYscreen - viewPortHeight / 2), 
					viewPortWidth, viewPortHeight);
	
			final int fromX = Math.max(0, (int)(viewBounds.x));
			final int toX = Math.min(width, (int)((viewBounds.x + viewBounds.width + 1)));
	
			final int fromY = Math.max(0, (int)(viewBounds.y));
			final int toY = Math.min(height, (int)((viewBounds.y + viewBounds.height + 1)));
			
			final FloorEntry[] floorEntries = map.floorEntries;
			final float[] floorColors = map.floorColors;
			
			int y = fromY + offsetY;
			int xStart = fromX + offsetX;
	
			//Draw floor
			for (int row = fromY; row < toY; row++) {
				int x = xStart;
				for (int col = fromX; col < toX; col++) {
					
					FloorEntry floor = floorEntries[row * width + col];
					
					if (floor != null) {
						camera.setColor(floorColors[row * width + col]);
						camera.draw(floor.getFloorTexture(col + offsetX, row + offsetY, map), x, y, 1, 1);
					}
					
					x++;
				}
				y++;
			}		
		}
		
		//Draw entities
		camera.enableBlending();
		
		for (int i = 0; i < maps.size; i++) {
			WorldMap map = maps.get(i);
			
			if (!map.isReady() || !map.getMapId().equals(cameraMapId))
				continue;
			
			final int offsetX = map.getOffsetX();
			final int offsetY = map.getOffsetY();
			
			final float offsetXscreen = offsetX;
			final float offsetYscreen = offsetY;
			
			final int width = map.width;
			final int height = map.height;
			
			viewBounds.set(
					(float) (cameraPositionX - offsetXscreen - viewPortWidth / 2), 
					(float) (cameraPositionY - offsetYscreen - viewPortHeight / 2), 
					viewPortWidth, viewPortHeight);
	
			final int fromX = Math.max(0, (int)(viewBounds.x));
			final int toX = Math.min(width, (int)((viewBounds.x + viewBounds.width + 1)));
	
			final int fromY = Math.max(0, (int)(viewBounds.y));
			final int toY = Math.min(height, (int)((viewBounds.y + viewBounds.height + 1)));
			
			final EntityEntry[] entityEntries = map.entityEntries;
			final float[] entityColors = map.entityColors;
	
			int y = fromY + offsetY;
			int xStart = fromX + offsetX;
		
			for (int row = fromY; row < toY; row++) {
				int x = xStart;
				for (int col = fromX; col < toX; col++) {
					
					EntityEntry entity = entityEntries[row * width + col];
					
					//Only draw non-overlay tiles
					if (entity != null && !entity.isOverlay()) {
						camera.setColor(entityColors[row * width + col]);
						if (entity.getImageTexture() != null)
							camera.draw(entity.getImageTexture(), x, y, 1, 1);
						EntityExtraTile[] extraTiles = entity.getExtraTiles();
						if (extraTiles != null) {
							for (int j = 0; j < extraTiles.length; j++) {
								if (!extraTiles[j].isOverlay())
									camera.draw(extraTiles[j].getImageTexture(), x + extraTiles[j].getOffsetX(), y + extraTiles[j].getOffsetY(), 1, 1);
							}
						}
					}
					
					x++;
				}
				y++;
			}		
		}
	}	
	
	static public void drawOverlayMaps(Array<WorldMap> maps, WorldCamera camera) {
		
		//Setup batch
		float viewPortWidth = camera.getViewportWidth() * camera.getZoom() + EXTRA_TILES_TO_DRAW * 2;
		float viewPortHeight = camera.getViewportHeight() * camera.getZoom() + EXTRA_TILES_TO_DRAW * 2;
		float cameraPositionX = camera.getX() - EXTRA_TILES_TO_DRAW;
		float cameraPositionY = camera.getY() + EXTRA_TILES_TO_DRAW;
		String cameraMapId = camera.getMapId();
		
		//Draw overlays
		camera.enableBlending();
		
		for (int i = 0; i < maps.size; i++) {
			WorldMap map = maps.get(i);
			
			if (!map.isReady() || !map.getMapId().equals(cameraMapId))
				continue;
			
			final int offsetX = map.getOffsetX();
			final int offsetY = map.getOffsetY();
			
			final float offsetXscreen = offsetX;
			final float offsetYscreen = offsetY;
			
			final int width = map.width;
			final int height = map.height;
			
			viewBounds.set(
					(float) (cameraPositionX - offsetXscreen - viewPortWidth / 2), 
					(float) (cameraPositionY - offsetYscreen - viewPortHeight / 2), 
					viewPortWidth, viewPortHeight);
	
			final int fromX = Math.max(0, (int)(viewBounds.x));
			final int toX = Math.min(width, (int)((viewBounds.x + viewBounds.width + 1)));
	
			final int fromY = Math.max(0, (int)(viewBounds.y));
			final int toY = Math.min(height, (int)((viewBounds.y + viewBounds.height + 1)));
			
			final EntityEntry[] entityEntries = map.entityEntries;
			final float[] entityColors = map.entityColors;
	
			int y = fromY + offsetY;
			int xStart = fromX + offsetX;
		
			for (int row = fromY; row < toY; row++) {
				int x = xStart;
				for (int col = fromX; col < toX; col++) {
					
					EntityEntry entity = entityEntries[row * width + col];
					
					//Only draw overlay tiles
					if (entity != null) {
						
						camera.setColor(entityColors[row * width + col]);
						if (entity.isOverlay() && entity.getImageTexture() != null)
							camera.draw(entity.getImageTexture(), x, y, 1, 1);
						
						EntityExtraTile[] extraTiles = entity.getExtraTiles();
						if (extraTiles != null) {
							for (int j = 0; j < extraTiles.length; j++) {
								if (entity.isOverlay() || extraTiles[j].isOverlay())
									camera.draw(extraTiles[j].getImageTexture(), x + extraTiles[j].getOffsetX(), y + extraTiles[j].getOffsetY(), 1, 1);
							}
						}
					}
					
					x++;
				}
				y++;
			}		
		}
	}	
	

	@Override
	public void reset() {
		//Reset poolable object
		ready = false;
		Arrays.fill(entityEntries, null);
		Arrays.fill(floorEntries, null);
		Arrays.fill(floorColors, NumberUtils.intToFloatColor(0xFFFFFFFF));
		Arrays.fill(entityColors, NumberUtils.intToFloatColor(0xFFFFFFFF));
	}	
	
	public void resetDirtyFlag() {
		dirty = false;
	}
	
	static ObjectSet<String> knownIdsSet = new ObjectSet<String>();
	static Array<String> knownIds = new Array<String>(String.class);
	
	public void saveToBytes(OutputStream outputBytes) {
		
		knownIdsSet.clear();
		knownIds.clear();
		
		DataOutput output = null;
		try {
			output = new DataOutput(outputBytes);
			
			output.writeInt(SERIALIZATION_VERSION, true);
			output.writeInt(width, true);
			output.writeInt(height, true);
			
			for (int i = 0; i < floorEntries.length; i++)
				if (floorEntries[i] != null)
					knownIdsSet.add(floorEntries[i].getId());
			
			for (int i = 0; i < entityEntries.length; i++)
				if (entityEntries[i] != null)
					knownIdsSet.add(entityEntries[i].getId());
			
			Iterator<String> idsSetIteator = knownIdsSet.iterator();
			knownIds.add(""); //null
			output.writeInt(knownIdsSet.size, true);
			while (idsSetIteator.hasNext()) {
				String id = idsSetIteator.next();
				knownIds.add(id);
				output.writeString(id);
			}			
			
			//Perform RLE (run length encoding)
			FloorEntry currentFloor = floorEntries[0];
			int currentFloorCount = 1;
			for (int i = 1; i < floorEntries.length; i++) {
				if (floorEntries[i] == currentFloor) {
					currentFloorCount++;
				} else {
					output.writeInt(currentFloorCount, true);
					if (currentFloor != null)
						output.writeInt(knownIds.indexOf(currentFloor.getId(), true), true);
					else
						output.writeInt(0, true);
					currentFloor = floorEntries[i];
					currentFloorCount = 1;
				}
			}
			output.writeInt(currentFloorCount, true);
			if (currentFloor != null)
				output.writeInt(knownIds.indexOf(currentFloor.getId(), true), true);
			else
				output.writeInt(0, true);
			
			float currentFloorColor = floorColors[0];
			int currentFloorColorCount = 1;
			for (int i = 1; i < floorColors.length; i++) {
				if (floorColors[i] == currentFloorColor) {
					currentFloorColorCount++;
				} else {
					output.writeInt(currentFloorColorCount, true);
					output.writeFloat(currentFloorColor);
					currentFloorColor = floorColors[i];
					currentFloorColorCount = 1;
				}
			}
			output.writeInt(currentFloorColorCount, true);	
			output.writeFloat(currentFloorColor);
			
			EntityEntry currentEntity = entityEntries[0];
			int currentEntityCount = 1;
			for (int i = 1; i < entityEntries.length; i++) {
				if (entityEntries[i] == currentEntity) {
					currentEntityCount++;
				} else {
					output.writeInt(currentEntityCount, true);
					if (currentEntity != null)
						output.writeInt(knownIds.indexOf(currentEntity.getId(), true), true);
					else
						output.writeInt(0, true);
					currentEntity = entityEntries[i];
					currentEntityCount = 1;
				}
			}
			output.writeInt(currentEntityCount, true);
			if (currentEntity != null)
				output.writeInt(knownIds.indexOf(currentEntity.getId(), true), true);
			else
				output.writeInt(0, true);
			
			float currentEntityColor = entityColors[0];
			int currentEntityColorCount = 1;
			for (int i = 1; i < entityColors.length; i++) {
				if (entityColors[i] == currentEntityColor) {
					currentEntityColorCount++;
				} else {
					output.writeInt(currentEntityColorCount, true);
					output.writeFloat(currentEntityColor);
					currentEntityColor = entityColors[i];
					currentEntityColorCount = 1;
				}
			}
			output.writeInt(currentEntityColorCount, true);	
			output.writeFloat(currentEntityColor);
			
			//We do this in the finally block
			//output.close();
			
		} catch(IOException ex) {
			Gdx.app.error("WORLDMAP", "Map saving failed", ex);
		}  finally {
			try {
				if (output != null)
					output.close();
				else
					outputBytes.close();
			} catch (IOException ex) {
				//Nothing to do..
			}
		} 
	}
	
	public boolean loadFromBytes(InputStream inputBytes) {
		
		knownIds.clear();
		DataInput input = null;
		try {
			input = new DataInput(inputBytes);
			
			int version = input.readInt(true);
			
			if (version != SERIALIZATION_VERSION) {
				input.close();
				return false;
			}
			
			int width = input.readInt(true);
			int height = input.readInt(true);
			
			if (width != this.width || height != this.height) {
				input.close();
				return false;
			}
			
			knownIds.add(""); //null
			int knownIdsCount = input.readInt(true);
			for (int i = 0; i < knownIdsCount; i++)
				knownIds.add(input.readString());
						
			for (int i = 0; i < floorEntries.length; i++) {
				int count = input.readInt(true);
				int index = input.readInt(true);
				
				if (index > 0) {
					FloorEntry floor = world.getCatalog().getFloorById(knownIds.items[index]);
					for (int j = 0; j < count; j++)
						floorEntries[i++] = floor;
				} else {
					for (int j = 0; j < count; j++)
						floorEntries[i++] = null;
				}
				i--; //offset modification done in RLE loop
			}
			
			for (int i = 0; i < floorColors.length; i++) {
				int count = input.readInt(true);
				float color = input.readFloat();
				for (int j = 0; j < count; j++)
					floorColors[i++] = color;
				i--; //offset modification done in RLE loop
			}
			
			for (int i = 0; i < entityEntries.length; i++) {
				int count = input.readInt(true);
				int index = input.readInt(true);
				
				if (index > 0) {
					EntityEntry entity = world.getCatalog().getEntityById(knownIds.items[index]);
					for (int j = 0; j < count; j++)
						entityEntries[i++] = entity;
				} else {
					for (int j = 0; j < count; j++)
						entityEntries[i++] = null;
				}
				i--; //offset modification done in RLE loop
			}
			
			for (int i = 0; i < entityColors.length; i++) {
				int count = input.readInt(true);
				float color = input.readFloat();
				for (int j = 0; j < count; j++)
					entityColors[i++] = color;
				i--; //offset modification done in RLE loop
			}
			
			//We do this in the finally block
			//inputBytes.close();
			
			return true;
		} catch(IOException ex) {
			Gdx.app.error("WORLDMAP", "Map loading failed", ex);
			return false;
		}  finally {
			try {
				if (input != null)
					input.close();
				else
					inputBytes.close();
			} catch (IOException ex) {
				//Nothing to do..
			}
		} 
	}
	
	public void connectMap(WorldMap otherMap) {
		
		if (!otherMap.mapId.equals(mapId))
			return;
		
		int dx = otherMap.mapX - mapX;
		int dy = otherMap.mapY - mapY;
				
		if (dx == 0) {
			if (dy == -1) {
				mapUp = otherMap;
				otherMap.mapDown = this;
			} else if (dy == 1) {
				mapDown = otherMap;
				otherMap.mapUp = this;
			}
		} else if (dy == 0) {
			if (dx == -1) {
				mapLeft = otherMap;
				otherMap.mapRight = this;
			} else if (dx == 1) {
				mapRight = otherMap;
				otherMap.mapLeft = this;
			}
		}
	}
	
	public void disconnectMap() {
		
		if (mapDown != null) {
			mapDown.mapUp = null;
			mapDown = null;
		}
		
		if (mapUp != null) {
			mapUp.mapDown = null;
			mapUp = null;
		}
		
		if (mapLeft != null) {
			mapLeft.mapRight = null;
			mapLeft = null;
		}
		
		if (mapRight != null) {
			mapRight.mapLeft = null;
			mapRight = null;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof WorldMap) {
			WorldMap other = (WorldMap) obj;
			return other.mapX == mapX && other.mapY == mapY;
		}
		
		return super.equals(obj);
	}
	

	public void fillFloorTiles9Slice(int topLeftX, int topLeftY, int width, int height, 
			String floorPrefix, boolean fillMiddle) {
		
		FloorEntry topLeft = world.getCatalog().getFloorById(floorPrefix + EntityEntry.TOP_LEFT_SUFFIX);
		FloorEntry top = world.getCatalog().getFloorById(floorPrefix + EntityEntry.TOP_SUFFIX);
		FloorEntry topRight = world.getCatalog().getFloorById(floorPrefix + EntityEntry.TOP_RIGHT_SUFFIX);
		
		FloorEntry left = world.getCatalog().getFloorById(floorPrefix + EntityEntry.MIDDLE_LEFT_SUFFIX);
		FloorEntry middle = fillMiddle ? world.getCatalog().getFloorById(floorPrefix + EntityEntry.MIDDLE_SUFFIX) : null;
		FloorEntry right = world.getCatalog().getFloorById(floorPrefix + EntityEntry.MIDDLE_RIGHT_SUFFIX);
		
		FloorEntry bottomLeft = world.getCatalog().getFloorById(floorPrefix + EntityEntry.BOTTOM_LEFT_SUFFIX);
		FloorEntry bottom = world.getCatalog().getFloorById(floorPrefix + EntityEntry.BOTTOM_SUFFIX);
		FloorEntry bottomRight = world.getCatalog().getFloorById(floorPrefix + EntityEntry.BOTTOM_RIGHT_SUFFIX);
		
		fillFloorTiles9Slice(topLeftX, topLeftY, width, height,
				topLeft, top, topRight,
				left, middle, right,
				bottomLeft, bottom, bottomRight);
	}
	
	public void fillFloorTiles9Slice(int topLeftX, int topLeftY, int width, int height, 
			FloorEntry topLeft, FloorEntry top, FloorEntry topRight,
			FloorEntry left, FloorEntry middle, FloorEntry right,
			FloorEntry bottomLeft, FloorEntry bottom, FloorEntry bottomRight) {
		
		//Top / bottom
		for (int x = topLeftX + 1; x < topLeftX + width - 1; x++) {
			floorEntries[topLeftY * this.width + x] = top; 
			floorEntries[(topLeftY + height - 1) * this.width + x] = bottom;
		}
		
		//Left / right
		for (int y = topLeftY + 1; y < topLeftY + height - 1; y++) {
			floorEntries[y * this.width + topLeftX] = left;
			floorEntries[y * this.width + topLeftX + width - 1] = right;
		}
		
		//Add corners
		floorEntries[topLeftY * this.width + topLeftX] = topLeft;
		floorEntries[topLeftY * this.width + topLeftX + width - 1] = topRight;
		floorEntries[(topLeftY + height - 1) * this.width + topLeftX + width - 1] = bottomRight;
		floorEntries[(topLeftY + height - 1) * this.width + topLeftX] = bottomLeft;
		
		//Middle
		if (middle != null) {
			for (int y = topLeftY + 1; y < topLeftY + height - 1; y++)
				for (int x = topLeftX + 1; x < topLeftX + width - 1; x++)
					floorEntries[y * this.width + x] = middle;
		}
	}
	
	public void fillFloorTiles(int topLeftX, int topLeftY, int width, int height, FloorEntry floor) {
		for (int y = topLeftY; y < topLeftY + height; y++) {
			int offset = y * this.width + topLeftX;
			for (int x = topLeftX; x < topLeftX + width; x++) {
				floorEntries[offset++] = floor;
			}
		}
	}	
	
	public void fillEntityTiles9Slice(int topLeftX, int topLeftY, int width, int height, 
			String entityPrefix, boolean fillMiddle) {

		EntityEntry topLeft = world.getCatalog().getEntityById(entityPrefix + EntityEntry.TOP_LEFT_SUFFIX);
		EntityEntry top = world.getCatalog().getEntityById(entityPrefix + EntityEntry.TOP_SUFFIX);
		EntityEntry topRight = world.getCatalog().getEntityById(entityPrefix + EntityEntry.TOP_RIGHT_SUFFIX);
		
		EntityEntry left = world.getCatalog().getEntityById(entityPrefix + EntityEntry.MIDDLE_LEFT_SUFFIX);
		EntityEntry middle = fillMiddle ? world.getCatalog().getEntityById(entityPrefix + EntityEntry.MIDDLE_SUFFIX) : null;
		EntityEntry right = world.getCatalog().getEntityById(entityPrefix + EntityEntry.MIDDLE_RIGHT_SUFFIX);
		
		EntityEntry bottomLeft = world.getCatalog().getEntityById(entityPrefix + EntityEntry.BOTTOM_LEFT_SUFFIX);
		EntityEntry bottom = world.getCatalog().getEntityById(entityPrefix + EntityEntry.BOTTOM_SUFFIX);
		EntityEntry bottomRight = world.getCatalog().getEntityById(entityPrefix + EntityEntry.BOTTOM_RIGHT_SUFFIX);
		
		fillEntityTiles9Slice(topLeftX, topLeftY, width, height,
				topLeft, top, topRight,
				left, middle, right,
				bottomLeft, bottom, bottomRight);
	}
	
	public void fillEntityTiles9Slice(int topLeftX, int topLeftY, int width, int height, 
			EntityEntry topLeft, EntityEntry top, EntityEntry topRight,
			EntityEntry left, EntityEntry middle, EntityEntry right,
			EntityEntry bottomLeft, EntityEntry bottom, EntityEntry bottomRight) {
		
		//Top / bottom
		for (int x = topLeftX + 1; x < topLeftX + width - 1; x++) {
			entityEntries[topLeftY * this.width + x] = top; 
			entityEntries[(topLeftY + height - 1) * this.width + x] = bottom;
		}
		
		//Left / right
		for (int y = topLeftY + 1; y < topLeftY + height - 1; y++) {
			entityEntries[y * this.width + topLeftX] = left;
			entityEntries[y * this.width + topLeftX + width - 1] = right;
		}
		
		//Add corners
		entityEntries[topLeftY * this.width + topLeftX] = topLeft;
		entityEntries[topLeftY * this.width + topLeftX + width - 1] = topRight;
		entityEntries[(topLeftY + height - 1) * this.width + topLeftX + width - 1] = bottomRight;
		entityEntries[(topLeftY + height - 1) * this.width + topLeftX] = bottomLeft;
		
		//Middle
		if (middle != null) {
			for (int y = topLeftY + 1; y < topLeftY + height - 1; y++)
				for (int x = topLeftX + 1; x < topLeftX + width - 1; x++)
					entityEntries[y * this.width + x] = middle;
		}
	}	

	public void fillEntityTiles(int topLeftX, int topLeftY, int width, int height, EntityEntry entity) {
		for (int y = topLeftY; y < topLeftY + height; y++) {
			int offset = y * this.width + topLeftX;
			for (int x = topLeftX; x < topLeftX + width; x++) {
				entityEntries[offset++] = entity;
			}
		}
	}	
}