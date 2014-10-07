package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.CloseDoorAction;
import roguelike.actions.GetItemAction;
import roguelike.actions.InventoryAction;
import roguelike.actions.LookAction;
import roguelike.actions.QuitAction;
import roguelike.actions.RangedAttackAction;
import roguelike.actions.ShowMessagesAction;
import roguelike.actions.StairsDownAction;
import roguelike.actions.WaitAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.items.RangedWeapon;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class PlayerInputBehavior extends Behavior {

	public PlayerInputBehavior(Actor actor) {
		super(actor);
	}

	@Override
	public boolean isHostile() {
		return false;
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
			return walk(DirectionIntercardinal.LEFT);

		case DOWN_LEFT:
			return walk(DirectionIntercardinal.DOWN_LEFT);

		case UP_RIGHT:
			return walk(DirectionIntercardinal.UP_RIGHT);

		case RIGHT:
			return walk(DirectionIntercardinal.RIGHT);

		case UP_LEFT:
			return walk(DirectionIntercardinal.UP_LEFT);

		case UP:
			return walk(DirectionIntercardinal.UP);

		case DOWN_RIGHT:
			return walk(DirectionIntercardinal.DOWN_RIGHT);

		case DOWN:
			return walk(DirectionIntercardinal.DOWN);

		case REST:
			return new WaitAction(actor);

		case INVENTORY:
			return new InventoryAction(actor);

		case CLOSE_DOOR:
			return new CloseDoorAction(actor, Game.current().getCurrentMapArea());

		case STAIRS_DOWN:
			return useStairs();

		case PICK_UP:
			return new GetItemAction(actor, Game.current().getCurrentMapArea());

		case LOOK:
			return new LookAction(actor, Game.current().getCurrentMapArea());

		case SHOW_MESSAGES:
			return new ShowMessagesAction(actor);

		case RANGED_ATTACK:
			return getRangedAttackAction();

		default:
			return null;
		}
	}

	@Override
	public Behavior getNextBehavior() {
		return this;
	}

	private WalkAction walk(DirectionIntercardinal direction) {
		return new WalkAction(actor, Game.current().getCurrentMapArea(), direction);
	}

	private Action useStairs() {
		return new StairsDownAction(actor, Game.current().getCurrentMapArea());
	}

	private RangedAttackAction getRangedAttackAction() {
		RangedWeapon rangedWeapon = actor.equipment().getRangedWeapon();
		if (rangedWeapon != null)
			return new RangedAttackAction(actor, Game.current().getCurrentMapArea(), rangedWeapon);
		else {
			Game.current().displayMessage("You don't have a ranged weapon equipped.");
		}
		return null;
	}
}
