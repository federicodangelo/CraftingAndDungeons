package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.PlatformAdapter;
import com.fangelo.craftinganddungeons.ui.Dialog;
import com.fangelo.craftinganddungeons.ui.DialogCloseListener;
import com.fangelo.craftinganddungeons.ui.DialogResult;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.dialog.ConfirmDialog;
import com.fangelo.craftinganddungeons.world.World;

public class SettingsScreen extends Screen {
	
	static public boolean showFps;
	static public boolean showToolLabels;
	static public boolean showScreenshotButton;
	static public boolean showTools;
	
	private Table settingsContainer;
	
	private Button rebuildWorldButton;
	private Button resetPlayerPositionButton;
	private Button exitWorldButton;
	
	private Button showFpsButton;
	private Button showToolLabelsButton;
	private Button showToolsButton;
	private Button showScreenshotButtonButton;
	
	private Button loginButton;
	private Button logoutButton;
	
	private Button closeButton;
	
	private World world;
	
	public SettingsScreen(final World world) {
		
		this.world = world;
		
		settingsContainer = new Table(skin).padLeft(30).padRight(30).padBottom(5).padTop(5);
		settingsContainer.setBackground("panel-brown");
		
		add(settingsContainer).center();
		
		settingsContainer.add("Settings").top().padBottom(20);
		settingsContainer.row();
				
		showFpsButton = new CheckBox("Show FPS / Stats", skin);
		showFpsButton.setChecked(showFps);
		showFpsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showFps = showFpsButton.isChecked();
			}
		});
		settingsContainer.add(showFpsButton).pad(5);
		
		settingsContainer.row();
		
		HorizontalGroup toolsGroup = new HorizontalGroup();
		toolsGroup.space(5);
	
		showToolsButton = new CheckBox("Show Tools", skin);
		showToolsButton.setChecked(showTools);
		showToolsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showTools = showToolsButton.isChecked();
			}
		});
		toolsGroup.addActor(showToolsButton);
		
		showToolLabelsButton = new CheckBox("Labels", skin);
		showToolLabelsButton.setChecked(showToolLabels);
		showToolLabelsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showToolLabels = showToolLabelsButton.isChecked();
			}
		});
		toolsGroup.addActor(showToolLabelsButton);
		
		settingsContainer.add(toolsGroup).pad(5);
				
		settingsContainer.row();
		
		showScreenshotButtonButton = new CheckBox("Show Screenshot Button", skin);
		showScreenshotButtonButton.setChecked(showScreenshotButton);
		showScreenshotButtonButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				showScreenshotButton = showScreenshotButtonButton.isChecked();
			}
		});
		settingsContainer.add(showScreenshotButtonButton).pad(5);
		
		settingsContainer.row();
		
		rebuildWorldButton = new TextButton("Rebuild World", skin);
		
		rebuildWorldButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.show(new ConfirmDialog("Rebuild World", "Rebuild World?\nAll changes will be lost.")).addListener(new DialogCloseListener() {
					@Override
					public void closed(DialogCloseEvent event, Dialog dialog) {
						if (event.result == DialogResult.Yes)
							world.rebuild();
					}
				});
			}
		});
		
		//This option is a little too dangerous..
		//settingsContainer.add(rebuildWorldButton).pad(5);
		
		settingsContainer.row();
		
		resetPlayerPositionButton = new TextButton("Reset Player Position", skin);
		
		resetPlayerPositionButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.show(new ConfirmDialog("Player Position", "Reset player position?" )).addListener(new DialogCloseListener() {
					@Override
					public void closed(DialogCloseEvent event, Dialog dialog) {
						if (event.result == DialogResult.Yes)
							world.resetPlayerPosition();
					}
				});
				
			}
		});
		
		settingsContainer.add(resetPlayerPositionButton).pad(5);
		
		settingsContainer.row();
		
		if (PlatformAdapter.getInstance().hasSocialNetwork()) {
			
			HorizontalGroup socialGroup = new HorizontalGroup();
			socialGroup.space(5);
			
			loginButton = new TextButton("Login", skin);
			logoutButton = new TextButton("Logout", skin);
			
			socialGroup.addActor(new Container<Actor>(loginButton).minWidth(100));
			socialGroup.addActor(new Container<Actor>(logoutButton).minWidth(100));
						
			loginButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					PlatformAdapter.getInstance().loginSocialNetwork();
				}
			});
			
			logoutButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					PlatformAdapter.getInstance().logoutSocialNetwork();
				}
			});
			
			settingsContainer.add(socialGroup).pad(5);
			
			settingsContainer.row();
		}		
		
		exitWorldButton = new TextButton("Exit World", skin);
		
		exitWorldButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//We already handle the logic when the back button is pressed while
				//the game is being played, so use that logic
				Screens.inGameHudScreen.onBackButtonPressed();
			}
		});
		
		settingsContainer.add(exitWorldButton).pad(5);
		
		settingsContainer.row();
				
		closeButton = new TextButton("Close", skin);
		
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackButtonPressed();
			}
		});
		
		settingsContainer.add(closeButton).padTop(20);
	}
	
	@Override
	public void onShow() {
		
		rebuildWorldButton.setVisible(world.isInitialized());
		resetPlayerPositionButton.setVisible(world.isInitialized());
		exitWorldButton.setVisible(world.isInitialized());
		
		super.onShow();
	}	
}
