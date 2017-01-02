package com.fangelo.craftinganddungeons.world.generator;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.fangelo.craftinganddungeons.catalog.Catalog;
import com.fangelo.craftinganddungeons.savegame.SaveSlotData;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldBounds;
import com.fangelo.craftinganddungeons.world.WorldCamera;
import com.fangelo.craftinganddungeons.world.generator.configuration.WorldGeneratorConfiguration;
import com.fangelo.craftinganddungeons.world.map.WorldMap;

public abstract class WorldGenerator {
	
	//Accessed from multiple threads, needs synchronized methods (getMapFromPool / addMapToPool)
	private final Pool<WorldMap> mapsPool = Pools.get(WorldMap.class);
	
	//Accessed only by main thread in syncFromWorld()
	//We keep maps ordered by Y, from top to down (ascending Y values), so when the maps
	//are drawn they are drawn fully from top to down.
	private final Array<WorldMap> activeMaps = new Array<WorldMap>(true, 16, WorldMap.class);
	
	//Accessed from multiple threads, but is thread safe, can be used directly
	private final PriorityBlockingQueue<WorldGeneratorTask> tasks = new PriorityBlockingQueue<WorldGeneratorTask>();
	
	//Fixed chunk size..
	static public final int MAP_SIZE_TILE = 128;
	
	protected World world;
	protected Catalog catalog;
	protected GridPoint2 startingPosition; //Must be initialized
	protected String startingMapId = ""; //Must be initialized
	protected WorldBounds worldBounds; //Must be initialized
	protected WorldGeneratorParameters parameters;
	protected WorldGeneratorConfiguration configuration;
	protected boolean initialized;
	
	protected RandomXS128 random = new RandomXS128();
	
	private SaveSlotData saveSlotData;
	
	public final void init(World world, WorldGeneratorParameters parameters) {
		
		this.world = world;
		this.catalog = world.getCatalog();
		this.parameters = parameters;
		this.configuration = parameters.configuration;
		this.saveSlotData = parameters.slot;
				
		onInit();
		
		initRandomGenerators();
		
		initialized = true;
		
		startGenerationThread();
	}
	
	protected void onInit() {
		
	}
	
	protected void initRandomGenerators() {
		random.setSeed(configuration.seed);
	}
	
	public final GridPoint2 getStartingTilePosition() {
		return startingPosition;
	}
	
	public final String getStartingMapId() {
		return startingMapId;
	}
	
	public final WorldBounds getBounds() {
		return worldBounds;
	}
	
	public final WorldGeneratorParameters getParameters() {
		return parameters;
	}
	
	private void generateMap(WorldMap map) {
		
		if (!loadMapData(map)) {
			long randomSeed = (((long) map.getMapX()) << 32) | map.getMapY(); 
			random.setSeed(randomSeed + configuration.seed);
			onGenerateMap(map);
		}
		
		map.resetDirtyFlag();
		map.setReady();
	}
	
	private void clearAllMapData() {
		saveSlotData.clearSaveGame();
	}
	
	private boolean loadMapData(WorldMap map) {
		return saveSlotData.loadMapData(map);
	}
	
	private void saveMapData(WorldMap map) {
		saveSlotData.saveMapData(map);
	}
	
	protected abstract void onGenerateMap(WorldMap map);
	
	private Thread generationThread;
	private volatile boolean stopGenerationThread;
	
	private final void startGenerationThread() {
		generationThread = new Thread(new Runnable() {
			public void run() {
				
				while (!stopGenerationThread) {
					
					try {
						WorldGeneratorTask task = tasks.poll(50, TimeUnit.MILLISECONDS);
						
						if (task != null) {
							switch(task.taskType) {
							case WorldGeneratorTask.TASK_CREATE_MAP:
								generateMap(task.map);
								break;
								
							case WorldGeneratorTask.TASK_REMOVE_MAP:
								if (task.map.isDirty())
									saveMapData(task.map);
								returnMapToPool(task.map);
								break;
							}
						}
					}catch(InterruptedException ex) {
						//Do nothing
					}
				}
			}
		}
		);
		
		generationThread.start();
	}
	
	private final void stopGenerationThread() {
		
		if (generationThread != null) {
			stopGenerationThread = true;
			
			//Wait until generation thread finishes
			while (generationThread.isAlive()) {
				try {
					Thread.sleep(50);
				}catch(InterruptedException ex) {
				}
			}
			
			generationThread = null;
		}
	}
	
	private Rectangle viewBounds = new Rectangle();
	
	private Array<WorldMap> tmpVisibleMaps = new Array<WorldMap>(WorldMap.class);
	
	private synchronized WorldMap getMapFromPool() {
		return mapsPool.obtain();
	}
	
	private synchronized void returnMapToPool(WorldMap map) {
		mapsPool.free(map);
	}
	
