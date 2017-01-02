package com.fangelo.craftinganddungeons.ui;

import java.util.ArrayDeque;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.fangelo.craftinganddungeons.ui.screen.SettingsScreen;

public class ScreenManager {
	static private ScalingViewport stageViewport;
	
	static private SpriteBatch spriteBatch;
	
	static private Stage stage;
	static private Table stageTable;
	
	static private Stage stageNotifications;
	static private Table stageNotificationsTable;
	
	static private Stage stageHints;
	static private Table stageHintsTable;
	
	static private Stage stageDebug;
	static private Table stageDebugTable;
	
	static private TextureAtlas uiTextureAtlas;
	static private Skin skin;
	static private Screen activeScreen;
	static private ArrayDeque<Screen> screensStack;
	
	static private Label statsLabel;
	
	static private Dialog activeDialog;
	
	static public Screen getActiveScreen() {
		return activeScreen;
	}
	
	static public Dialog getActiveDialog() {
		return activeDialog;
	}
	
	static public TextureAtlas getTextureAtlas() {
		return uiTextureAtlas;
	}
	
	static public Skin getSkin() {
		return skin;
	}
	
	//Non-instanceable
	private ScreenManager() {
		
	}
	
	static public void init() {
		
		uiTextureAtlas = new TextureAtlas(Gdx.files.internal("ui/ui.atlas"));
		skin = new Skin(Gdx.files.internal("ui/ui.json"), uiTextureAtlas);
		stageViewport = new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		spriteBatch = new SpriteBatch();
		
		stageTable = new Table(skin);
		stageTable.setFillParent(true);
		stage = new Stage(stageViewport, spriteBatch);
		stage.addActor(stageTable);
		
		stageNotificationsTable = new Table(skin);
		stageNotificationsTable.setFillParent(true);
		stageNotifications = new Stage(stageViewport, spriteBatch);
		stageNotifications.addActor(stageNotificationsTable);
		
		stageHintsTable = new Table(skin);
		stageHintsTable.setFillParent(true);
		stageHints = new Stage(stageViewport, spriteBatch);
		stageHints.addActor(stageHintsTable);
		
		stageDebugTable = new Table(skin);
		stageDebugTable.setFillParent(true);
		stageDebug = new Stage(stageViewport, spriteBatch);
		stageDebug.addActor(stageDebugTable);
		
		screensStack = new ArrayDeque<Screen>();
		
		statsLabel = new Label("", skin);
		stageDebugTable.add(statsLabel).expand().left().top().pad(5);
	}
	
	static public void dispose() {
		
		if (stage != null)
			stage.dispose();
		
		if (stageNotifications != null)
			stageNotifications.dispose();
		
		if (stageHints != null)
			stageHints.dispose();
		
		if (stageDebug != null)
			stageDebug.dispose();
		
		if (spriteBatch != null)
			spriteBatch.dispose();
		
		if (skin != null)
			skin.dispose();
		
		//No need to dispose the texture atlas, it's automatically
		//disposed when the skin is disposed
		uiTextureAtlas = null;
    	
		stageViewport = null;
		
		stage = null;
		stageTable = null;
		
		stageNotifications = null;
		stageNotificationsTable = null;
		
		stageHints = null;
		stageHintsTable = null;
		
		stageDebug = null;
		stageDebugTable = null;
		
		skin = null;
		activeScreen = null;
		
		screensStack = null;
	}
	
	static public Stage getStage() {
		return stage;
	}
	
	static public void resize(int width, int height) {
		stageViewport.setWorldSize(width / 2, height / 2);
		stageViewport.update(width, height, true);
		
		if (activeScreen != null)
			activeScreen.onResize(width, height);
	}
	
	static public void updateAndDraw() {
		stage.act(Gdx.graphics.getDeltaTime());
	    stage.draw();
	    
	    stageHints.act(Gdx.graphics.getDeltaTime());
	    stageHints.draw();
	    
	    stageNotifications.act(Gdx.graphics.getDeltaTime());
	    stageNotifications.draw();
	    
	    if (SettingsScreen.showFps) {
	    	updateDebug(Gdx.graphics.getDeltaTime());
	    	stageDebug.act(Gdx.graphics.getDeltaTime());
	    	stageDebug.draw();
	    }
	    
	    //stageTable.drawDebug(shapeRenderer); // This is optional, but enables debug lines for tables.
	}
	
