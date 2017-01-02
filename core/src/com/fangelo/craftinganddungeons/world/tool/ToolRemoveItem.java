package com.fangelo.craftinganddungeons.world.tool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.world.InputMode;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class ToolRemoveItem extends Tool{

	@Override
	public TextureRegion getIcon() {
		return ScreenManager.getTextureAtlas().findRegion("icon-cardboard-box");	
	}

	@Override
	public String getIconDescription() {
		return "remove\nitem";
	}

	@Override
	public String getDescription() {
		return "Remove items";
	}

	@Override
	public void onSelected(WorldInputHandler worldInput) {
		worldInput.setMode(InputMode.REMOVE_ENTITY);
	}

}
