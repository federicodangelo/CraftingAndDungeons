package com.fangelo.craftinganddungeons.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.fangelo.craftinganddungeons.ui.dialog.ConfirmDialog;

public abstract class Screen extends Table {
	
	protected Skin skin;
	
	public Screen() {
		setFillParent(true);
		skin = ScreenManager.getSkin();
		setSkin(skin);
	}
	
	public void onShow() {
		//Called when the screen is shown
	}
	
	public void onHide() {
		//Called when the screen is hidden
	}
	
	public void onResize(int width, int height) {
		//Called when the screen is resized
	}
	
	public void onBackButtonPressed() {
		if (ScreenManager.canPop()) {
			ScreenManager.pop();
		} else {
			ScreenManager.show(new ConfirmDialog("Exit", "Exit game?")).addListener(new DialogCloseListener() {
				@Override
				public void closed(DialogCloseEvent event, Dialog dialog) {
					if (event.result == DialogResult.Yes)
						Gdx.app.exit();
				}
			});
		}
	}
}
