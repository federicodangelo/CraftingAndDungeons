package com.fangelo.craftinganddungeons.world;

import com.badlogic.gdx.math.MathUtils;

public class WorldBounds {
	
	private int maxX;
	private int maxY;
	
	private float maxXw;
	private float maxYw;
	
	public int getMinTileX() {
		return 0;
	}
	
	public int getMinTileY() {
		return 0;
	}
	
	public int getMaxTileX() {
		return maxX;
	}

	public int getMaxTileY() {
		return maxY;
	}
	
	public float getMinWorldX() {
		return 0;
	}
	
	public float getMinWorldY() {
		return 0;
	}

	public float getMaxWorldX() {
		return maxXw;
	}

	public float getMaxWorldY() {
		return maxYw;
	}
	
	public WorldBounds(int maxX, int maxY) {
		this.maxX = maxX;
		this.maxY = maxY;
		
		this.maxXw = maxX;
		this.maxYw = maxY;
	}
	
	public int clampTileX(int x) {
		return MathUtils.clamp(x, 0, maxX);
	}
	
	public int clampTileY(int y) {
		return MathUtils.clamp(y, 0, maxY);
	}
	
	public boolean containsTile(int x, int y) {
		return x >= 0 && x <= maxX && y >= 0 && y <= maxY;
	}

	public float clampWorldX(float x) {
		return MathUtils.clamp(x, 0, maxXw);
	}
	
	public float clampWorldY(float y) {
		return MathUtils.clamp(y, 0, maxYw);
	}
	
	public float clampWorldX(float x, float width) {
		return MathUtils.clamp(x, 0, maxXw + 1 - width);
	}
	
	public float clampWorldY(float y, float height) {
		return MathUtils.clamp(y, 0, maxYw + 1 - height);
	}
	
	public boolean containsWorld(float x, float y) {
		return x >= 0 && x <= maxXw && y >= 0 && y <= maxYw;
	}
}
