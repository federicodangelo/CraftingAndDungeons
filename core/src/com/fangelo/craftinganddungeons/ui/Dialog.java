package com.fangelo.craftinganddungeons.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;
import com.fangelo.craftinganddungeons.ui.DialogCloseListener.DialogCloseEvent;

public abstract class Dialog extends com.badlogic.gdx.scenes.scene2d.ui.Dialog {
	
	protected ScreenManager screenManager;
	protected Skin skin;
	
	public Dialog(String title) {
		super(title, ScreenManager.getSkin(), "dialog");
		
		skin = ScreenManager.getSkin();
		setSkin(skin);
		
		//Nicer default button size, easier to click on mobile devices
		getButtonTable().defaults().minWidth(100);
	}
	
	@Override
	protected void result(Object object) {
		
		DialogCloseEvent closeEvent = Pools.obtain(DialogCloseEvent.class);
		
		if (object instanceof DialogResult) {
			closeEvent.result = (DialogResult) object;
		} else {
			Gdx.app.error("DIALOG", "Expected DialogResult, but received " + object);
			closeEvent.result = DialogResult.None;
		}
		fire(closeEvent);
		
		Pools.free(closeEvent);
	}
}
