package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.CloseDoorAction;
import roguelike.actions.GetItemAction;
import roguelike.actions.InventoryAction;
import roguelike.actions.LookAction;
import roguelike.actions.QuitAction;
import roguelike.actions.RestAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Player;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class PlayerInputBehavior extends Behavior {

	public PlayerInputBehavior(Player actor) {
		super(actor);
	}

	@Override
	public Action getAction() {
		InputCommand input = InputManager.nextCommand();
		if (input == null)
			return null;

		// use Shift+up/right/down/left for up-left, up-right,down-right,down-left

		switch (input) {
		case CANCEL:
			return new QuitAction(actor);

		case LEFT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.LEFT);

		case DOWN_LEFT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.DOWN_LEFT);

		case UP_RIGHT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.UP_RIGHT);

		case RIGHT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.RIGHT);

		case UP_LEFT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.UP_LEFT);

		case UP:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.UP);

		case DOWN_RIGHT:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.DOWN_RIGHT);

		case DOWN:
			return new WalkAction(actor, Game.current().getCurrentMapArea(), DirectionIntercardinal.DOWN);

		case REST:
			return new RestAction(actor);

		case INVENTORY:
			return new InventoryAction(actor);

		case CLOSE_DOOR:
			return new CloseDoorAction(actor, Game.current().getCurrentMapArea());

		case PICK_UP:
			return new GetItemAction(actor, Game.current().getCurrentMapArea());

		case LOOK:
			return new LookAction(actor, Game.current().getCurrentMapArea());

		default:
			return null;
		}
	}

	@Override
	public Behavior getNextBehavior() {
		return this;
	}

}
