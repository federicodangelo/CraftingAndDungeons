package com.fangelo.craftinganddungeons.world.tool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.world.InputMode;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class ToolPlayer extends Tool {

	@Override
	public TextureRegion getIcon() {
		return ScreenManager.getTextureAtlas().findRegion("icon-player");
	}

	@Override
	public String getIconDescription() {
		return "player";
	}

	@Override
	public String getDescription() {
		return "Control the player";
	}

	@Override
	public void onSelected(WorldInputHandler worldInput) {
		worldInput.setMode(InputMode.MOVE_PLAYER);
	}
}
