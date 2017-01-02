package com.fangelo.craftinganddungeons.ui.screen;

import java.text.DateFormat;
import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fangelo.craftinganddungeons.savegame.SaveGameManager;
import com.fangelo.craftinganddungeons.savegame.SaveSlotData;
import com.fangelo.craftinganddungeons.ui.Dialog;
import com.fangelo.craftinganddungeons.ui.DialogCloseListener;
import com.fangelo.craftinganddungeons.ui.DialogResult;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.dialog.ConfirmDialog;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorParameters;
import com.fangelo.craftinganddungeons.world.generator.configuration.WorldGeneratorConfiguration;

public class SaveGameSelectorScreen extends Screen {

	private World world;
	private Button returnButton;
	
	private TextButton[] slotButtons;
	private TextButton[] deleteSlotButtons;
	private DateFormat dateFormatter;
	
	public SaveGameSelectorScreen(final World world) {
		
		setBackground("panel-brown");
		
		this.world = world;
		
		add("SELECT SAVE SLOT").padBottom(20);
		row();
		
		slotButtons = new TextButton[5];
		deleteSlotButtons = new TextButton[5];
		
		for (int i = 0; i < slotButtons.length; i++) {
			
			HorizontalGroup group = new HorizontalGroup();
			
			group.space(10);
			
			slotButtons[i] = new TextButton("Slot " + i, skin);
			deleteSlotButtons[i] = new TextButton("DELETE", skin);
			
			final int slot = i;
			
			slotButtons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					onSlotClicked(slot);
				}
			});
			
			group.addActor(slotButtons[i]);
			
			deleteSlotButtons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					onDeleteSlotClicked(slot);
				}
			});
			
			group.addActor(deleteSlotButtons[i]);
			
			add(group).pad(10);
			
			row();
		}

		returnButton = new TextButton("Return", skin);
		
		returnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackButtonPressed();
			}
		});
		
		add(returnButton).padTop(20);
		
		dateFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);		
	}
	
	@Override
	public void onShow() {
		refreshSlots();
	}
	
	private void refreshSlots() {
		
		ArrayList<SaveSlotData> saves = SaveGameManager.getSaveSlots();
		
		for (int i = 0; i < slotButtons.length; i++) {
			
			SaveSlotData foundSave = null;
			
			for (int j = 0; j < saves.size(); j++) {
				if (saves.get(j).getSlot() == i) {
					foundSave = saves.get(j);
					break;
				}
			}
			
			if (foundSave != null) {
				
				slotButtons[i].setText("Slot " + (foundSave.getSlot() + 1) + " - " + foundSave.getConfiguration().generatorType.toString() + " - " + dateFormatter.format(foundSave.getCreationDate().getTime()));
			
				deleteSlotButtons[i].setDisabled(false);
				
			} else {
				
				slotButtons[i].setText("New Game on Slot " + (i + 1));
				
				deleteSlotButtons[i].setDisabled(true);
			}
		}
	}
	
	private void onSlotClicked(int slot) {
		
		SaveSlotData slotData = SaveGameManager.getSaveSlotData(slot);
		
		if (slotData.hasData()) {
			
			WorldGeneratorConfiguration configuration = slotData.getConfiguration();
			
			world.init(new WorldGeneratorParameters(slotData, configuration));
			
		} else {
			
			Screens.newGameConfigurationScreen.setSlot(slot);
			
			ScreenManager.push(Screens.newGameConfigurationScreen);
		}
	}
	
	private void onDeleteSlotClicked(final int slot) {
		
		ScreenManager.show(new ConfirmDialog("Delete World", "Delete World?\nAll progress will be lost.")).addListener(new DialogCloseListener() {
			@Override
			public void closed(DialogCloseEvent event, Dialog dialog) {
				if (event.result == DialogResult.Yes) {
					SaveGameManager.getSaveSlotData(slot).clearSaveGame();
					
					refreshSlots();
				}
			}
		});
	}
}
