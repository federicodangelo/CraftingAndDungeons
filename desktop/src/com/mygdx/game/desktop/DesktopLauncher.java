package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fangelo.craftinganddungeons.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
		
		config.width = 1366; //landscape!
		config.height = 768;
		
		//config.width = 768; //portrait!
		//config.height = 1366;
		
		new LwjglApplication(new MyGdxGame(new DesktopPlatformAdapter()), config);
	}
}
