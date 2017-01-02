package com.fangelo.craftinganddungeons.world.generator;

import com.badlogic.gdx.math.Vector2;
import com.fangelo.craftinganddungeons.world.entity.Player;
import com.fangelo.craftinganddungeons.world.map.WorldMap;

public class WorldGeneratorTask implements Comparable<WorldGeneratorTask> {
	static public final int TASK_CREATE_MAP = 0;
	static public final int TASK_REMOVE_MAP = 1;
	
	public int taskType;
	public WorldMap map;
	public float distanceToPlayer;
	
	private WorldGeneratorTask(int taskType, WorldMap map, float distanceToPlayer) {
		this.taskType = taskType;
		this.map = map;
		this.distanceToPlayer = distanceToPlayer;
	}
	
	static public WorldGeneratorTask createMap(WorldMap map, Player player) {
		return new WorldGeneratorTask(TASK_CREATE_MAP, map, getDistanceToPlayer(map, player));
	}

	static public WorldGeneratorTask removeMap(WorldMap map, Player player) {
		return new WorldGeneratorTask(TASK_REMOVE_MAP, map, getDistanceToPlayer(map, player));
	}
	
	static private float getDistanceToPlayer(WorldMap map, Player player) {
		return Vector2.dst2(map.getCenterX(), map.getCenterY(), player.getX(), player.getY());
	}

	@Override
	public int compareTo(WorldGeneratorTask other) {
		return Float.compare(distanceToPlayer, other.distanceToPlayer);
	}
}
