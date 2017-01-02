package com.fangelo.craftinganddungeons.ui.dialog;

import com.badlogic.gdx.Gdx;
import com.fangelo.craftinganddungeons.ui.Dialog;

public class TestDialog extends Dialog {
	
	public TestDialog() {
		super("Nice dialog");
		
		text("This is a nice dialog");
		
		button("Button 1");
		button("Button 2");
		button("Button 3");
	}
	
	@Override
	protected void result(Object object) {
		Gdx.app.log("NANA", "Dialog closed!");
	}
}
