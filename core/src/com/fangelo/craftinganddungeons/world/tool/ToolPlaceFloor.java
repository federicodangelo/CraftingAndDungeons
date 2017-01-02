package com.fangelo.craftinganddungeons.world.tool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.world.InputMode;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class ToolPlaceFloor extends Tool {

	@Override
	public TextureRegion getIcon() {
		return ScreenManager.getTextureAtlas().findRegion("icon-wheelbarrow");	
	}

	@Override
	public String getIconDescription() {
		return "place\nfloor";
	}

	@Override
	public String getDescription() {
		return "Place floor tiles on the floor";
	}

	@Override
	public void onSelected(WorldInputHandler worldInput) {
		worldInput.setMode(InputMode.PLACE_FLOOR);
	}

}
