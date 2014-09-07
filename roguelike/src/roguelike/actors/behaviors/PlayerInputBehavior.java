package roguelike.actors.behaviors;

import java.awt.event.KeyEvent;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.CloseDoorAction;
import roguelike.actions.FailAction;
import roguelike.actions.GetItemAction;
import roguelike.actions.InventoryAction;
import roguelike.actions.QuitAction;
import roguelike.actions.RestAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Player;
import roguelike.ui.InputManager;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class PlayerInputBehavior extends Behavior {

	public PlayerInputBehavior(Player actor) {
		super(actor);
	}

	@Override
	public Action getAction() {
		KeyEvent input = InputManager.nextKey();
		if (input == null)
			return null;

		switch (input.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			return new QuitAction(actor);

		case KeyEvent.VK_LEFT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.LEFT);

		case KeyEvent.VK_RIGHT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.RIGHT);

		case KeyEvent.VK_UP:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.UP);

		case KeyEvent.VK_DOWN:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.DOWN);

		case KeyEvent.VK_PERIOD:
			return new RestAction(actor);

		case KeyEvent.VK_I:
			return new InventoryAction(actor);

		case KeyEvent.VK_C:
			return new CloseDoorAction(actor, Game.current().getCurrentMapArea());

		case KeyEvent.VK_G:
			return new GetItemAction(actor, Game.current().getCurrentMapArea());

		}
		return new FailAction(actor);
	}

	@Override
	public Behavior getNextBehavior() {
		return this;
	}

}
