package com.fangelo.craftinganddungeons.world.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.fangelo.craftinganddungeons.catalog.Catalog;

public class Inventory {
	
	static public final int MAX_SLOTS = 99;
	
	//Inventory slots, sorted by slot number
	private Array<InventorySlot> slots = new Array<InventorySlot>();
	private Catalog catalog;
	
	public void setCatalog(Catalog catalog) {
		this.catalog = catalog;
	}
	
	public Array<InventorySlot> getSlots() {
		return slots;
	}
	
	public InventorySlot getSlot(int slotNumber) {
		for (int i = 0; i < slots.size; i++)
			if (slots.get(i).getSlotNumber() == slotNumber)
				return slots.get(i);
		
		return null;
	}
	
	public void removeFromSlot(InventorySlot slot, int amount) {
		
		slot.remove(amount);
		
		if (slot.getAmount() == 0) {
			//Slot empty, remove from slots array
			slots.removeValue(slot, true);
			Pools.get(InventorySlot.class).free(slot);
		}
	}
	
	public boolean remove(String entityId, int amount) {
		
		for (int i = slots.size - 1; i >= 0 && amount > 0; i--) {
			if (slots.get(i).getEntityId().equals(entityId)) {
				InventorySlot slot = slots.get(i);
				if (slot.getAmount() <= amount) {
					//Slot will be completely empty, remove from slots arrays
					amount -= slot.getAmount();
					slots.removeIndex(i);
					Pools.get(InventorySlot.class).free(slot);
					
				} else {
					slot.remove(amount);
					amount = 0;
				}
			}
		}
		
		return amount <= 0;
	}
	
	public boolean has(String entityId, int amount) {
		
		for (int i = 0; i < slots.size && amount > 0; i++)
			if (slots.get(i).getEntityId().equals(entityId))
				amount -= slots.get(i).getAmount();
		
		return amount <= 0;
	}
	
	public int add(String entityId, int amount) {
		
		int maxStack = catalog.getEntityById(entityId).getMaxInventoryStack();
		
		for (int i = 0; i < slots.size; i++) {
			if (slots.get(i).getEntityId().equals(entityId) && slots.get(i).getAmount() < maxStack) {
				int canAdd = maxStack - slots.get(i).getAmount();
				if (amount <= canAdd) {
					//Slot found with enough space to add the amount, use it
					slots.get(i).add(amount);
					amount = 0;
					break;
				} else {
					//Add what we can to the found slot, and keep searching for another slot
					slots.get(i).add(canAdd);
					amount -= canAdd;
				}
			}
		}
		
		while (amount > 0 && slots.size < MAX_SLOTS) {
			
			InventorySlot slot = Pools.get(InventorySlot.class).obtain();
			slot.init(getEmptySlotNumber(), entityId, Math.min(maxStack, amount));
			
			insertSlot(slot);
			
			amount -= slot.getAmount();
		}
		
		//Return remaining value
		return amount;
	}
	
	private int getEmptySlotNumber() {
		//This works because we keep slots sorted by slot, so the first "hole" that 
		//we find in the sequence if an empty slot that can be used.
		for (int i = 0; i < slots.size; i++)
			if (slots.get(i).getSlotNumber() != i)
				return i;
		
		return slots.size;
	}
	
	public void loadSlots(Array<InventorySlot> inventorySlots) {
		clear();
		//We assume that the received array can be kept, so we just use it.. and also
		//assume that is already sorted by slot number
		this.slots = inventorySlots;
	}
	
	public void clear() {
		Pools.get(InventorySlot.class).freeAll(slots);
		slots.clear();
	}
	
	private void insertSlot(InventorySlot slot) {
		//Insert entry in slots keeping the list orderer by slot
		boolean inserted = false;
		for (int i = 0; i < slots.size; i++) {
			if (slots.get(i).getSlotNumber() > slot.getSlotNumber()) {
				slots.insert(i, slot);
				inserted = true;
				break;
			}
		}
		if (!inserted)
			slots.add(slot); //add last
	}

	public void swapSlots(int fromSlotNumber, int toSlowNumber) {
		
		InventorySlot fromSlot = getSlot(fromSlotNumber);
		InventorySlot toSlot = getSlot(toSlowNumber);
		
		if (fromSlot != null) {
			slots.removeValue(fromSlot, true);
			fromSlot.setSlotNumber(toSlowNumber);
			insertSlot(fromSlot);
		}
		
		if (toSlot != null) {
			slots.removeValue(toSlot, true);
			toSlot.setSlotNumber(fromSlotNumber);
			insertSlot(toSlot);
		}
		
	}
}
