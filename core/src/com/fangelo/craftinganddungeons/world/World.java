package com.fangelo.craftinganddungeons.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Json;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.ui.Dialog;
import com.fangelo.craftinganddungeons.ui.DialogCloseListener;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.dialog.LoadingWorldDialog;
import com.fangelo.craftinganddungeons.ui.screen.Screens;
import com.fangelo.craftinganddungeons.world.entity.Entity;
import com.fangelo.craftinganddungeons.world.entity.Player;
import com.fangelo.craftinganddungeons.world.entity.PlayerSaveInfo;
import com.fangelo.craftinganddungeons.world.generator.WorldGenerator;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorFactory;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorParameters;
import com.fangelo.craftinganddungeons.world.map.WorldMap;
import com.fangelo.craftinganddungeons.world.pathfinder.Pathfinder;
import com.fangelo.craftinganddungeons.world.tile.WorldTiles;

public class World {
	
	static public final int CAMERA_SCALE = 16;
	
	private final WorldCamera camera;
	private final AtlasRegion playerTexture;
	private final Catalog catalog;
	private final WorldTiles tiles;
	
	private final DelayedRemovalArray<Entity> entities = new DelayedRemovalArray<Entity>();
	private final Array<WorldMap> activeMaps = new Array<WorldMap>(false, 16, WorldMap.class);
	
	private WorldGenerator worldGenerator;
	private Player player;
	private WorldBounds bounds;
	
	private Pathfinder pathfinder;
	
	private boolean loading;
	private boolean loadingComplete;
	private boolean initialized;
			
	public final WorldCamera getCamera() {
		return camera;
	}
	
	public final Player getPlayer() {
		return player;
	}
	
	public final Catalog getCatalog() {
		return catalog;
	}
	
	public final WorldTiles getTiles() {
		return tiles;
	}
	
	public final WorldBounds getBounds() {
		return bounds;
	}
	
	public final boolean isInitialized() {
		return initialized;
	}
	
	public final boolean isLoading() {
		return loading;
	}
	
	public Pathfinder getPathfinder() {
		return pathfinder;
	}
	
	public final WorldGenerator getWorldGenerator() {
		return worldGenerator;
	}
	
	public World() {
		
		this.tiles = new WorldTiles(4);
		this.catalog = new Json().fromJson(Catalog.class, Gdx.files.internal("data/catalog.txt"));
		this.catalog.init(tiles);
		
		camera = new WorldCamera();
		
		camera.resize(Gdx.graphics.getWidth() / CAMERA_SCALE, Gdx.graphics.getHeight() / CAMERA_SCALE);
		
		playerTexture = tiles.getTileByName("player");
		
		this.pathfinder = new Pathfinder(this);
	}
	
	public final void init(WorldGeneratorParameters parameters) {
	
		if (initialized) {
			Gdx.app.error("WORLD", "World already initialized");
			return;
		}
		
		worldGenerator = WorldGeneratorFactory.getWorldGenerator(parameters.configuration.generatorType);
		
		worldGenerator.init(this, parameters);
		
		bounds = worldGenerator.getBounds();
				
		player = new Player(new TextureRegion(playerTexture));
		
		PlayerSaveInfo playerSaveInfo = parameters.slot.getPlayerInfo();
		
		if (playerSaveInfo == null) {
			//New player!
			player.moveTo(
					worldGenerator.getStartingTilePosition().x, 
					worldGenerator.getStartingTilePosition().y,
					false);
			player.setMapId(worldGenerator.getStartingMapId());
		} else {
			//Existing player, initialize
			player.loadSaveInfo(playerSaveInfo);
		}
		
		camera.setPosition(player.getX(), player.getY());
		camera.setMapId(player.getMapId());
		
		addEntity(player);
		
		if (playerSaveInfo == null)
			player.addDefaultInventory();
		
		worldGenerator.syncFromWorld(this, activeMaps);
		
		//worldGenerator.waitTasksQueueEmpty();
		loading = true;
		loadingComplete = false;
		ScreenManager.showWithoutFadeIn(new LoadingWorldDialog(this)).addListener(new DialogCloseListener() {
			@Override
			public void closed(DialogCloseEvent event, Dialog dialog) {
				loadingComplete = true;
			}
		});
	}
	
	public final void updateLoading() {
		if (loading && loadingComplete) {
			loading = false;
			if (!initialized) {
				initialized = true;
				ScreenManager.show(Screens.inGameHudScreen);
			}
		}
	}
	
	public final void resize(int width, int height) {
	    camera.resize(width / CAMERA_SCALE, height / CAMERA_SCALE);
	}
	
	public final void addEntity(Entity entity) {
		entities.add(entity);
		entity.setWorld(this);
	}
	
