package com.fangelo.craftinganddungeons.ui.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.fangelo.craftinganddungeons.ui.Screen;
import com.fangelo.craftinganddungeons.ui.UIUtils;
import com.fangelo.craftinganddungeons.world.World;
import com.fangelo.craftinganddungeons.world.entity.Inventory;
import com.fangelo.craftinganddungeons.world.entity.InventorySlot;

public class InventoryScreen extends Screen {
	
	private World world;
	
	private Button closeButton;
	
	//First 8 slots are here
	private WidgetGroup topSlotsContainer;
	private Cell<WidgetGroup> topSlotsContainerCell;
	
	//Remaining slots are here
	private WidgetGroup scrollContainer;
	
	private Container<Actor> scrollContainerContainer;
	
	private int swapSlot;
	
	private ButtonGroup<Button> inventorySlotGroup;
	private Button[] inventorySlotButtons;
	private Label[] inventorySlotLabels;
	private Image[] inventorySlotImages;
	private TextureRegionDrawable[] inventorySlotImagesDrawables;
	
	public InventoryScreen(World world) {
		this.world = world;
		
		topSlotsContainer = new Table(skin);
		scrollContainer = new Table(skin);
		
		inventorySlotButtons = new Button[Inventory.MAX_SLOTS];
		inventorySlotLabels = new Label[Inventory.MAX_SLOTS];
		inventorySlotImages = new Image[Inventory.MAX_SLOTS];
		inventorySlotImagesDrawables = new TextureRegionDrawable[Inventory.MAX_SLOTS];
		
		inventorySlotGroup = new ButtonGroup<Button>();
		
		//First create everything and add buttons
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			
			inventorySlotButtons[i] = new Button(skin, "toggle");
			inventorySlotLabels[i] = new Label("", skin);
			inventorySlotImagesDrawables[i] = new TextureRegionDrawable(world.getTiles().getTileByName("empty"));
			inventorySlotImages[i] = new Image(inventorySlotImagesDrawables[i]);
			
			inventorySlotButtons[i].setWidth(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE);
			inventorySlotButtons[i].setHeight(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE);
			
			inventorySlotImages[i].setWidth(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6);
			inventorySlotImages[i].setHeight(-(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 6)); //vertical flip
			
			inventorySlotButtons[i].setProgrammaticChangeEvents(false);
			
			final int slotNumber = i;
			
			inventorySlotGroup.add(inventorySlotButtons[i]);
		
			inventorySlotButtons[i].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					onInventorySlotClicked(slotNumber);
				}
			});
			
			if (i < 8) {
				topSlotsContainer.addActor(inventorySlotButtons[i]);
			} else {
				scrollContainer.addActor(inventorySlotButtons[i]);
			}
		}
		
		//Now add images 
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			if (i < 8) {
				topSlotsContainer.addActor(inventorySlotImages[i]);
			} else {
				scrollContainer.addActor(inventorySlotImages[i]);
			}
		}			
		
		//And finally add labels 
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			if (i < 8) {
				topSlotsContainer.addActor(inventorySlotLabels[i]);
			} else {
				scrollContainer.addActor(inventorySlotLabels[i]);
			}
		}			
		
		scrollContainerContainer = new Container<Actor>(scrollContainer)
				.width((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * 8)
				.height((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * (((int) Math.ceil(Inventory.MAX_SLOTS / 8.0f)) - 1));
		
		ScrollPane scroll = new ScrollPane(scrollContainerContainer, skin);
		
		scroll.setScrollingDisabled(true, false);
		scroll.setForceScroll(false, true);
		
		topSlotsContainerCell = add(topSlotsContainer).width((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * 8).height(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).top().pad(5);
		
		row();
		
		add(scroll).fill().expand().padLeft(5).padRight(5);
		
		row();
		
		closeButton = UIUtils.createCustomImageButton("icon-back");
		add(closeButton).bottom().right().size(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).pad(5);
		
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onBackButtonPressed();
			}
		});
	}
	
	private int lastVisibleSlots = 8;
	
	@Override
	public void layout() {
		
		int visibleSlots = (int) (getWidth() / (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2));
		
		if (visibleSlots != lastVisibleSlots) {
			scrollContainerContainer.width((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * visibleSlots);
			scrollContainerContainer.height((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * (((int) Math.ceil((Inventory.MAX_SLOTS - 8) / (float) visibleSlots))));
			lastVisibleSlots = visibleSlots;
			
			if (visibleSlots >= 8) {
				//1 row
				topSlotsContainerCell.width((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * 8).height(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE).top().pad(5);
			} else {
				//2 rows
				topSlotsContainerCell.width((UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2) * 4).height(UIUtils.DEFAULT_IMAGE_BUTTON_SIZE * 2 + 2).top().pad(5);
			}
		}
		
		super.layout();
		
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			if (i < 8) {
				
				if (visibleSlots >= 8) {
					//1 row
					inventorySlotButtons[i].setPosition(
							(int) (i * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)), 
							0);
					
					inventorySlotImages[i].setPosition(
							(int) (i * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + 3, 
							UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 3);
							
					inventorySlotLabels[i].setPosition(
							(int) (i * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE / 3, 
							UIUtils.DEFAULT_IMAGE_BUTTON_SIZE / 2 + 1);
				} else {
					//2 rows
					inventorySlotButtons[i].setPosition(
							(int) ((i % 4) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)), 
							-(((i / 4) - 1) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)));
					
					inventorySlotImages[i].setPosition(
							(int) ((i % 4) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + 3, 
							-(((i / 4) - 1) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 3);
							
					inventorySlotLabels[i].setPosition(
							(int) ((i % 4) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE / 3, 
							-(((i / 4) - 1) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE / 2 + 1);
				}
				
			} else {
				
				int offset = i - 8;
				
				inventorySlotButtons[i].setPosition(
						(int) ((offset % visibleSlots) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)), 
						-(((offset / visibleSlots) + 1) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + scrollContainer.getHeight()
				);
				
				inventorySlotImages[i].setPosition(
						(int) ((offset % visibleSlots) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + 3, 
						-(((offset / visibleSlots) + 1) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + scrollContainer.getHeight() + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE - 3
				);
				
				inventorySlotLabels[i].setPosition(
						(int) ((offset % visibleSlots) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE / 3, 
						-(((offset / visibleSlots) + 1) * (UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2)) + scrollContainer.getHeight() - UIUtils.DEFAULT_IMAGE_BUTTON_SIZE / 2 + 1 + UIUtils.DEFAULT_IMAGE_BUTTON_SIZE + 2
				);
			}
		}
	}
	
	private void onInventorySlotClicked(int slotNumber) {
		if (swapSlot != -1) {
			//Swap
			world.getPlayer().getInventory().swapSlots(swapSlot, slotNumber);
			swapSlot = -1;
			inventorySlotGroup.uncheckAll();
			for (int i = 0; i < inventorySlotLabels.length; i++)
				inventorySlotLabels[i].setColor(Color.WHITE);
			updateInventorySlotButtons();
		} else {
			swapSlot = slotNumber;
			inventorySlotLabels[slotNumber].setColor(Color.WHITE);
		}
	}
	
	public void updateInventorySlotButtons() {
		for (int i = 0; i < inventorySlotButtons.length; i++) {
			InventorySlot slot = world.getPlayer().getInventory().getSlot(i);
			
			if (slot != null) {
				inventorySlotLabels[i].setText(Integer.toString(slot.getAmount()));
				inventorySlotImagesDrawables[i].setRegion(world.getCatalog().getEntityById(slot.getEntityId()).getImageTexture());
			} else {
				inventorySlotLabels[i].setText("");
				inventorySlotImagesDrawables[i].setRegion(world.getTiles().getTileByName("empty"));
			}
		}
	}
	
	@Override
	public void onShow() {
		updateInventorySlotButtons();
		swapSlot = -1;
		inventorySlotGroup.uncheckAll();
		for (int i = 0; i < inventorySlotLabels.length; i++)
			inventorySlotLabels[i].setColor(Color.WHITE);
	}
}
