package com.fangelo.craftinganddungeons.android;

import java.io.File;

import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaScannerConnection;
import android.os.Environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fangelo.craftinganddungeons.PlatformAdapter;

public class AndroidPlatformAdapter extends PlatformAdapter {
	
	static private final String SAVEGAME_FOLDER = "Crafting And Dungeons Saves";
	private AndroidLauncher launcher;
	
	public AndroidPlatformAdapter(AndroidLauncher launcher) {
		
		try {
			this.version =
					launcher.getPackageManager().getPackageInfo(launcher.getPackageName(), 0).versionName + " (code " +
					launcher.getPackageManager().getPackageInfo(launcher.getPackageName(), 0).versionCode + ")";
		} catch (NameNotFoundException ex) {
			this.version = "";
		}
		
		this.launcher = launcher;
		
		File picturesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (picturesPath != null)
			screenshotFolder = new FileHandle(picturesPath);
		
		File externalPath = Environment.getExternalStorageDirectory();
		if (externalPath != null)
			saveGameFolder = new FileHandle(externalPath);
		
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
	
	@Override
	public void updateScreenshotFolder(final FileHandle screenshotFile) {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				MediaScannerConnection.scanFile(launcher.getApplicationContext(),
		                new String[] { screenshotFile.file().toString() }, 
		                null, 
		                null);
		            		
			}
		});
	}
	
	@Override
	public boolean hasSocialNetwork() {
		return true;
	}
	
	@Override
	public void loginSocialNetwork() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				launcher.loginSocialNetwork();
			}
		});
	}

	@Override
	public void logoutSocialNetwork() {
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				launcher.logoutSocialNetwork();
			}
		});
	}
}
