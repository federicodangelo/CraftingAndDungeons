package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fangelo.craftinganddungeons.catalog.entry.EntityEntry;
import com.fangelo.craftinganddungeons.ui.Dialog;
import com.fangelo.craftinganddungeons.ui.DialogCloseListener;
import com.fangelo.craftinganddungeons.ui.DialogResult;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.ScreenManager;
import com.fangelo.craftinganddungeons.ui.UIUtils;
import com.fangelo.craftinganddungeons.ui.dialog.ConfirmDialog;
import com.fangelo.craftinganddungeons.utils.ScreenshotManager;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.WorldInputHandler;
import com.fangelo.craftinganddungeons.world.entity.InventorySlot;
import com.fangelo.craftinganddungeons.world.generator.WorldGeneratorTransitionInfo;
import com.fangelo.craftinganddungeons.world.tool.Tool;
import com.fangelo.craftinganddungeons.world.tool.ToolPlaceFloor;
import com.fangelo.craftinganddungeons.world.tool.ToolPlaceItem;
import com.fangelo.craftinganddungeons.world.tool.ToolPlayer;
import com.fangelo.craftinganddungeons.world.tool.ToolRemoveFloor;
import com.fangelo.craftinganddungeons.world.tool.ToolRemoveItem;

public class InGameHudScreen extends Screen {
	
	private Table topLeftContainer;
	private Table topRightContainer;
	private Table bottomRightContainer;
	private Table bottomLeftContainer;
	
	private Table middleRightContainer;
		
	private WidgetGroup container;
		
	private Table toolsContainer;
	
	private int inventoryLines = 1;
	private Table inventoryContainer;
	
	private Button viewMapButton;
	private TextButton specialActionButton;
	
	private Button screenshotButton;
	private Button settingsButton;
	
	private Button inventoryButton;
		
	private WorldInputHandler worldInput;
		
	private World world;
	
	private int selectedTool;
	private Tool[] tools;
	private ButtonGroup<Button> toolsSlotGroup;
	private ImageTextButton[] toolsSlotButtons;
	private ImageTextButtonStyle[] toolsSlotButtonsStyles;
	private TextureRegionDrawable[] toolsSlotButtonsImages;

	private int selectedSlot;
	private ButtonGroup<Button> inventorySlotGroup;
	private ImageTextButton[] inventorySlotButtons;
	private ImageTextButtonStyle[] inventorySlotButtonsStyles;
	private TextureRegionDrawable[] inventorySlotButtonsImages;
	
