package com.fangelo.craftinganddungeons.savegame;

import java.util.ArrayList;

import com.badlogic.gdx.files.FileHandle;
import com.fangelo.craftinganddungeons.PlatformAdapter;


public class SaveGameManager {
	static public SaveSlotData getSaveSlotData(int slot) {
		
		SaveSlotData saveSlotData = new SaveSlotData(slot);
		
		return saveSlotData;
	}
	
	static public ArrayList<SaveSlotData> getSaveSlots() {
		
		ArrayList<SaveSlotData> slots = new ArrayList<SaveSlotData>();
		
		FileHandle saveGamesFolder = PlatformAdapter.getInstance().getSaveGameFolder();
		
		if (saveGamesFolder != null) {
			
			FileHandle[] files = saveGamesFolder.list();
			
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() && files[i].name().startsWith(SaveSlotData.SLOT_PREFIX)) {
					String fileName = files[i].name();
					String slotNumberStr = fileName.substring(SaveSlotData.SLOT_PREFIX.length());
					try {
						int slotNumber = Integer.parseInt(slotNumberStr);
						
						SaveSlotData saveSlotData = new SaveSlotData(slotNumber);
						
						if (saveSlotData.hasData())
							slots.add(saveSlotData);
						
					} catch(NumberFormatException ex) {
						//Do nothing
					}
				}
			}
		}
		
		return slots;
	}
}
