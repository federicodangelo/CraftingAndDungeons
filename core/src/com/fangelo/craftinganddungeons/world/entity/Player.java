package com.fangelo.craftinganddungeons.world.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.world.pathfinder.Path;

public class Player extends Entity {

	static private final Pool<Path> pathsPool = Pools.get(Path.class);
	
	private Inventory inventory = new Inventory();
	private boolean walking;
	private float walkToX;
	private float walkToY;
	private float walkSpeed = 12; //tiles per second
	private Path walkingPath;
	private int walkingPathIndex;
	
	public float getWalkSpeed() {
		return walkSpeed;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public void walkTo(Vector2 vec) {
		walkTo(vec.x, vec.y);
	}
	
	public boolean isIdle() {
		return !walking;
	}
	
	public void walkTo(float x, float y) {
		
		Path newPath = getWorld().getPathfinder().findPathTo((int) this.getX(), (int) this.getY(), (int) x, (int) y, getMapId(), pathsPool.obtain(), 0, null, false);
		
		if (newPath.size() > 0) {
			
			//Stop any existing walk
			stopWalking();
			
			walkingPath = newPath;
			
			walking = true;
			
			walkToX = walkingPath.get(0).x;
			walkToY = walkingPath.get(0).y;
			
			walkingPathIndex = 1;
		} else {
			pathsPool.free(newPath);
		}
	}
	
	public void stopWalking() {
		if (walkingPath != null) {
			pathsPool.free(walkingPath);
			walkingPath = null;
		}
		walking = false;
	}
	
	public Player(TextureRegion texture) {
		super(texture);
	}
	
	@Override
	protected void onAddedToWorld() {
		inventory.setCatalog(getWorld().getCatalog());
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		float maxDistance = walkSpeed * deltaTime;
		
		int maxSteps = 8; //Just in case of float precision issues..
		while (walking && maxDistance > 0 && maxSteps-- > 0) {
			float x = getX();
			float y = getY();
			
			float dx = walkToX - x;
			float dy = walkToY - y;
			
			if (dx != 0 || dy != 0) {
				
				float len = Vector2.len(dx, dy);
				
				if (len > maxDistance) {
					x = x + (dx / len) * maxDistance;
					y = y + (dy / len) * maxDistance;
					maxDistance = 0;
				} else {
					maxDistance -= len;
					x = walkToX;
					y = walkToY;
				}
				
				if (!moveTo(x, y, false)) //We already validate collisions while path-finding..
					if (x != getX() && y != getY())
						stopWalking();
			}
			
			if (walking && x == walkToX && y == walkToY) {
				
				if (walkingPathIndex == walkingPath.size()) {
					stopWalking();
				} else {
					walkToX = walkingPath.get(walkingPathIndex).x;
					walkToY = walkingPath.get(walkingPathIndex).y;
					walkingPathIndex++;
				}
			}
		}
	}
	
	public PlayerSaveInfo getSaveInfo() {
		PlayerSaveInfo saveInfo = new PlayerSaveInfo();
		
		saveInfo.x = getX();
		saveInfo.y = getY();
		saveInfo.mapId = getMapId();
		saveInfo.inventorySlots = inventory.getSlots();
		
		return saveInfo;
	}
	
	public void loadSaveInfo(PlayerSaveInfo info) {
		moveTo(info.x, info.y, false);
		setMapId(info.mapId);
		inventory.loadSlots(info.inventorySlots);
	}

	public void addDefaultInventory() {
		
		//Give default inventory to player
		for (int i = 0; i < getWorld().getCatalog().getEntities().size(); i++) {
			EntityEntry entityEntry = getWorld().getCatalog().getEntities().get(i);
			
			//Only give known tags..
			if (entityEntry.getTag().equals("plant") ||
				entityEntry.getTag().equals("rock") ||
				entityEntry.getTag().equals("tree")) {
				
				//Don't give empty floor tiles
				if (entityEntry.isFloor() && entityEntry.getFloorId() == getWorld().getCatalog().getEmptyFloor().getId())
					continue;
				
				//Don't give empty white floor tiles
				if (entityEntry.isFloor() && entityEntry.getFloorId().equals("floor-white"))
					continue;
				
				inventory.add(entityEntry.getId(), entityEntry.getMaxInventoryStack());
			}
		}
	}
}
