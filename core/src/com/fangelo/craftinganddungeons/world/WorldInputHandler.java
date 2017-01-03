package com.fangelo.craftinganddungeons.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.catalog.entry.FloorEntry;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.screen.Screens;
import com.fangelo.craftinganddungeons.world.entity.InventorySlot;
import com.fangelo.craftinganddungeons.world.entity.Player;

public class WorldInputHandler implements GestureListener, InputProcessor {
	
	static private final float MIN_ZOOM_VALUE = 0.125f;
	static private final float MAX_ZOOM_VALUE = 2.0f;
	
	private World world;
	private float movementSpeed = 12; //tiles per second
	private InputMode mode = InputMode.MOVE_PLAYER;
	private boolean forceCenterCameraOnPlayer;
		
	private InventorySlot inventorySlot;
	
	public World getGameWorld() {
		return world;
	}
	
	public WorldInputHandler(World gameWorld) {
		this.world = gameWorld;
	}
	
	public void setMode(InputMode mode) {
		this.mode = mode;
		forceCenterCameraOnPlayer = false;
	}
	
	public void centerCameraOnPlayer() {
		forceCenterCameraOnPlayer = true;
	}
	
	public void setSelectedInventorySlot(InventorySlot slot) {
		this.inventorySlot = slot;
	}
	
	private boolean acceptsInput() {
		
		if (!world.isInitialized())
			return false;
		
		if (ScreenManager.getActiveDialog() != null)
			return false;
		
		if (ScreenManager.getActiveScreen() != Screens.inGameHudScreen && ScreenManager.getActiveScreen() != Screens.viewMapScreen)
			return false;
		
		return true;
	}
	