	public final void removeEntity(Entity entity) {
		entities.removeValue(entity, true);
		entity.setWorld(null);
	}
	
	public final void update() {
		
		//Update entities
		float deltaTime = Gdx.graphics.getDeltaTime();
		
		entities.begin();
		try {
			for (int i = 0; i < entities.size; i++)
				entities.get(i).update(deltaTime);
		} finally {
			entities.end();
		}
		
		//Update active maps
        worldGenerator.syncFromWorld(this, activeMaps);
	}
	
	public void draw() {
		
        camera.update();
        
		//Draw everything
        camera.begin();
		
		//Draw floor maps
        WorldMap.drawFloorMaps(activeMaps, camera);
        
		//Draw dynamic entities
        camera.setColor(Color.WHITE);
		for (int i = 0; i < entities.size; i++)
			entities.get(i).draw(camera);
		
		//Draw overlay maps
        WorldMap.drawOverlayMaps(activeMaps, camera);
        
		camera.end();
	}
	
	static private Vector2 tmpVector2 = new Vector2();
	
	static public Vector2 getTileWorldPosition(GridPoint2 coord) {
		tmpVector2.set(coord.x, coord.y);
		return tmpVector2;
	}
	
	static public Vector2 getTileWorldPosition(int x, int y) {
		tmpVector2.set(x, y);
		return tmpVector2;
	}
	
	static private GridPoint2 tmpGridPoint2 = new GridPoint2();
	
	static public GridPoint2 getTileMapPosition(float x, float y) {
		tmpGridPoint2.set((int) x, (int) y);
		return tmpGridPoint2;
	}
	
	public boolean isValidTile(int x, int y, String mapId) {
		return bounds.containsTile(x, y);
	}
	
	public boolean isValidTile(GridPoint2 coord, String mapId) {
		return isValidTile(coord.x, coord.y, mapId);
	}
	
	private WorldMap getMapTile(int x, int y, String mapId) {
		for (int i = 0; i < activeMaps.size; i++) {
			if (activeMaps.items[i].getMapId().equals(mapId) && activeMaps.items[i].isValidTile(x, y)) {
				if (activeMaps.items[i].isReady())
					return activeMaps.items[i];
				else
					return null; //Map not fully initialized yet
			}
		}
		return null;
	}
	
	public FloorEntry getFloorTile(GridPoint2 coord, String mapId) {
		return getFloorTile(coord.x, coord.y, mapId);
	}
	
	public FloorEntry getFloorTile(int x, int y, String mapId) {
		WorldMap map = getMapTile(x, y, mapId);
		if (map != null)
			return map.getFloorTile(x, y);
		return null;
	}
	
	public void setFloorTile(int x, int y, String mapId, FloorEntry floor) {
		WorldMap map = getMapTile(x, y, mapId);
		if (map != null)
			map.setFloorTile(x, y, floor);
	}
	
	public boolean isWalkableTile(GridPoint2 coord, String mapId) {
		return isWalkableTile(coord.x, coord.y, mapId);
	}
	
	public boolean isWalkableTile(int x, int y, String mapId) {
		FloorEntry floor = getFloorTile(x, y, mapId);
		if (floor == null || floor.isSolid())
			return false;
		
		EntityEntry entity = getEntityTile(x, y, mapId);
		if (entity != null && entity.isSolid())
			return false;
		
		return true;
	}
	
	public boolean isWalkableTileDiagonal(GridPoint2 coord, String mapId) {
		return isWalkableTileDiagonal(coord.x, coord.y, mapId);
	}
	
	public boolean isWalkableTileDiagonal(int x, int y, String mapId) {
		FloorEntry floor = getFloorTile(x, y, mapId);
		if (floor == null || floor.isSolid())
			return false;
		
		EntityEntry entity = getEntityTile(x, y, mapId);
		if (entity != null && entity.isSolidDiagonal())
			return false;
		
		return true;
	}
	
	public EntityEntry getEntityTile(GridPoint2 coord, String mapId) {
		return getEntityTile(coord.x, coord.y, mapId);
	}
	
	public EntityEntry getEntityTile(int x, int y, String mapId) {
		WorldMap map = getMapTile(x, y, mapId); 
		if (map != null)
			return map.getEntityTile(x, y);
		return null;
	}
	
	public void setEntityTile(int x, int y, String mapId, EntityEntry entityEntry) {
		WorldMap map = getMapTile(x, y, mapId); 
		if (map != null)
			map.setEntityTile(x, y, entityEntry);
	}	

