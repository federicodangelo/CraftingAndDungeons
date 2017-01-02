package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.ScreenManager;

public class MainMenuScreen extends Screen {
	
	private Button settingsButton;
	private Button playButton;
	private Button aboutButton;
	
	public MainMenuScreen() {
		setBackground("panel-brown");
		
		add("Crafting and Dungeons!!").padBottom(20);
		row();		
		
		playButton = new TextButton("Play", skin);
		
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.push(Screens.saveGameSelectorScreen);
			}
		});
		
		add(playButton).minWidth(150).pad(10);
		row();
		
		aboutButton = new TextButton("About..", skin);
		
		aboutButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.push(Screens.aboutScreen);
			}
		});
		
		add(aboutButton).minWidth(150).pad(10);
		row();
		
		settingsButton = new TextButton("Settings", skin);
		
		settingsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.push(Screens.settingsScreen);
			}
		});
		
		add(settingsButton).minWidth(150).pad(10);
	}
}
