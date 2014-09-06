package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.maps.Door;
import roguelike.maps.Tile;

public class OpenDoorAction extends Action {

	private Tile tile;

	public OpenDoorAction(Actor actor, Tile tile) {
		super(actor);
		this.tile = tile;
	}

	@Override
	protected ActionResult onPerform() {
		if (!(tile instanceof Door))
			return ActionResult.failure();

		Door door = (Door) tile;
		door.open(actor.getGame().getCurrentMapArea());

		return ActionResult.success();
	}

}
