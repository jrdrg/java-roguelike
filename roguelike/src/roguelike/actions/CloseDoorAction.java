package roguelike.actions;

import java.awt.event.KeyEvent;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.maps.Door;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.InputManager;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class CloseDoorAction extends Action {

	private DirectionIntercardinal direction;
	private MapArea map;

	public CloseDoorAction(Actor actor, MapArea map) {
		super(actor);
		direction = DirectionIntercardinal.NONE;
		this.map = map;

		Game.current().displayMessage("Direction?");
	}

	public CloseDoorAction(Actor actor, MapArea map, DirectionIntercardinal direction) {
		super(actor);
		this.map = map;
		this.direction = direction;
	}

	@Override
	protected ActionResult onPerform() {
		if (direction == DirectionIntercardinal.NONE) {
			KeyEvent nextKey = InputManager.nextKey();
			if (nextKey != null) {
				switch (nextKey.getKeyCode()) {
				case KeyEvent.VK_UP:
					direction = DirectionIntercardinal.UP;
					break;

				case KeyEvent.VK_DOWN:
					direction = DirectionIntercardinal.DOWN;
					break;

				case KeyEvent.VK_LEFT:
					direction = DirectionIntercardinal.LEFT;
					break;

				case KeyEvent.VK_RIGHT:
					direction = DirectionIntercardinal.RIGHT;
					break;

				default:
					return ActionResult.failure().setMessage("Invalid direction");

				}
			} else {
				return ActionResult.incomplete();
			}
		}

		Coordinate pos = actor.getPosition().createOffsetPosition(direction);

		Tile tile = map.getTileAt(pos);
		if (tile instanceof Door) {
			((Door) tile).close(map);
			return ActionResult.success();
		} else {
			return ActionResult.failure().setMessage("No door in that direction");
		}
	}
}