	public InGameHudScreen(final WorldInputHandler worldInput, final World world) {
		
		this.worldInput = worldInput;
		this.world = world;
		
		container = new WidgetGroup();
		add(container).fill();
		
		topLeftContainer = new Table();
		topRightContainer = new Table();
		bottomRightContainer = new Table();
		bottomLeftContainer = new Table();
		middleRightContainer = new Table();
		
		container.addActor(topLeftContainer);
		container.addActor(topRightContainer);
		container.addActor(bottomRightContainer);
		container.addActor(bottomLeftContainer);
		container.addActor(middleRightContainer);
		
		viewMapButton = UIUtils.createCustomImageButton("icon-treasure-map");
		topLeftContainer.add(viewMapButton).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		viewMapButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.push(Screens.viewMapScreen);
			}
		});
		
		settingsButton = UIUtils.createCustomImageButton("icon-cog");
		topRightContainer.add(settingsButton).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		settingsButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.push(Screens.settingsScreen);
			}
		});
		
		topRightContainer.row();
		
		screenshotButton = UIUtils.createCustomImageButton("icon-cctv-camera");
		topRightContainer.add(screenshotButton).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		screenshotButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenshotManager.saveScreenshot();
			}
		});
		
		inventoryButton = UIUtils.createCustomImageButton("icon-backpack");
		bottomRightContainer.add(inventoryButton).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		inventoryButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				ScreenManager.push(Screens.inventoryScreen);
			}
		});
		
		specialActionButton = new TextButton("Special", skin);
		
		middleRightContainer.add(specialActionButton).minWidth(100).minHeight(100).pad(5);
		
		specialActionButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSpecialActionClicked();
			}
		});		
		
		createToolsToolbox();

		createInventoryToolbox();
		
		container.addActor(toolsContainer);
		container.addActor(inventoryContainer);
	}
	
	@Override
	public void layout() {
		
		super.layout();
		
		container.setWidth(getWidth());
		container.setHeight(getHeight());
		
		topLeftContainer.setPosition(
				(int) (-container.getWidth() / 2 + topLeftContainer.getPrefWidth() / 2), 
				(int) (container.getHeight() / 2 - topLeftContainer.getPrefHeight() / 2)
		);
		
		topRightContainer.setPosition(
				(int) (container.getWidth() / 2 - topRightContainer.getPrefWidth() / 2), 
				(int) (container.getHeight() / 2 - topRightContainer.getPrefHeight() / 2)
		);
		
		bottomLeftContainer.setPosition(
				(int) (-container.getWidth() / 2 + bottomLeftContainer.getPrefWidth() / 2), 
				(int) (-container.getHeight() / 2 + bottomLeftContainer.getPrefHeight() / 2)
		);
		
		bottomRightContainer.setPosition(
				(int) (container.getWidth() / 2 - bottomRightContainer.getPrefWidth() / 2), 
				(int) (-container.getHeight() / 2 + bottomRightContainer.getPrefHeight() / 2)
		);
		
		toolsContainer.setPosition(
				(int) (-container.getWidth() / 2 + toolsContainer.getPrefWidth() / 2) + 5, 
				(int) (0)
		);
		
		middleRightContainer.setPosition(
				(int) (container.getWidth() / 2 - middleRightContainer.getPrefWidth() / 2), 
				(int) (0)
		);
		
		if (inventoryLines == 1 && (container.getWidth() - inventoryContainer.getPrefWidth()) / 2 < bottomRightContainer.getPrefWidth()) {
			
			inventoryContainer.clearChildren();
			
			for (int i = 0; i < inventorySlotButtons.length; i++) {
				if (i > 0 && i % 4 == 0)
					inventoryContainer.row();
				inventoryContainer.add(inventorySlotButtons[i]).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).padLeft(2).padBottom(2);
			}
			
			inventoryLines = 2;
			
		} else if (inventoryLines == 2 && (container.getWidth() - inventoryContainer.getPrefWidth() * 2) / 2 > bottomRightContainer.getPrefWidth()) {
			
			inventoryContainer.clearChildren();
			
			for (int i = 0; i < inventorySlotButtons.length; i++)
				inventoryContainer.add(inventorySlotButtons[i]).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).padLeft(2);
			
			inventoryLines = 1;
		}
		
		inventoryContainer.setPosition(
				(int) (0), 
				(int) (-container.getHeight() / 2 + inventoryContainer.getPrefHeight() / 2) + 5
		);
	}
	
	private void createInventoryToolbox() {

		inventoryContainer = new Table();
		
		inventorySlotButtons = new ImageTextButton[8];
		inventorySlotButtonsImages = new TextureRegionDrawable[inventorySlotButtons.length];
		inventorySlotButtonsStyles = new ImageTextButtonStyle[inventorySlotButtons.length];
		
		inventorySlotGroup = new ButtonGroup<Button>();
		inventorySlotGroup.setMinCheckCount(0);
		
		
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			
			TextButtonStyle defaultTextButtonStyle = skin.get("toggle", TextButtonStyle.class);
			
			inventorySlotButtonsImages[i] = new TextureRegionDrawable(world.getTiles().getTileByName("empty"));
			inventorySlotButtonsStyles[i] = new ImageTextButtonStyle(defaultTextButtonStyle.up, defaultTextButtonStyle.down, defaultTextButtonStyle.checked, defaultTextButtonStyle.font);
			inventorySlotButtonsStyles[i].imageUp = inventorySlotButtonsStyles[i].imageDown = inventorySlotButtonsImages[i];
			inventorySlotButtonsStyles[i].checkedFontColor = Color.WHITE;
			inventorySlotButtonsStyles[i].fontColor = Color.WHITE;
			
			inventorySlotButtons[i] = new ImageTextButton("", inventorySlotButtonsStyles[i]);
			inventorySlotButtons[i].setWidth(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE);
			inventorySlotButtons[i].setHeight(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE);
			
			//Put label OVER image
			inventorySlotButtons[i].clearChildren();
			WidgetGroup group = new WidgetGroup();
			group.addActor(inventorySlotButtons[i].getImage());
			
			inventorySlotButtons[i].getImage().setWidth(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6);
			inventorySlotButtons[i].getImage().setHeight(-(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6)); //flip image vertically
			inventorySlotButtons[i].getImage().setPosition(-(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6) / 2, (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6) / 2);
			group.addActor(inventorySlotButtons[i].getLabel());
			
			inventorySlotButtons[i].setProgrammaticChangeEvents(false);
			inventorySlotButtons[i].add(group).top().left();
			
			final int slotNumber = i;
			
			inventorySlotGroup.add(inventorySlotButtons[i] );
			
			inventorySlotButtons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					onInventorySlotClicked(slotNumber, true);
				}
			});
			
			inventoryContainer.add(inventorySlotButtons[i]).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).padRight(2);
		}
		
		inventoryLines = 1;
	}
		
	private void createToolsToolbox() {
		
		toolsContainer = new Table();
		
		tools = new Tool[5];
		tools[0] = new ToolPlayer();
		tools[1] = new ToolPlaceItem();
		tools[2] = new ToolRemoveItem();
		tools[3] = new ToolPlaceFloor();
		tools[4] = new ToolRemoveFloor();
		
		toolsSlotButtons = new ImageTextButton[tools.length];
		toolsSlotButtonsImages = new TextureRegionDrawable[tools.length];
		toolsSlotButtonsStyles = new ImageTextButtonStyle[tools.length];
		
		toolsSlotGroup = new ButtonGroup<Button>();
		
		for (int i = 0; i < toolsSlotButtons.length; i++) {
			
			Tool tool = tools[i];
			
			TextButtonStyle defaultTextButtonStyle = skin.get("toggle", TextButtonStyle.class);
			
			toolsSlotButtonsImages[i] = new TextureRegionDrawable(tool.getIcon());
			toolsSlotButtonsStyles[i] = new ImageTextButtonStyle(defaultTextButtonStyle.up, defaultTextButtonStyle.down, defaultTextButtonStyle.checked, defaultTextButtonStyle.font);
			toolsSlotButtonsStyles[i].imageUp = toolsSlotButtonsStyles[i].imageDown = toolsSlotButtonsImages[i];
			toolsSlotButtonsStyles[i].checkedFontColor = Color.WHITE;
			toolsSlotButtonsStyles[i].fontColor = Color.WHITE;
			
			toolsSlotButtons[i] = new ImageTextButton("", toolsSlotButtonsStyles[i]);
			toolsSlotButtons[i].setWidth(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE);
			toolsSlotButtons[i].setHeight(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE);
						
			//Put label OVER image
			toolsSlotButtons[i].clearChildren();
			WidgetGroup group = new WidgetGroup();
			group.addActor(toolsSlotButtons[i].getImage());
			
			toolsSlotButtons[i].getImage().setWidth(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6);
			toolsSlotButtons[i].getImage().setHeight(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6);
			toolsSlotButtons[i].getImage().setPosition(-(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6) / 2, -(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6) / 2);
			group.addActor(toolsSlotButtons[i].getLabel());
			
			toolsSlotButtons[i].setProgrammaticChangeEvents(false);
			toolsSlotButtons[i].add(group).top().left();
			
			//Set icon description AFTER putting the label OVER the image, otherwise 
			//it shows up misaligned
			toolsSlotButtons[i].getLabel().setText(tool.getIconDescription());
			
			toolsSlotGroup.add(toolsSlotButtons[i]);
			
			final int toolNumber = i;
			
			toolsSlotButtons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					onToolSlotClicked(toolNumber, true);
				}
			});	
			
			if (i == 0) {
				//Player icon goes to bottom left container
				bottomLeftContainer.add(toolsSlotButtons[i]).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
			} else {
				toolsContainer.add(toolsSlotButtons[i]).size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).padTop(2);
				toolsContainer.row();
			}
		}
	}
	
	private void onToolSlotClicked(int toolNumber, boolean showHint) {
		if (showHint)
			ScreenManager.showHint(tools[toolNumber].getDescription());
		selectedTool = toolNumber;
		toolsSlotButtons[toolNumber].setChecked(true);
		tools[toolNumber].onSelected(worldInput);
	}
	
	private void onInventorySlotClicked(int slotNumber, boolean switchTool) {
		selectedSlot = slotNumber;
		InventorySlot slot = world.getPlayer().getInventory().getSlot(slotNumber);
		inventorySlotButtons[slotNumber].setChecked(true);
		worldInput.setSelectedInventorySlot(slot);
		
		if (switchTool && slot != null) {
			EntityEntry slotEntity = world.getCatalog().getEntityById(slot.getEntityId());
			
			if (slotEntity != null) {
				if (slotEntity.isFloor()) {
					//Switch to floor placement tool
					for (int i = 0; i < tools.length; i++)
						if (tools[i] instanceof ToolPlaceFloor)
							onToolSlotClicked(i, true);
				} else {
					//Switch to entity placement tool
					for (int i = 0; i < tools.length; i++)
						if (tools[i] instanceof ToolPlaceItem)
							onToolSlotClicked(i, true);
				}
			}
			
		}
	}
	
	public void updateInventorySlotButtons() {
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			InventorySlot slot = world.getPlayer().getInventory().getSlot(i);
			
			if (slot != null) {
				inventorySlotButtons[i].setText(Integer.toString(slot.getAmount()));
				inventorySlotButtonsImages[i].setRegion(world.getCatalog().getEntityById(slot.getEntityId()).getImageTexture());
			} else {
				inventorySlotButtons[i].setText("");
				inventorySlotButtonsImages[i].setRegion(world.getTiles().getTileByName("empty"));
			}
		}
	}
	
	@Override
	public void onShow() {
		super.onShow();
		
		for (int i = 0; i < toolsSlotButtons.length; i++)
			toolsSlotButtons[i].getLabel().setVisible(SettingsScreen.showToolLabels);
		
		updateInventorySlotButtons();
		
		onToolSlotClicked(selectedTool, false);
		onInventorySlotClicked(selectedSlot, false);
		
		specialActionButton.setVisible(false);
		screenshotButton.setVisible(SettingsScreen.showScreenshotButton);
		toolsContainer.setVisible(SettingsScreen.showTools);
	}
	
	@Override
	public void onBackButtonPressed() {
		ScreenManager.show(new ConfirmDialog("Return to Main Menu", "Return to Main Menu?\nGame progress will be automatically saved")).addListener(new DialogCloseListener() {
			@Override
			public void closed(DialogCloseEvent event, Dialog dialog) {
				if (event.result == DialogResult.Yes) {
					world.saveEverything();
					world.clear();
					ScreenManager.show(Screens.mainMenuScreen);
				}
			}
		});
	}
	
	private void onSpecialActionClicked() {
		
		GridPoint2 playerMapPosition = World.getTileMapPosition(world.getPlayer().getX(),world.getPlayer().getY());
		
		if (world.getEntityTile(playerMapPosition, world.getPlayer().getMapId()) != null) {
			if (world.getEntityTile(playerMapPosition, world.getPlayer().getMapId()).getTag().equals(EntityEntry.TAG_DUNGEON_ENTRANCE)) {
				
				WorldGeneratorTransitionInfo transitionInfo = world.getWorldGenerator().getEnterMapId(world.getPlayer().getMapId(), playerMapPosition.x, playerMapPosition.y);
				
				//ENTER DUNGEON
				if (transitionInfo != null)
					world.changePlayerMap(transitionInfo.mapId, transitionInfo.x, transitionInfo.y);
				
			} else if (world.getEntityTile(playerMapPosition, world.getPlayer().getMapId()).getTag().equals(EntityEntry.TAG_DUNGEON_EXIT)) {
				
				WorldGeneratorTransitionInfo transitionInfo = world.getWorldGenerator().getExitMapId(world.getPlayer().getMapId(), playerMapPosition.x, playerMapPosition.y);
				
				//EXIT DUNGEON
				if (transitionInfo != null)
					world.changePlayerMap(transitionInfo.mapId, transitionInfo.x, transitionInfo.y);
			}
		}
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		if (!world.getPlayer().isIdle()) {
			specialActionButton.setVisible(false);
		} else {
			GridPoint2 playerMapPosition = World.getTileMapPosition(world.getPlayer().getX(),world.getPlayer().getY());
			
			if (world.getEntityTile(playerMapPosition, world.getPlayer().getMapId()) != null) {
				if (world.getEntityTile(playerMapPosition, world.getPlayer().getMapId()).getTag().equals(EntityEntry.TAG_DUNGEON_ENTRANCE)) {
					specialActionButton.setVisible(true);
					specialActionButton.setText("ENTER\nDUNGEON");
				} else if (world.getEntityTile(playerMapPosition, world.getPlayer().getMapId()).getTag().equals(EntityEntry.TAG_DUNGEON_EXIT)) {
					specialActionButton.setVisible(true);
					specialActionButton.setText("EXIT\nDUNGEON");
				} else {
					specialActionButton.setVisible(false);
				}
			} else {
				specialActionButton.setVisible(false);
			}
			
			invalidate();
		}
	}
}
