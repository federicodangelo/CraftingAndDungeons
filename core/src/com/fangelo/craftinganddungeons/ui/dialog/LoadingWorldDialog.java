package com.fangelo.craftinganddungeons.ui.dialog;

import com.fangelo.craftinganddungeons.ui.Dialog;
import com.fangelo.craftinganddungeons.ui.DialogResult;
import com.fangelo.craftinganddungeons.world.World;

public class LoadingWorldDialog extends Dialog {
	
	private World world;
	private boolean hidding;
	private int waitFramesBeforeBlocking = 5;
	
	public LoadingWorldDialog(World world) {
		super("Loading");
	
		text("Loading..");
		
		this.world = world;
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (!hidding && world.getWorldGenerator().isTasksQueueEmpty()) {
			hidding = true;
			result(DialogResult.Ok);
			hide();
		} else {
			waitFramesBeforeBlocking--;
			if (waitFramesBeforeBlocking < 0)
				world.getWorldGenerator().waitTasksQueueEmpty();
		}
	}
}
