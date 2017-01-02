package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.PlatformAdapter;
import com.fangelo.craftinganddungeons.ui.Screen;

public class AboutScreen extends Screen {
	
	private Button closeButton;
	
	public AboutScreen() {
		setBackground("panel-brown");
		
		add("Crating and Dungeons v" + PlatformAdapter.getInstance().getVersion()).pad(10);
		row();
		addLinkButton("Developed using libgdx", "https://libgdx.badlogicgames.com/").pad(10);
		row();
		addLinkButton("Using free assets from Kenney", "http://kenney.nl/").pad(10);
		row();
		addLinkButton("Using icons from Game-icons.net", "http://game-icons.net/").pad(10);
		row();
		
		closeButton = new TextButton("Close", skin);
		
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackButtonPressed();
			}
		});
		
		add(closeButton).padTop(20);
	}
	
	private Cell<TextButton> addLinkButton(final String text, final String link) {
		TextButton button = new TextButton(text + "\n(" + link + ")", skin);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (Gdx.net != null)
					Gdx.net.openURI(link);
			}
		});
		
		return add(button);
	}
}
