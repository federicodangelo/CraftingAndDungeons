package com.fangelo.craftinganddungeons.ui.screen;

import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class Screens {
	
	static public InGameHudScreen inGameHudScreen;
	static public SettingsScreen settingsScreen;
	static public MainMenuScreen mainMenuScreen;
	static public SaveGameSelectorScreen saveGameSelectorScreen;
	static public AboutScreen aboutScreen;
	static public NewGameConfigurationScreen newGameConfigurationScreen;
	static public ViewMapScreen viewMapScreen;
	static public InventoryScreen inventoryScreen;
	
	static public void init(World world, WorldInputHandler worldInputHandler) {
		settingsScreen = new SettingsScreen(world);
		inGameHudScreen = new InGameHudScreen(worldInputHandler, world);
		saveGameSelectorScreen = new SaveGameSelectorScreen(world);
		mainMenuScreen = new MainMenuScreen();
		aboutScreen = new AboutScreen();
		newGameConfigurationScreen = new NewGameConfigurationScreen(world);
		viewMapScreen = new ViewMapScreen(worldInputHandler, world);
		inventoryScreen = new InventoryScreen(world);
	}
	
	static public void dispose() {
		settingsScreen = null;
		inGameHudScreen = null;
		saveGameSelectorScreen = null;
		mainMenuScreen = null;
		aboutScreen = null;
		newGameConfigurationScreen = null;
		viewMapScreen = null;
		inventoryScreen = null;
	}
}
