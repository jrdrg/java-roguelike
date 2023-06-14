package roguelike.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actors.Actor;
import roguelike.maps.Door;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class WalkAction extends Action {
    private static final Logger LOG = LogManager.getLogger(WalkAction.class);

	private MapArea mapArea;
	private DirectionIntercardinal direction;
	private boolean canOpenDoors;

	public WalkAction(Actor actor, MapArea mapArea, DirectionIntercardinal direction) {
		this(actor, mapArea, direction, true);
	}

	public WalkAction(Actor actor, MapArea mapArea, DirectionIntercardinal direction, boolean canOpenDoors) {
		super(actor);
		this.mapArea = mapArea;
		this.direction = direction;
		this.canOpenDoors = canOpenDoors;
	}

	@Override
	public ActionResult onPerform() {
		if (!actor.isAlive()) {
			LOG.warn(">>> Actor is dead = {}", actor.getName());
			return ActionResult.success().setMessage("Actor is dead");
		}

		if (direction.deltaX == 0 && direction.deltaY == 0) {
			return ActionResult.alternate(new FailAction(actor));
		}
		Coordinate pos = actor.getPosition().createOffsetPosition(direction);

		if (mapArea.getActorAt(pos.x, pos.y) != null) {
			// TODO: behavior.getAttackAction() - allow player to confirm, allow
			// npc's to attack or not depending on behavior

			return ActionResult.alternate(new AttackAction(actor, mapArea.getActorAt(pos.x, pos.y)));
		}

		if (!mapArea.canMoveTo(actor, pos)) {
			Tile tile = mapArea.getTileAt(pos);
			if (tile != null && tile instanceof Door && canOpenDoors)
				return ActionResult.alternate(new OpenDoorAction(actor, tile));

			return ActionResult.failure().setMessage(actor.doAction("can't move there"));
		}

		// we can move here
		mapArea.moveActor(actor, pos);

		ActionResult result = ActionResult.success();
		return result;
	}
}
