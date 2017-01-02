package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.UIUtils;
import com.fangelo.craftinganddungeons.utils.ScreenshotManager;
import com.fangelo.craftinganddungeons.world.InputMode;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class ViewMapScreen extends Screen {
	
	//private World world;
	private WorldInputHandler worldInput;
	
	private Button screenshotButton;
	private Button closeButton;
	
	public ViewMapScreen(final WorldInputHandler worldInput, final World world) {
		//this.world = world;
		this.worldInput = worldInput;
		
		screenshotButton = UIUtils.createCustomImageButton("icon-cctv-camera");
		add(screenshotButton).expand().top().right().size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		row();
		
		screenshotButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenshotManager.saveScreenshot();
			}
		});
		
		closeButton = UIUtils.createCustomImageButton("icon-back");
		add(closeButton).expand().bottom().right().size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackButtonPressed();
			}
		});
	}
	
	@Override
	public void onShow() {
		worldInput.setMode(InputMode.SCROLL_MAP);
		
		screenshotButton.setVisible(SettingsScreen.showScreenshotButton);
	}
}
