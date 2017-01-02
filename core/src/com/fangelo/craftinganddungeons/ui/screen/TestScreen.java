package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.dialog.TestDialog;

public class TestScreen extends Screen  {
	
	public TestScreen() {
		final Slider slider = new Slider(0, 100, 10, false, skin);
		
		slider.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.log("lalala", "New slider value: " + slider.getValue());
			}
		});
		
		add(slider);
		
		Button button = new TextButton("Click me!", skin);

		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.show(new TestDialog());
			}
		});
		
		row();
		
		add(button);
		
		row();
		
		add(new Table()).height(50);
		
		row();
		
		Table scrollContent = new Table(skin);
		
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.row();
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.row();
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.row();
		scrollContent.add("this");
		scrollContent.add("is");
		scrollContent.add("a");
		scrollContent.add("text");
		scrollContent.row();
										
		ScrollPane scroll = new ScrollPane(scrollContent, skin);
		
		scroll.setScrollingDisabled(false, true);
		//scroll.setFadeScrollBars(false);
		
		add(scroll).fillX().expandX();//.height(300);// ;//.expandX().height(200).fill();
		
		//table.add(new ProgressBar(0, 100, 10, false, skin));
		
		//table.add(new Image(img));		
	}

}
