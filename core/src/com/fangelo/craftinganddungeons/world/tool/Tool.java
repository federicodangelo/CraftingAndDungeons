package com.fangelo.craftinganddungeons.world.tool;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public abstract class Tool {
	
	public abstract TextureRegion getIcon();
	
	public abstract String getIconDescription();
	
	public abstract String getDescription();
	
	public abstract void onSelected(WorldInputHandler worldInput);
}
