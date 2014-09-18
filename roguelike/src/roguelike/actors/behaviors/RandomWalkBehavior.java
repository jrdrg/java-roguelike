package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.RestAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class RandomWalkBehavior extends Behavior {

	public RandomWalkBehavior(Actor actor) {
		super(actor);
	}

	@Override
	public Action getAction() {
		MapArea map = Game.current().getCurrentMapArea();
		double rnd = Game.current().random().nextDouble();
		DirectionIntercardinal direction;
		if (rnd < 0.25) {
			direction = DirectionIntercardinal.UP;
		} else if (rnd < 0.5) {
			direction = DirectionIntercardinal.LEFT;
		} else if (rnd < 0.75) {
			direction = DirectionIntercardinal.DOWN;
		} else {
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
			Log.debug("Switching to targeted attack behavior");
			return new TargetedAttackBehavior(actor, lastAttackedBy.getActor());
		}

		return this;
	}
}