	public void handleInput() {
		
		if (!acceptsInput())
			return;
		
		float deltaTime = Gdx.graphics.getDeltaTime();
		        
        if (mode == InputMode.SCROLL_MAP) {
        	
	        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
	            world.getCamera().translate(-movementSpeed * deltaTime, 0);
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
	        	world.getCamera().translate(movementSpeed * deltaTime, 0);
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
	        	world.getCamera().translate(0, movementSpeed * deltaTime);
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
	        	world.getCamera().translate(0, -movementSpeed * deltaTime);
	        }
        
        } else if (mode == InputMode.MOVE_PLAYER) {
        	
        	Player player = world.getPlayer();
        	
	        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
	        	player.walkTo(player.getX() - 1, player.getY());
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
	        	player.walkTo(player.getX() + 1, player.getY());
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
	        	player.walkTo(player.getX(), player.getY() + 1);
	        }
	        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
	        	player.walkTo(player.getX(), player.getY() - 1);
	        }
	        
	        //Camera position updated in updateCameraPosition()
        }
    }	
	
	public void updateCameraPosition() {
		
		if (mode == InputMode.MOVE_PLAYER || 
			mode == InputMode.PLACE_ENTITY || 
			mode == InputMode.PLACE_FLOOR ||
			mode == InputMode.REMOVE_ENTITY ||
			mode == InputMode.REMOVE_FLOOR ||
			forceCenterCameraOnPlayer) {
			
			float deltaTime = Gdx.graphics.getDeltaTime();
			
			Player player = world.getPlayer();
			
        	float playerX = player.getX() + player.getWidth() / 2.0f;
        	float playerY = player.getY() + player.getHeight() / 2.0f;
        	
        	float cameraX = world.getCamera().getX();
        	float cameraY = world.getCamera().getY();
        	
        	//Lerp towards player position at 2x player walking speed, unless the camera is too far away (2 seconds away), in which
        	//case we snap the camera right away
        	
        	if (Vector2.dst(playerX, playerY, cameraX, cameraY) > movementSpeed * 2.0f * 2) {
        		cameraX = playerX;
        		cameraY = playerY;
        	} else {
	        	cameraX = cameraX + MathUtils.clamp(playerX - cameraX, -movementSpeed * 2.0f * deltaTime, movementSpeed * 2.0f * deltaTime);
	        	cameraY = cameraY + MathUtils.clamp(playerY - cameraY, -movementSpeed * 2.0f * deltaTime, movementSpeed * 2.0f * deltaTime);
        	}
        	
        	world.getCamera().setPosition(cameraX, cameraY);
        	world.getCamera().setMapId(player.getMapId());
		}
	}
	
	private Vector3 tmpScreenCoords = new Vector3();
	private GridPoint2 tmpMapCoords = new GridPoint2();
	
	private GridPoint2 getMapCoordsFromScreenCoords(float x, float y) {
		tmpScreenCoords.set(x, y, 0);
		
		tmpScreenCoords = world.getCamera().unproject(tmpScreenCoords);
		
		tmpMapCoords.x = (int) tmpScreenCoords.x;
		tmpMapCoords.y = (int) tmpScreenCoords.y;
		
		return tmpMapCoords;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}


	@Override
	public boolean tap(float x, float y, int count, int button) {
		
		if (!acceptsInput())
			return false;
		
		GridPoint2 mapCoords = getMapCoordsFromScreenCoords(x, y);
		String mapId = world.getCamera().getMapId();
		
		if (world.isValidTile(mapCoords, mapId)) {
		
			boolean handled = true;
			
			switch(mode) {
				case MOVE_PLAYER:
					world.getPlayer().walkTo(World.getTileWorldPosition(mapCoords.x , mapCoords.y));
					break;
					
				case PLACE_FLOOR:
					if (world.getFloorTile(mapCoords.x, mapCoords.y, mapId) == world.getCatalog().getEmptyFloor() &&
						world.getEntityTile(mapCoords.x, mapCoords.y, mapId) == null) {
						
						if (inventorySlot != null && inventorySlot.getAmount() > 0) {
							
							EntityEntry entity = world.getCatalog().getEntityById(inventorySlot.getEntityId());
							
							if (entity != null && entity.isFloor()) {
								
								FloorEntry floor = world.getCatalog().getFloorById(entity.getFloorId());
								
								if (floor != null) {
									
									world.setFloorTile(mapCoords.x, mapCoords.y, mapId, floor);
									
									world.getPlayer().getInventory().removeFromSlot(inventorySlot, 1);
									
									Screens.inGameHudScreen.updateInventorySlotButtons();
								}
							} else {
								ScreenManager.showHint("Select a floor tile from the inventory first");
							}
						}
					} else {
						if (world.getFloorTile(mapCoords.x, mapCoords.y, mapId) != world.getCatalog().getEmptyFloor())
							ScreenManager.showHint("Remove existing floor tile before placing a new one");
						else
							ScreenManager.showHint("Can't put floor tile if there is an item on top");
					}
					break;
					
				case REMOVE_FLOOR:
					if (world.getFloorTile(mapCoords.x, mapCoords.y, mapId) != world.getCatalog().getEmptyFloor() &&
						world.getEntityTile(mapCoords.x, mapCoords.y, mapId) == null) {
						
						String entityId = world.getFloorTile(mapCoords.x, mapCoords.y, mapId).getEntityId();
						
						if (world.getPlayer().getInventory().add(entityId, 1) == 0) {
							
							world.setFloorTile(mapCoords.x, mapCoords.y, mapId, world.getCatalog().getEmptyFloor());
							
							Screens.inGameHudScreen.updateInventorySlotButtons();
						} else {
							ScreenManager.showHint("Inventory is FULL");
						}
						
					} else {
						if (world.getFloorTile(mapCoords.x, mapCoords.y, mapId) == world.getCatalog().getEmptyFloor())
							ScreenManager.showHint("There is no floor tile to remove");
						else
							ScreenManager.showHint("Can't remove floor tile if there is an item on top");
					}
					break;
					
				case PLACE_ENTITY:
					if (world.getEntityTile(mapCoords.x, mapCoords.y, mapId) == null) {
						
						if (inventorySlot != null && inventorySlot.getAmount() > 0) {
							
							EntityEntry entity = world.getCatalog().getEntityById(inventorySlot.getEntityId());
							
							if (entity != null && !entity.isFloor()) {
								
								world.setEntityTile(mapCoords.x, mapCoords.y, mapId, entity);
								
								world.getPlayer().getInventory().removeFromSlot(inventorySlot, 1);
								
								Screens.inGameHudScreen.updateInventorySlotButtons();
							} else {
								ScreenManager.showHint("Select an item from the inventory first");								
							}
						}
					} else {
						ScreenManager.showHint("Remove existing item before placing a new one");						
					}
					break;
					
				case REMOVE_ENTITY:
					if (world.getEntityTile(mapCoords.x, mapCoords.y, mapId) != null) {
						
						if (world.getPlayer().getInventory().add(world.getEntityTile(mapCoords.x, mapCoords.y, mapId).getId(), 1) == 0) {
							
							world.setEntityTile(mapCoords.x, mapCoords.y, mapId, null);
								
							Screens.inGameHudScreen.updateInventorySlotButtons();
						} else {
							ScreenManager.showHint("Inventory is FULL");
						}
					} else {
						ScreenManager.showHint("There is no item to remove");
					}
					break;
					
				default:
					handled = false;
					break;
			}	
			
			return handled;
		}
		
		return false;
	}


	@Override
	public boolean longPress(float x, float y) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}


	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	private boolean panWalking;
	private float panStartPlayerPositionX;
	private float panStartPlayerPositionY;
	private float panSumX;
	private float panSumY;
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		
		if (!acceptsInput())
			return false;
		
		if (mode == InputMode.PLACE_ENTITY || 
			mode == InputMode.PLACE_FLOOR ||
			mode == InputMode.REMOVE_ENTITY ||
			mode == InputMode.REMOVE_FLOOR ||
			mode == InputMode.MOVE_PLAYER) {
			
			if (!panWalking) {
				panStartPlayerPositionX = world.getPlayer().getX();
				panStartPlayerPositionY = world.getPlayer().getY();
				panSumX = panSumY = 0;
				panWalking = true;
			}
			
			float zoom = world.getCamera().getZoom();
			panSumX += deltaX * zoom / World.CAMERA_SCALE;
			panSumY += deltaY * zoom / World.CAMERA_SCALE;
						
			world.getPlayer().walkTo(
					panStartPlayerPositionX - panSumX,
					panStartPlayerPositionY - panSumY);
			
			return true;
			
		} else if (mode == InputMode.SCROLL_MAP) {
		
			float zoom = world.getCamera().getZoom();
			world.getCamera().translate(-deltaX * zoom / World.CAMERA_SCALE, -deltaY * zoom / World.CAMERA_SCALE);
			
			return true;
		}
		
		
		return false;
	}


	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		
		panWalking = false;
		
		if (!acceptsInput())
			return false;
		
		return false;
	}


	private float lastInitialDistance;
	private float startingZoom;
	
	@Override
	public boolean zoom(float initialDistance, float distance) {
		
		if (!acceptsInput())
			return false;
		
		if (lastInitialDistance != initialDistance) {
			lastInitialDistance = initialDistance;
			startingZoom = world.getCamera().getZoom();
		}
		
		world.getCamera().setZoom(MathUtils.clamp(startingZoom * (initialDistance / distance), MIN_ZOOM_VALUE, MAX_ZOOM_VALUE));
				
		return true;
	}


	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
			Vector2 pointer1, Vector2 pointer2) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}
	
	@Override
	public void pinchStop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keyDown(int keycode) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		if (!acceptsInput())
			return false;
		
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		
		if (!acceptsInput())
			return false;
		
		world.getCamera().setZoom(MathUtils.clamp(world.getCamera().getZoom() + amount * 0.1f, MIN_ZOOM_VALUE, MAX_ZOOM_VALUE));
				
		return true;
	}
}
