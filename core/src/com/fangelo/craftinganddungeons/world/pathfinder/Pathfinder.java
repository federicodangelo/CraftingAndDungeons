package com.fangelo.craftinganddungeons.world.pathfinder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.entity.Entity;
import com.fangelo.craftinganddungeons.world.generator.WorldGenerator;

public class Pathfinder {

	static private boolean LOG_TIME = false; 
	
	static private final float RootSquareTwo = (float) Math.sqrt(2);
	
    private int openSetCount;

    static private PathfinderPriorityQueue scorePlusHeuristicFromStartQueue = new PathfinderPriorityQueue((WorldGenerator.MAP_SIZE_TILE * WorldGenerator.MAP_SIZE_TILE) / 4);
    
    private final Pool<PathfinderCell> cellsPool = Pools.get(PathfinderCell.class, 2048);
    
    private World world;
    
    public Pathfinder(World world) {
    	this.world = world;
    }

    public Path findPathTo(
        int fromX,
        int fromY,
        int toX,
        int toY,
        String mapId,
        Path path, 
        int collisionMask, 
        Array<Entity> entitiesToIgnore,
        boolean ignoreDynamicEntities) {
    	
        long start = System.currentTimeMillis();

        path = findPathToReal(fromX, fromY, toX, toY, mapId, path, collisionMask, entitiesToIgnore, ignoreDynamicEntities);

        long end = System.currentTimeMillis();

        if (LOG_TIME && end - start >= 0.0f)
        {
            String entitiesToIgnoreStr = "";
            
            if (entitiesToIgnore != null) {
	            for (int i = 0; i < entitiesToIgnore.size; i++) {
	                if (entitiesToIgnoreStr.length() > 0)
	                    entitiesToIgnoreStr += ",";
	                entitiesToIgnoreStr += entitiesToIgnore.get(i).toString();
	            }
            }
            
            Gdx.app.log("PATHFINDER", 
            		String.format("From: %d,%d To: %d,%d Distance: %d Time: %dms Path len: %d Entities To Ignore: %s", 
            				fromX, fromY, 
            				toX, toY, 
            				Math.abs(toX - fromX) + Math.abs(toY - fromY), 
            				end - start, 
            				path.size(), 
            				entitiesToIgnoreStr)
            );
        }

        return path;
    }

    private Path findPathToReal(
        int fromX, int fromY, 
        int toX, int toY,
        String mapId,
        Path path, 
        int collisionMask, 
        Array<Entity> entitiesToIgnore,
        boolean ignoreDynamicEntities) {
    	
        if (path == null)
            path = new Path();
        else
            path.clear();

        PathfinderCell fromCell = getCell(fromX, fromY, mapId);
        PathfinderCell toCell = getCell(toX, toY, mapId);

        if (fromCell == toCell) {
        	clear();
            return path;
        }

        if (!fromCell.isWalkable(collisionMask, entitiesToIgnore, true) ||
            !toCell.isWalkable(collisionMask, entitiesToIgnore, true)) {
        	clear();
            return path;
        }

        openSetCount = 0;

        scorePlusHeuristicFromStartQueue.clear();

        fromCell.inOpenSet = true;
        openSetCount = 1;
        fromCell.scoreFromStart = fromCell.costPathfinder;
        scorePlusHeuristicFromStartQueue.Enqueue(fromCell, (fromCell.scoreFromStart + heuristic(fromCell, toCell)));

    	float maxCost = (Math.abs(toX - fromX) + Math.abs(toY - fromY)) * 1.5f;
    	if (maxCost < 20)
    		maxCost = 20;
        
        while(openSetCount > 0) {
            PathfinderCell current = scorePlusHeuristicFromStartQueue.dequeue();

            if (current == toCell) {
                //Target reached! reconstruct path
                path = reconstructPath(toCell, fromCell, path);
                clear();
                return path;
            }

            current.inOpenSet = false;
            openSetCount--;

            current.inClosedSet = true;

            for (int i = 0; i < current.getNeighborsCount(); i++) {
            	
                PathfinderCell neighbor = current.getNeighbor(i);

                //Validate that we can walk to the neighbor
                if (!neighbor.isWalkable(collisionMask, entitiesToIgnore, ignoreDynamicEntities)) {
                    continue;
                }

                float deltaCost;

                if (neighbor.x != current.x && neighbor.y != current.y) {
                    //Diagonal path
                    deltaCost = RootSquareTwo;

                    //Validate that we can walk through the diagonals
                    if (world.isValidTile(current.x, neighbor.y, mapId)) {
                        PathfinderCell diag1 = getCell(current.x, neighbor.y, mapId);

                        if (!diag1.isWalkableDiagonal(collisionMask, entitiesToIgnore, ignoreDynamicEntities)) {
                            //There is a non-walkable cell in the path of the diagonal, skip
                            continue;
                        }
                    }

                    if (world.isValidTile(neighbor.x, current.y, mapId)) {
                        PathfinderCell diag2 = getCell(neighbor.x, current.y, mapId);

                        if (!diag2.isWalkableDiagonal(collisionMask, entitiesToIgnore, ignoreDynamicEntities)) {
                            //There is a non-walkable cell in the path of the diagonal, skip
                            continue;
                        }
                    }

                } else {
                    //straight path
                    deltaCost = 1.0f;
                }

                float neighborScoreFromStart = current.scoreFromStart + deltaCost + neighbor.costPathfinder;

                if (neighbor.inClosedSet)
                    if (neighborScoreFromStart >= neighbor.scoreFromStart)
                        continue;

                if (!neighbor.inOpenSet || neighborScoreFromStart < neighbor.scoreFromStart) {
                	
                    if (neighbor.inOpenSet) {
                        scorePlusHeuristicFromStartQueue.UpdatePriority(neighbor, (neighborScoreFromStart + heuristic(neighbor, toCell)));
                    } else {
                        //Skip the neighbor if we ran out of space or the max cost was reached
                        if (neighborScoreFromStart > maxCost || scorePlusHeuristicFromStartQueue.isFull()) 
                            continue;
                        scorePlusHeuristicFromStartQueue.Enqueue(neighbor, (neighborScoreFromStart + heuristic(neighbor, toCell)));
                    }

                    neighbor.cameFrom = current;
                    neighbor.scoreFromStart = neighborScoreFromStart;

                    if (!neighbor.inOpenSet) {
                        neighbor.inOpenSet = true;
                        openSetCount++;
                    }
                }
            }
        }
        
        clear();
        return path;
    }
    
    private LongMap<PathfinderCell> returnedCells = new LongMap<PathfinderCell>();
    
    private void clear() {
    	for (PathfinderCell cell : returnedCells.values())
    		cellsPool.free(cell);
    	returnedCells.clear();
    }
    
    public PathfinderCell getCell(int x, int y, String mapId) {
    	
    	long key = (((long) x) << 32) | y;
    	
    	PathfinderCell cell = returnedCells.get(key);
    	
    	if (cell ==  null) {
    		cell = cellsPool.obtain();
    		cell.init(world, this, x, y, mapId);
    		returnedCells.put(key, cell);
		}
    	
    	return cell;
    }

    private Path reconstructPath(PathfinderCell toCell, PathfinderCell fromCell, Path path) {
    	PathfinderCell current = toCell;
    	
        while (current != fromCell) {
            path.add(current.x, current.y);
            current = current.cameFrom;
        }

        path.reverse();

        return path;
    }

    static private float heuristic(PathfinderCell p1, PathfinderCell p2) {
        return (float) (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y));
    }	
}
