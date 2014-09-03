package roguelike.actors.behaviors;

import roguelike.actions.Action;
import roguelike.actions.RestAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class RandomWalkBehavior extends Behavior {

	public RandomWalkBehavior(Actor actor) {
		super(actor);
	}

	@Override
	public Action getAction() {
		MapArea map = actor.getGame().getCurrentMapArea();
		double rnd = Math.random();
		DirectionIntercardinal direction;
		if (rnd < 0.25) {
			// return new WalkAction(actor, map, DirectionIntercardinal.UP);
			direction = DirectionIntercardinal.UP;
		} else if (rnd < 0.5) {
			// return new WalkAction(actor, map, DirectionIntercardinal.LEFT);
			direction = DirectionIntercardinal.LEFT;
		} else if (rnd < 0.75) {
			// return new WalkAction(actor, map, DirectionIntercardinal.DOWN);
			direction = DirectionIntercardinal.DOWN;
		} else {
			// return new WalkAction(actor, map, DirectionIntercardinal.RIGHT);
			direction = DirectionIntercardinal.RIGHT;
		}

		Coordinate pos = actor.getPosition().createOffsetPosition(direction);
		if (map.canMoveTo(actor, pos) && map.getActorAt(pos.x, pos.y) == null)
			return new WalkAction(actor, map, direction);

		return new RestAction(actor);
	}

	@Override
	public Behavior getNextBehavior() {
		AttackAttempt lastAttackedBy = actor.getLastAttackedBy();
		if (lastAttackedBy != null) {
			System.out.println("Switching to targeted attack behavior");
			return new TargetedAttackBehavior(actor, lastAttackedBy.getActor());
		}

		return this;
	}
}
