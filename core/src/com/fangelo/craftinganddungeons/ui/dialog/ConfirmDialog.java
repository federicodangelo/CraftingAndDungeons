package com.fangelo.craftinganddungeons.ui.dialog;

import com.fangelo.craftinganddungeons.ui.Dialog;
import com.fangelo.craftinganddungeons.ui.DialogResult;

public class ConfirmDialog extends Dialog {
	
	public ConfirmDialog(String title, String text) {
		super(title);
		
		text(text);
		
		button("Yes", DialogResult.Yes);
		button("No", DialogResult.No); 
	}
}
