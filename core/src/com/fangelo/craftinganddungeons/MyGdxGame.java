package com.fangelo.craftinganddungeons;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.input.GestureDetector;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.screen.Screens;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;

public class MyGdxGame extends ApplicationAdapter {
	
	private World world;
	private WorldInputHandler worldInputHandler;
		
	public MyGdxGame(PlatformAdapter platformAdapter) {
		//No need to store platformAdapter, it's available in PlatformAdapter.getInstance()
	}
	
	@Override
	public void pause() {
		if (world.isInitialized())
			world.saveEverything();
	}
	
	@Override
	public void create () {
		
		GLProfiler.enable();
		
		ScreenManager.init();
		
		world = new World();
		
		worldInputHandler = new WorldInputHandler(world);
		
		Screens.init(world, worldInputHandler);
		
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
				
		inputMultiplexer.addProcessor(ScreenManager.getStage());
		inputMultiplexer.addProcessor(new GestureDetector(worldInputHandler)); //gestures first
		inputMultiplexer.addProcessor(worldInputHandler); //base later
		inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.BACK) {
					if (ScreenManager.getActiveScreen() != null)
						ScreenManager.getActiveScreen().onBackButtonPressed();
					return true;
				}
				return false;
			}
		});
		
		Gdx.input.setCatchBackKey(true);
		
		Gdx.input.setInputProcessor(inputMultiplexer);
				
		ScreenManager.show(Screens.mainMenuScreen);
	}
	
	@Override
	public void resize (int width, int height) {
		ScreenManager.resize(width, height);
		
		world.resize(width, height);
	}
	
	@Override
	public void render () {
		
		if (world.isLoading())
			world.updateLoading();
		
		//world.UpdateLoading can change the value of initialized, that's why we ask again..
		if (world.isInitialized()) {
			//Update input
			if (!world.isLoading())
				worldInputHandler.handleInput();
			
			//Update world
			world.update();
			
			//Update camera position
			worldInputHandler.updateCameraPosition();
		}
		
		//Draw
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (world.isInitialized()) {
			//Draw world
			world.draw();
		}
		
		//Draw UI
		ScreenManager.updateAndDraw();
	}
	
	@Override
	public void dispose() {
		world.dispose();
		ScreenManager.dispose();
		Screens.dispose();
		PlatformAdapter.dispose();
	}
}
