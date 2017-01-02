package com.fangelo.craftinganddungeons.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UIUtils {
	
	static public final int DEFAULT_IMAGE_BUTTON_SIZE = 56;
	
	static public ImageButton createCustomImageButton(String iconName) {

		return createCustomImageButton(ScreenManager.getTextureAtlas().findRegion(iconName));
	}
	
	static public ImageButton createCustomImageButton(TextureRegion icon) {

		ButtonStyle defaultButtonStyle = ScreenManager.getSkin().get("default", ButtonStyle.class);
		
		ImageButtonStyle imageButtonStyle = new ImageButtonStyle(defaultButtonStyle.up, defaultButtonStyle.down, defaultButtonStyle.checked, null, null, null);
		imageButtonStyle.imageUp = imageButtonStyle.imageDown = imageButtonStyle.imageChecked = new TextureRegionDrawable(icon);
		ImageButton imageButton = new ImageButton(imageButtonStyle);
		
		return imageButton;
	}
}
