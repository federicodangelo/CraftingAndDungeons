package com.fangelo.craftinganddungeons.ui;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;

abstract public class DialogCloseListener implements EventListener {
	public boolean handle (Event event) {
		if (!(event instanceof DialogCloseEvent)) return false;
		closed((DialogCloseEvent) event, (Dialog) event.getTarget());
		return false;
	}

	/** @param actor The event target, which is the actor that emitted the change event. */
	abstract public void closed (DialogCloseEvent event, Dialog dialog);

	/** Fired when something in an actor has changed. This is a generic event, exactly what changed in an actor will vary.
	 * @author Nathan Sweet */
	static public class DialogCloseEvent extends Event {
		public DialogResult result;
	}
}
