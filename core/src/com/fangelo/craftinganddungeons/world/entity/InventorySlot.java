package com.fangelo.craftinganddungeons.world.entity;

public class InventorySlot {
	private int slotNumber;
	private String entityId;
	private int amount;
	
	public void init(int slotNumber, String entityId, int amount) {
		this.slotNumber = slotNumber;
		this.entityId = entityId;
		this.amount = amount;
	}
	
	public String getEntityId() {
		return entityId;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getSlotNumber() {
		return slotNumber;
	}
	
	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}
	
	public void add(int val) {
		amount += val;
	}
	
	public void remove(int val) {
		amount -= val;
		if (amount < 0)
			amount = 0;
	}
}
