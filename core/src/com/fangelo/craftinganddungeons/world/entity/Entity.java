package com.fangelo.craftinganddungeons.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldCamera;

public class Entity {
	private TextureRegion texture;
	
	private float x;
	private float y;
	private String mapId;
	
	private float width;
	private float height;
	
	private World world;
	
	public final float getX() {
		return x;
	}

	public final float getY() {
		return y;
	}
	
	public final String getMapId() {
		return mapId;
	}
	
	public final float getWidth() {
		return width;
	}
	
	public final float getHeight() {
		return height;
	}
		
	public final World getWorld() {
		return world;
	}
	
	public final void setWorld(World world) {
		this.world = world;
		if (world != null)
			onAddedToWorld();
		else
			onRemovedFromWorld();
	}
	
	protected void onAddedToWorld() {
		//Override
	}
	
	protected void onRemovedFromWorld() {
		//Override
	}
	
	public Entity (TextureRegion texture) {
		this.texture = texture;
		texture.flip(false, true);
		this.width = 1;
		this.height = 1;
	}
	
	public final void setMapId(String mapId) {
		this.mapId = mapId;
	}
	
	public final boolean moveTo(float newX, float newY, boolean checkCollision) {
		
		boolean collision = false;
		
		if (world != null && checkCollision) {
			
			newX = world.getBounds().clampWorldX(newX, getWidth());
			newY = world.getBounds().clampWorldY(newY, getHeight());
			
			float dx = newX - x;
			float dy = newY - y;
			
			if (dx > 0) {
				if (!world.isWalkableTile(World.getTileMapPosition(newX + getWidth(), y), mapId) || 
					!world.isWalkableTile(World.getTileMapPosition(newX + getWidth(), y + getHeight() - 0.1f), mapId)) {
					collision = true;
					x = World.getTileWorldPosition(World.getTileMapPosition(newX + getWidth(), y)).x - getWidth();
				} else {
					x = newX;
				}
			} else if (dx < 0) {
				if (!world.isWalkableTile(World.getTileMapPosition(newX, y), mapId) || 
					!world.isWalkableTile(World.getTileMapPosition(newX, y + getHeight() - 0.1f), mapId)) {
					collision = true;
					x = World.getTileWorldPosition(World.getTileMapPosition(newX, y)).x + 1;
				} else {
					x = newX;
				}
			}
			
			if (dy > 0) {
				if (!world.isWalkableTile(World.getTileMapPosition(x, newY + getHeight()), mapId) || 
					!world.isWalkableTile(World.getTileMapPosition(x + getWidth() - 0.1f, newY + getHeight()), mapId)) {
					collision = true;
					y = World.getTileWorldPosition(World.getTileMapPosition(x, newY + getHeight())).y - getHeight();
				} else {
					y = newY;
				}
			} else if (dy < 0) {
				if (!world.isWalkableTile(World.getTileMapPosition(x, newY), mapId) || 
					!world.isWalkableTile(World.getTileMapPosition(x + getWidth() - 0.1f, newY), mapId)) {
					collision = true;
					y = World.getTileWorldPosition(World.getTileMapPosition(x, newY)).y + 1;
				} else {
					y = newY;
				}
			}
		} else {
			x = newX;
			y = newY;
		}
		
		return !collision;
	}
	
	public void draw(WorldCamera camera) {
		camera.draw(texture, x, y + 1, 1, -1);
	}
	
	public void update(float deltaTime) {
		//Override
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
}
