package com.fangelo.craftinganddungeons.world.pathfinder;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.entity.Entity;


public class PathfinderCell implements Poolable {
	public int x;
	public int y;
	public String mapId;
	public float cost = 1.0f;
	public boolean walkable = true;
	public boolean walkableDiagonal = true;
	
	public float costPathfinder;
    public boolean inOpenSet;
    public boolean inClosedSet;
    public float scoreFromStart;
    public PathfinderCell cameFrom;
    public int priorityQueuePosition = -1;
    
    private PathfinderCell[] neighbors = new PathfinderCell[8];
    private boolean neighborsInit = false;
    private Pathfinder pathfinder;
    
    public boolean isWalkable(int collisionMask, Array<Entity> entitiesToIgnore, boolean ignoreDynamicEntities) {
    	//TODO: Implement remaining attributes
    	return walkable;
    }

    public boolean isWalkableDiagonal(int collisionMask, Array<Entity> entitiesToIgnore, boolean ignoreDynamicEntities) {
    	//TODO: Implement remaining attributes
    	return walkableDiagonal;
    }
    
	@Override
	public void reset() {
		cost = 1.0f;
		walkable = true;
		walkableDiagonal = true;
		costPathfinder = 0;
		inOpenSet = inClosedSet = false;
		scoreFromStart = 0.0f;
		cameFrom = null;
		priorityQueuePosition = -1;
		neighborsInit = false;
	}

	public void init(World world, Pathfinder pathfinder, int x, int y, String mapId) {
		this.x = x;
		this.y = y;
		this.mapId = mapId;
		this.pathfinder = pathfinder;
		this.walkable = world.isValidTile(x, y, mapId) && world.isWalkableTile(x, y, mapId);
		this.walkableDiagonal = world.isValidTile(x, y, mapId) && world.isWalkableTileDiagonal(x, y, mapId);
		//TODO: Init remaining properties
	}

	public int getNeighborsCount() {
		if (!neighborsInit)
			initNeighbors();
		
		return neighbors.length;
	}
	
	private void initNeighbors() {
		neighborsInit = true;
		neighbors[0] = pathfinder.getCell(x - 1, y - 1, mapId);
		neighbors[1] = pathfinder.getCell(x, y - 1, mapId);
		neighbors[2] = pathfinder.getCell(x + 1, y - 1, mapId);
		
		neighbors[3] = pathfinder.getCell(x - 1, y, mapId);
		//neighbors[0] = pathfinder.getCell(x, y); //same cell, can be skipped
		neighbors[4] = pathfinder.getCell(x + 1, y, mapId);
		
		neighbors[5] = pathfinder.getCell(x - 1, y + 1, mapId);
		neighbors[6] = pathfinder.getCell(x, y + 1, mapId);
		neighbors[7] = pathfinder.getCell(x + 1, y + 1, mapId);
	}

	public PathfinderCell getNeighbor(int i) {
		if (!neighborsInit)
			initNeighbors();
		
		return neighbors[i];
	}
}
