package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.files.FileHandle;
import com.fangelo.craftinganddungeons.PlatformAdapter;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.ShlObj;

public class DesktopPlatformAdapter extends PlatformAdapter {
	
	static private final String SCREENSHOTS_FOLDER = "Crafting And Dungeons Screenshots";
	static private final String SAVEGAME_FOLDER = "Crafting And Dungeons Saves";
	
	static private String[] knownDefaultPicturesFolders = new String[] {
		"Pictures",
		"My Pictures",
		"My Documents/My Pictures",
		"My Documents"
	};
	
	public DesktopPlatformAdapter() {
    	
		//We need to do this because Gdx.files isn't initialized yet...
		LwjglFiles desktopFilesAdapter = new LwjglFiles();
		
		//Init screenshots folder
		if (Platform.isWindows()) {
			try {
				String myPicturesFolder = Shell32Util.getFolderPath(ShlObj.CSIDL_MYPICTURES);
				
				if (myPicturesFolder != null && myPicturesFolder.length() > 0) {
					screenshotFolder = new FileHandle(myPicturesFolder);
					
					if (!screenshotFolder.exists() || !screenshotFolder.isDirectory())
						screenshotFolder = null;
				}
			}
			catch(Exception ex) {
				System.out.println("Exceptio while trying to retrieve 'My Pictures' folder location: " + ex.getMessage());
			}
			
			if (screenshotFolder == null) {
				//Try to find any of the known screenshot folders
				for (int i = 0; i < knownDefaultPicturesFolders.length; i++) {
					String folder = knownDefaultPicturesFolders[i];
					if (desktopFilesAdapter.external(folder).exists() && desktopFilesAdapter.external(folder).isDirectory()) {
						screenshotFolder = desktopFilesAdapter.external(folder);
						break;
					}
				}
			}
			
			if (screenshotFolder == null)
				screenshotFolder = new FileHandle(desktopFilesAdapter.getExternalStoragePath());
			
		} else {
			//Mac or linux
			screenshotFolder = new FileHandle(desktopFilesAdapter.getExternalStoragePath());
		}
		
		if (screenshotFolder != null && screenshotFolder.exists() && screenshotFolder.isDirectory()) {
			
			screenshotFolder = screenshotFolder.child(SCREENSHOTS_FOLDER);
			if (!screenshotFolder.exists()) {
				screenshotFolder.mkdirs();
				if (!screenshotFolder.exists() || !screenshotFolder.isDirectory())
					screenshotFolder = null;
			}
			
		} else {
			screenshotFolder = null;
		}
		
		//Init save games folder
		if (Platform.getOSType() == Platform.WINDOWS) {
			try {
				String myDocumentFolder = Shell32Util.getFolderPath(ShlObj.CSIDL_MYDOCUMENTS);
				
				if (myDocumentFolder != null && myDocumentFolder.length() > 0) {
					saveGameFolder = new FileHandle(myDocumentFolder);
					
					if (!saveGameFolder.exists() || !saveGameFolder.isDirectory())
						saveGameFolder = null;
				}
			}
			catch(Exception ex) {
				System.out.println("Exceptio while trying to retrieve 'My Pictures' folder location: " + ex.getMessage());
			}
			
			if (saveGameFolder == null)
				saveGameFolder = new FileHandle(desktopFilesAdapter.getExternalStoragePath());
		} else {
			//Mac or linux
			saveGameFolder = new FileHandle(desktopFilesAdapter.getExternalStoragePath());
		}
		
		if (saveGameFolder != null && saveGameFolder.exists() && saveGameFolder.isDirectory()) {
			
			saveGameFolder = saveGameFolder.child(SAVEGAME_FOLDER);
			if (!saveGameFolder.exists()) {
				saveGameFolder.mkdirs();
				if (!saveGameFolder.exists() || !saveGameFolder.isDirectory())
					saveGameFolder = null;
			}
			
		} else {
			saveGameFolder = null;
		}
		
	}

}

