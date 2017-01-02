package com.fangelo.craftinganddungeons.world.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class PlayerSaveInfo {
	public float x;
	public float y;
	public String mapId;
	public Array<InventorySlot> inventorySlots;
	
	static public Json getJsonParser() {
		return new Json();
	}
}
