package roguelike.actions;

import java.awt.event.KeyEvent;

import roguelike.actors.Actor;
import roguelike.items.Item;
import roguelike.items.Equipment.ItemSlot;
import roguelike.maps.MapArea;
import roguelike.ui.InputManager;
import roguelike.util.Coordinate;

public class LookAction extends Action {

	private MapArea mapArea;
	private Coordinate lookPosition = new Coordinate();

	protected LookAction(Actor actor, MapArea mapArea) {
		super(actor);
		this.mapArea = mapArea;
		this.usesEnergy = false;
	}

	@Override
	protected ActionResult onPerform() {

		KeyEvent nextKey = InputManager.nextKey();
		if (nextKey != null) {

			switch (nextKey.getKeyCode()) {
			case KeyEvent.VK_UP:
				break;
			case KeyEvent.VK_DOWN:
				break;
			case KeyEvent.VK_LEFT:
				break;
			case KeyEvent.VK_RIGHT:
				break;

			}
			// process key
		}

		return null;
	}
}
