package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.savegame.SaveGameManager;
import com.fangelo.craftinganddungeons.savegame.SaveSlotData;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorParameters;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorType;
import com.fangelo.craftinganddungeons.world.generator.configuration.WorldGeneratorConfiguration;

public class NewGameConfigurationScreen extends Screen {
	
	private Button prevGeneratorButton;
	private Button nextGeneratorButton;
	
	private Label selectedGeneratorLabel;
	
	private Button startGameButton;
	private Button returnButton;
	
	private WorldGeneratorType[] generatorTypes = WorldGeneratorType.values();
	
	private TextField generatorSeedField;
	
	private int generatorTypeIndex;
	private WorldGeneratorType generatorType;
	
	private World world;
	
	private int slot;
	
	public void setSlot(int slot) {
		this.slot = slot;
	}
	
	public NewGameConfigurationScreen(final World world) {
		
		setBackground("panel-brown");
		
		generatorTypeIndex = 0;
		generatorType = generatorTypes[generatorTypeIndex];
		
		this.world = world;
		
		add("NEW GAME CONFIGURATION").padBottom(20);
		row();
		
		HorizontalGroup generatorGroup = new HorizontalGroup();
		
		generatorGroup.space(10);
		
		add(generatorGroup).pad(10);
		
		prevGeneratorButton = new TextButton("<<", skin);
		
		prevGeneratorButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPrevGeneratorButtonClick();
			}
		});
		
		generatorGroup.addActor(prevGeneratorButton);
		
		selectedGeneratorLabel = new Label("GENERATOR", skin);
		
		generatorGroup.addActor(selectedGeneratorLabel);
		
		nextGeneratorButton = new TextButton(">>", skin);
		
		nextGeneratorButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onNextGeneratorButtonClick();
			}
		});
		
		generatorGroup.addActor(nextGeneratorButton);	
		
		row();
		
		HorizontalGroup seedGroup = new HorizontalGroup();
		
		seedGroup.space(10);
		
		generatorSeedField = new TextField("seed", skin);
		generatorSeedField.setMaxLength(Long.toString(Long.MAX_VALUE).length());
		generatorSeedField.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		
		seedGroup.addActor(new Label("Seed:", skin ));
		seedGroup.addActor(generatorSeedField);
		
		add(seedGroup).pad(10);
		
		row();
		
		HorizontalGroup buttonsGroup = new HorizontalGroup();
		
		buttonsGroup.space(10);
		
		add(buttonsGroup).pad(20);
		
		returnButton = new TextButton("Return", skin);
		
		returnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onReturnButtonClick();
			}
		});
		
		buttonsGroup.addActor(returnButton);
		
		startGameButton = new TextButton("Start Game", skin);
		
		startGameButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onStartGameButtonClick();
			}
		});
		
		buttonsGroup.addActor(startGameButton);
				
		updateSelectedWorldGenerator();
	}	
	
	@Override
	public void onShow() {
		generatorSeedField.setText(Long.toString(MathUtils.random.nextLong()));
	}
	
	private void updateSelectedWorldGenerator() {
		selectedGeneratorLabel.setText(generatorType.toString());
	}
	
	private void onNextGeneratorButtonClick() {
		generatorTypeIndex = (generatorTypeIndex + 1) % generatorTypes.length;
		generatorType = generatorTypes[generatorTypeIndex];
		updateSelectedWorldGenerator();
	}
		
	private void onPrevGeneratorButtonClick() {
		generatorTypeIndex--;
		if (generatorTypeIndex < 0)
			generatorTypeIndex = generatorTypes.length - 1;
		generatorType = generatorTypes[generatorTypeIndex];
		updateSelectedWorldGenerator();
	}

	private void onStartGameButtonClick() {
		
		long seed = 0;
		
		try {
			seed = Long.parseLong(generatorSeedField.getText());
		} catch(NumberFormatException ex) {
			return;
		}
		
		WorldGeneratorConfiguration configuration = new WorldGeneratorConfiguration();
		configuration.generatorType = generatorType;
		configuration.seed = seed;
		
		SaveSlotData slotData = SaveGameManager.getSaveSlotData(slot);
		slotData.clearSaveGame();
		slotData.saveConfiguration(configuration);
		
		world.init(new WorldGeneratorParameters(slotData, configuration));		
	}
	
	private void onReturnButtonClick() {
		onBackButtonPressed();
	}
}
