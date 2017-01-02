package com.fangelo.craftinganddungeons.savegame;

import java.util.Calendar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.fangelo.craftinganddungeons.PlatformAdapter;
import com.fangelo.craftinganddungeons.world.entity.PlayerSaveInfo;
import com.fangelo.craftinganddungeons.world.generator.configuration.WorldGeneratorConfiguration;
import com.fangelo.craftinganddungeons.world.map.WorldMap;

public class SaveSlotData {
	
	static public final String SLOT_PREFIX = "slot-";
	static private final String CONFIGURATION_FILE_NAME = "configuration.txt";
	static private final String PLAYER_FILE_NAME = "player.txt";
	static private final String MAP_PREFIX = "map-";
	static private final String MAP_EXTENSION = "map";
	
	private int slot;
	private Calendar creationDate;
	private FileHandle slotFolder;
	private boolean hasData;
	
	public int getSlot() {
		return slot;
	}
	
	public Calendar getCreationDate() {
		return creationDate;
	}
	
	public boolean hasData() {
		return hasData;
	}
	
	public boolean isValid() {
		return hasData && getConfiguration() != null;
	}
	
	public SaveSlotData(int slot) {
		this.slot = slot;
		
		FileHandle saveGamesFolder = PlatformAdapter.getInstance().getSaveGameFolder();
		
		if (saveGamesFolder != null) {
			slotFolder = saveGamesFolder.child(SLOT_PREFIX + slot);
			
			if (!slotFolder.exists())
				slotFolder.mkdirs();
			
			if (!slotFolder.exists() || !slotFolder.isDirectory()) {
				Gdx.app.error("SLOT", "Missing slot folder " + SLOT_PREFIX + slot + " in savegame folder " + saveGamesFolder.path());
				slotFolder = null;
			}
			
		} else {
			Gdx.app.error("SLOT", "Missing save game folder");
			slotFolder = null;
		}
		
		if (slotFolder != null) {
			
			if (slotFolder.child(CONFIGURATION_FILE_NAME).exists()) {
				hasData = (getConfiguration() != null);
				if (hasData) {
					creationDate = Calendar.getInstance();
					creationDate.setTimeInMillis(slotFolder.child(CONFIGURATION_FILE_NAME).lastModified());
				}
			}
			
		} else {
			hasData = false;
		}
	}
	
	public void clearSaveGame() {
		try {
			if (slotFolder != null) {
				FileHandle[] maps = slotFolder.list(MAP_EXTENSION);
				
				for (int i = 0; i < maps.length; i++)
					if (!maps[i].isDirectory())
						maps[i].delete();
				
				FileHandle configurationFile = slotFolder.child(CONFIGURATION_FILE_NAME);
				if (configurationFile.exists())
					configurationFile.delete();
				
				FileHandle playerFile = slotFolder.child(PLAYER_FILE_NAME);
				if (playerFile.exists())
					playerFile.delete();
			}
			
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Clear All Map Data Failed", ex);
		}		
	}
	
	public boolean loadMapData(WorldMap map) {
		try {
			if (slotFolder != null) {
				
				FileHandle mapFile = slotFolder.child(MAP_PREFIX + map.getMapId() + "-" + map.getMapX() + "-" + map.getMapY() + "." + MAP_EXTENSION);
				
				if (!mapFile.exists())
					return false;
				
				return map.loadFromBytes(mapFile.read(4096));
			}
			
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Map loading failed", ex);
		}
		
		return false;
	}
	
	public void saveMapData(WorldMap map) {
		try {
			if (slotFolder == null)
				return;
			
			FileHandle mapFile = slotFolder.child(MAP_PREFIX + map.getMapId() + "-" + map.getMapX() + "-" + map.getMapY() + "." + MAP_EXTENSION);
			
			//saveToBytes() closes the stream
			map.saveToBytes(mapFile.write(false, 4096));
			
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Map saving failed", ex);
		}
	}
	
	public PlayerSaveInfo getPlayerInfo() {
		try {
			if (slotFolder != null) {
				FileHandle playerFile = slotFolder.child(PLAYER_FILE_NAME);
				
				if (playerFile.exists()) {
					Json jsonParser = PlayerSaveInfo.getJsonParser();
					
					PlayerSaveInfo playerSaveInfo = jsonParser.fromJson(PlayerSaveInfo.class, playerFile);
					
					return playerSaveInfo;
				}
			}
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Player info loading failed", ex);
		}
		
		return null;
	}
	
	public void savePlayerInfo(PlayerSaveInfo playerSaveInfo) {
		try {
			if (slotFolder == null)
				return;
			
			FileHandle playerFile = slotFolder.child(PLAYER_FILE_NAME);
			
			Json jsonParser = PlayerSaveInfo.getJsonParser();
			
			jsonParser.toJson(playerSaveInfo, playerFile);			
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Player info saving failed", ex);
		}
	}
	
	public WorldGeneratorConfiguration getConfiguration() {
		
		try {
			if (slotFolder != null) {
				
				FileHandle configurationFile = slotFolder.child(CONFIGURATION_FILE_NAME);
				
				if (configurationFile.exists()) {
					
					Json jsonParser = WorldGeneratorConfiguration.getJsonParser();
					
					WorldGeneratorConfiguration configuration = jsonParser.fromJson(WorldGeneratorConfiguration.class, configurationFile);
					
					return configuration;
				}
			}
			
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Error loading configuration file", ex);
		}
		
		return null;
	}
	
	public void saveConfiguration(WorldGeneratorConfiguration configuration) {
		try {
			if (slotFolder != null) {
				
				FileHandle configurationFile = slotFolder.child(CONFIGURATION_FILE_NAME);
				
				Json jsonParser = WorldGeneratorConfiguration.getJsonParser();
				
				jsonParser.toJson(configuration, configurationFile);
				
				creationDate = Calendar.getInstance();
				creationDate.setTimeInMillis(configurationFile.lastModified());
			}
			
		} catch (Exception ex) {
			Gdx.app.error("SLOT", "Error saving configuration file", ex);
		}
	}		
}
