package com.fangelo.craftinganddungeons.world.tool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.world.InputMode;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class ToolRemoveFloor extends Tool {

	@Override
	public TextureRegion getIcon() {
		return ScreenManager.getTextureAtlas().findRegion("icon-spade");	
	}

	@Override
	public String getIconDescription() {
		return "remove\nfloor";
	}

	@Override
	public String getDescription() {
		return "Remove floor tiles";
	}

	@Override
	public void onSelected(WorldInputHandler worldInput) {
		worldInput.setMode(InputMode.REMOVE_FLOOR);
	}

}