	public final void syncFromWorld(World world, Array<WorldMap> activeMapsCopy) {
		
		WorldCamera camera = world.getCamera();
		WorldBounds bounds = world.getBounds();
		
		//First update active maps
		float width = camera.getViewportWidth() * camera.getZoom();
		float height = camera.getViewportHeight() * camera.getZoom();
		viewBounds.set((float) (camera.getX() - width / 2), (float) (camera.getY() - height / 2), width, height);
		
		String mapId = camera.getMapId();
		
		int col1 = Math.max(bounds.getMinTileX(), (int)(viewBounds.x));
		int col2 = Math.min(bounds.getMaxTileX(), (int)((viewBounds.x + viewBounds.width + 1)));

		int row1 = Math.max(bounds.getMinTileY(), (int)(viewBounds.y));
		int row2 = Math.min(bounds.getMaxTileY(), (int)((viewBounds.y + viewBounds.height + 1)));
		
		tmpVisibleMaps.clear();
		
		//Make sure that all the visible maps are generated, and discard non-visible maps
		//Increment bounds to pre-load near maps
		row1 -= WorldGenerator.MAP_SIZE_TILE;
		row2 += WorldGenerator.MAP_SIZE_TILE * 2;
		col1 -= WorldGenerator.MAP_SIZE_TILE;
		col2 += WorldGenerator.MAP_SIZE_TILE * 2;
		
		for (int y = row1; y <= row2; y += WorldGenerator.MAP_SIZE_TILE) {
			for (int x = col1; x <= col2; x += WorldGenerator.MAP_SIZE_TILE) {
				
				int mapX = x / MAP_SIZE_TILE;
				int mapY = y / MAP_SIZE_TILE;
				
				boolean found = false;
				
				for (int i = 0; i < activeMaps.size; i++) {
					if (activeMaps.get(i).getMapId().equals(mapId) && activeMaps.get(i).getMapX() == mapX && activeMaps.get(i).getMapY() == mapY) {
						tmpVisibleMaps.add(activeMaps.get(i));
						found = true;
						break;
					}
				}
				
				if (!found) {
					WorldMap newMap = getMapFromPool();
					newMap.init(world, MAP_SIZE_TILE, MAP_SIZE_TILE, mapX, mapY, mapId, mapX * MAP_SIZE_TILE, mapY * MAP_SIZE_TILE);
					
					for (int i = 0; i < activeMaps.size; i++)
						activeMaps.items[i].connectMap(newMap);
					
					//Add map to list of active maps, keeping sorted by Y, it will be fully initialized in the generation thread
					boolean inserted = false;
					for (int i = 0; i < activeMaps.size; i++) {
						if (activeMaps.items[i].getOffsetY() >= newMap.getOffsetY()) {
							inserted = true;
							activeMaps.insert(i, newMap);
							break;
						}
					}
					
					if (!inserted)
						activeMaps.add(newMap); //Map not inserted, add last
					
					//Enqueue "generate map" task
					tasks.add(WorldGeneratorTask.createMap(newMap, world.getPlayer()));
					
					tmpVisibleMaps.add(newMap);
				}
			}
		}
		
		for (int i = activeMaps.size - 1; i >= 0; i--) {
			WorldMap map = activeMaps.get(i);
			if (!tmpVisibleMaps.contains(map, true)) {
				
				if (map.isReady()) { //Only remove maps that have been fully initialized
					
					//Don't remove maps that belong to world (empty map id) if we are not on world, this speed ups
					//transitions that return to the default world, since those maps have not been unloaded
					if (mapId.length() > 0 && map.getMapId().length() == 0)
						continue;
					
					map.disconnectMap();
					
					//Create "destroy map" task
					tasks.add(WorldGeneratorTask.removeMap(map, world.getPlayer()));
					
					//Remove map from list of active maps, it will be returned to the pool in the generation thread
					activeMaps.removeIndex(i);
				}
			}
		}	
		
		activeMapsCopy.clear();
		activeMapsCopy.addAll(activeMaps);
	}
	
	public void waitTasksQueueEmpty() {
		while (!tasks.isEmpty()) {
			try {
				Thread.sleep(10);
			}catch(InterruptedException ex) {
			}
		}
	}
	
	public boolean isTasksQueueEmpty() {
		return tasks.isEmpty();
	}
	
	public void saveEverything() {
		waitTasksQueueEmpty();
		
		for (int i = 0; i < activeMaps.size; i++) {
			if (activeMaps.get(i).isDirty()) {
				saveMapData(activeMaps.get(i));
				activeMaps.get(i).resetDirtyFlag();
			}
		}
	}
	
	//Returns the id of the map that will be entered at the given position
	private WorldGeneratorTransitionInfo tmpTransitionInfo = new WorldGeneratorTransitionInfo();
	public final WorldGeneratorTransitionInfo getEnterMapId(String mapId, int x, int y) {
		if (onGetEnterMapId(mapId, x, y, tmpTransitionInfo))
			return tmpTransitionInfo;
		return null;
	}
	
	protected abstract boolean onGetEnterMapId(String mapId, int x, int y, WorldGeneratorTransitionInfo transitionInfo);
	
	//Returns the id of the map that will be exited-to from the given position
	public final WorldGeneratorTransitionInfo getExitMapId(String mapId, int x, int y) {
		if (onGetExitMapId(mapId, x, y, tmpTransitionInfo))
			return tmpTransitionInfo;
		return null;
	}
	
	protected abstract boolean onGetExitMapId(String mapId, int x, int y, WorldGeneratorTransitionInfo transitionInfo);
	
	public void clear() {
		stopGenerationThread();
		
		activeMaps.clear();
		tasks.clear();
	}
	
	public final void rebuild(long newSeed) {
		waitTasksQueueEmpty();
		for (int i = activeMaps.size - 1; i >= 0; i--) {
			WorldMap map = activeMaps.get(i);
			map.disconnectMap();
			activeMaps.removeIndex(i);
			mapsPool.free(map);
		}	
		clearAllMapData();
		
		configuration.seed = newSeed;
		saveSlotData.saveConfiguration(configuration);
		initRandomGenerators();
	}
}
