package com.fangelo.craftinganddungeons;

import com.badlogic.gdx.files.FileHandle;

public class PlatformAdapter {
	
	static private PlatformAdapter instance;
	
	static public PlatformAdapter getInstance() {
		return instance;
	}
	
	static public void dispose() {
		instance = null;
	}
	
	public PlatformAdapter() {
		instance = this;
	}

	protected FileHandle screenshotFolder;
	protected FileHandle saveGameFolder;
	protected String version = "";
	
	public FileHandle getScreenshotFolder() {
		return screenshotFolder;
	}
	
	public FileHandle getSaveGameFolder() {
		return saveGameFolder;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void updateScreenshotFolder(final FileHandle screenshotFile) {
		
	}
	
	public boolean hasSocialNetwork() {
		return false;
	}
	
	public void loginSocialNetwork() {
		
	}

	public void logoutSocialNetwork() {
		
	}
}
