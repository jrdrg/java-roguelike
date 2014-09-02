package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.maps.Door;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class WalkAction extends Action {

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
			System.out.println(">>> Actor is dead! " + actor.getName());
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

			return ActionResult.failure().setMessage("Can't move there");
		}

		// we can move here
		moveTo(pos);

		ActionResult result = ActionResult.success();
		if (Player.isPlayer(actor)) {
			result.setMessage("Moving to " + pos.x + ", " + pos.y);
		}
		return result;
	}

	private void moveTo(Coordinate newPosition) {
		// get position where actor is
		Tile oldPosition = mapArea.getTileAt(actor.getPosition());

		// move the actor from the old tile to the new one
		oldPosition.moveActorTo(mapArea.getTileAt(newPosition));

		// set the actor's position to the new position
		actor.setPosition(newPosition.x, newPosition.y);
	}
}