	static private StringBuilder statsBuilder = new StringBuilder();
	
	static private float statsUpdateTime;
	static private int statsUpdateTimeFrames;
	
	static private void updateDebug(float delta) {
		statsUpdateTime += delta;
		statsUpdateTimeFrames++;
		
		if (statsUpdateTime > 1.0f) {
			
			statsBuilder.setLength(0);;
			statsBuilder.append("fps: ");
			statsBuilder.append(Gdx.graphics.getFramesPerSecond());
			
			if (GLProfiler.isEnabled()) {
				statsBuilder.append(" dc: ");
				statsBuilder.append(GLProfiler.drawCalls / statsUpdateTimeFrames);
				statsBuilder.append(" vc: ");
				statsBuilder.append((int) (GLProfiler.vertexCount.total / statsUpdateTimeFrames));
				GLProfiler.reset();
			}		
			
			statsBuilder.append("\nmem: ");
			statsBuilder.append(Gdx.app.getNativeHeap() / 1024);
			statsBuilder.append("K (nat) ");
			statsBuilder.append(Gdx.app.getJavaHeap() / 1024);
			statsBuilder.append("K (java)");
			
			statsLabel.setText(statsBuilder.toString());
			
			statsUpdateTimeFrames = 0;
			statsUpdateTime = 0.0f;
		}		
	}
	
	static public void show(Screen screen) {
		
		if (activeScreen != null) {
			activeScreen.onHide();
			stageTable.removeActor(activeScreen);
		}
		
		screensStack.clear();
		
		activeScreen = screen;
		
		activeScreen.onShow();
		
		stageTable.addActor(screen);
	}
	
	static public void push(Screen screen) {
		if (activeScreen != null) {
			activeScreen.onHide();
			stageTable.removeActor(activeScreen);
			screensStack.push(activeScreen);
		}
		
		activeScreen = screen;
		
		activeScreen.onShow();
		
		stageTable.addActor(activeScreen);
	}
	
	static public boolean canPop() {
		return screensStack.size() > 0;
	}
	
	static public void pop() {
		if (activeScreen != null) {
			activeScreen.onHide();
			stageTable.removeActor(activeScreen);
		}
		
		activeScreen = screensStack.pop();
		
		activeScreen.onShow();
		
		stageTable.addActor(activeScreen);
	}
	
	static public void showNotification(String message) {
		
		Gdx.app.log("NOTIFICATION", message);
		
		Table notification = new Table(skin);
		notification.setBackground("button-brown");
		notification.add(message);
		notification.addAction(
				Actions.sequence(
						Actions.fadeIn(0.25f),
						Actions.delay(3.0f),
						Actions.fadeOut(0.25f),
						Actions.removeActor()
				)
		);
		
		stageNotificationsTable.add(notification).fillX().expandX().pad(0, 5, 0, 5);
	}
	

	static public void showHint(String message) {
		
		//Remove existing tips
		stageHintsTable.clear();
		
		Table hint = new Table(skin);
		hint.setBackground("button-brown");
		hint.add(message);
		hint.addAction(
				Actions.sequence(
						Actions.fadeIn(0.25f),
						Actions.delay(2.0f),
						Actions.fadeOut(0.25f),
						Actions.removeActor()
				)
		);
		
		stageHintsTable.add(hint).top().expand().pad(5);
	}	

	static private DialogCloseListener dialogCloseListener = new DialogCloseListener() {
		@Override
		public void closed(DialogCloseEvent event, Dialog dialog) {
			if (activeDialog == dialog)
				activeDialog = null;
			dialog.removeListener(this);
		}
	}; 
	
	static public Dialog show(Dialog dialog) {
		activeDialog = dialog;
		dialog.addListener(dialogCloseListener);
		dialog.show(getStage());
		return dialog;
	}
	
	static public Dialog showWithoutFadeIn(Dialog dialog) {
		activeDialog = dialog;
		dialog.addListener(dialogCloseListener);
		dialog.show(getStage(), null);
		dialog.setPosition(Math.round((stage.getWidth() - dialog.getWidth()) / 2), Math.round((stage.getHeight() - dialog.getHeight()) / 2));
		return dialog;
	}
	
}
