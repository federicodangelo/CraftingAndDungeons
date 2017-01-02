package com.fangelo.craftinganddungeons.world.tool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.world.InputMode;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class ToolPlaceItem extends Tool {

	@Override
	public TextureRegion getIcon() {
		return ScreenManager.getTextureAtlas().findRegion("icon-trample");	
	}

	@Override
	public String getIconDescription() {
		return "place\nitem";
	}

	@Override
	public String getDescription() {
		return "Place items on the floor";
	}

	@Override
	public void onSelected(WorldInputHandler worldInput) {
		worldInput.setMode(InputMode.PLACE_ENTITY);
	}

}