	public boolean raycastWorld(double x0f, double y0f, double x1f, double y1f, String mapId, RaycastHitInfo outHitInfo) {
		
		//Raycast with pixel precision (using ints)
		
		int x0 = (int) Math.round(x0f);
		int y0 = (int) Math.round(y0f);
		int x1 = (int) Math.round(x1f);
		int y1 = (int) Math.round(y1f);
					
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		
		int sx = x0 < x1 ? 1 : -1; 
		int sy = y0 < y1 ? 1 : -1; 
		
		int err = dx - dy;
		int e2;
		int currentX = (int) x0;
		int currentY = (int) y0;
		
		int lastNonCollisionX = currentX;
		int lastNonCollisionY = currentY;
		int distance = -1; //first loop is starting point, we need to ignore distance
		
		while(true) {

			int tileX = currentX;
			int tileY = currentY;
        	
        	if (!isWalkableTile(tileX, tileY, mapId)) {
        		if (outHitInfo != null) {
					outHitInfo.hitTileX = tileX;
					outHitInfo.hitTileY = tileY;
					outHitInfo.hitWorldX = lastNonCollisionX;
					outHitInfo.hitWorldY = lastNonCollisionY;
					outHitInfo.hitWorldDistance = (distance > 0 ? distance : 0);
				}
				return true;        		
        	} else {
        		lastNonCollisionX = currentX;
        		lastNonCollisionY = currentY;
        		distance++;
        	}
        	
			if(currentX == x1 && currentY == y1) {
				break;
			}
			
			e2 = 2 * err;
			if (e2 > -1 * dy) {
				err = err - dy;
				currentX = currentX + sx;
			}
			
			if (e2 < dx) {
				err = err + dx;
				currentY = currentY + sy;
			}
		}
		
		//No collision!
		return false;
	}	
	
	public boolean raycastTile(int x0, int y0, int x1, int y1, String mapId, RaycastHitInfo outHitInfo) {
		
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		
		int sx = x0 < x1 ? 1 : -1; 
		int sy = y0 < y1 ? 1 : -1; 
		
		int err = dx-dy;
		int e2;
		int currentX = x0;
		int currentY = y0;
		
		while(true) {

        	if (!isWalkableTile(currentX, currentX, mapId)) {
        		if (outHitInfo != null) {
					outHitInfo.hitTileX = currentX;
					outHitInfo.hitTileY = currentY;
					outHitInfo.hitWorldX = outHitInfo.hitTileX;
					outHitInfo.hitWorldY = outHitInfo.hitTileX;
				}
				return true;        		
        	}
        	
			if(currentX == x1 && currentY == y1) {
				break;
			}
			
			e2 = 2*err;
			if(e2 > -1 * dy) {
				err = err - dy;
				currentX = currentX + sx;
			}
			
			if(e2 < dx) {
				err = err + dx;
				currentY = currentY + sy;
			}
		}
                		
		//No collision!
		return false;
	}	

	public void clear() {
		if (!initialized) {
			Gdx.app.error("WORLD" , "World isn't initialized, can't clear()");
			return;
		}
		
		worldGenerator.clear();
		worldGenerator = null;
		
		activeMaps.clear();
		
		entities.clear();
		player = null;
		
		initialized = false;
	}
	
	public void saveEverything() {
		if (!initialized) {
			Gdx.app.error("WORLD" , "World isn't initialized, can't saveEverything()");
			return;
		}
		
		worldGenerator.getParameters().slot.savePlayerInfo(player.getSaveInfo());
		
		worldGenerator.saveEverything();
	}
	
	public void dispose() {
		if (initialized) {
			saveEverything();
			clear();
		}
		camera.dispose();
		tiles.dispose();
	}
	
	public void rebuild() {
		worldGenerator.waitTasksQueueEmpty();
		activeMaps.clear();
		
		resetPlayerPosition();
		
		worldGenerator.rebuild(MathUtils.random.nextLong());
		worldGenerator.syncFromWorld(this, activeMaps);
		worldGenerator.waitTasksQueueEmpty();
	}

	public void resetPlayerPosition() {
		player.stopWalking();
		
		player.moveTo(
				worldGenerator.getStartingTilePosition().x, 
				worldGenerator.getStartingTilePosition().y,
				false);
		player.setMapId(worldGenerator.getStartingMapId());
		
		camera.setPosition(player.getX(), player.getY());
		camera.setMapId(player.getMapId());
	}
	
	public void changePlayerMap(String newMapId, int newX, int newY) {
		
		if (!newMapId.equals(player.getMapId())) {
			player.setMapId(newMapId);
			player.moveTo(newX, newY, false);
			
			camera.setMapId(player.getMapId());
			camera.setPosition(player.getX(), player.getY());
			
			worldGenerator.syncFromWorld(this, activeMaps);
			
			loading = true;
			ScreenManager.showWithoutFadeIn(new LoadingWorldDialog(this)).addListener(new DialogCloseListener() {
				@Override
				public void closed(DialogCloseEvent event, Dialog dialog) {
					loadingComplete = true;
				}
			});
		}
	}
}
