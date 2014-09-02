package roguelike.actors.behaviors;

import java.awt.Point;
import java.util.Queue;

import roguelike.actions.Action;
import roguelike.actions.AttackAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.util.DirectionIntercardinal;
import squidpony.squidmath.Bresenham;

public class TargetedAttackBehavior extends Behavior {

	private Actor target;

	protected TargetedAttackBehavior(Actor actor, Actor target) {
		super(actor);

		actor.getGame().displayMessage(actor.getName() + " is now attacking " + target.getName(), SColor.PURPLE);
		this.target = target;
	}

	@Override
	public Action getAction() {

		Coordinate actorPos = actor.getPosition();
		Coordinate targetPos = target.getPosition();

		Queue<Point> lineToTarget = Bresenham.line2D(actorPos, targetPos);
		if (lineToTarget.size() <= 2) {
			return new AttackAction(actor, target);
		}

		// walk towards the target
		Coordinate diff = actorPos.createOffsetPosition(-targetPos.x, -targetPos.y);

		DirectionIntercardinal direction = DirectionIntercardinal.getDirection(diff.x, diff.y);
		MapArea mapArea = actor.getGame().getCurrentMapArea();
		return new WalkAction(actor, mapArea, direction);
	}

	@Override
	public Behavior getNextBehavior() {
		if (actor.isAlive()) {
			if (target.isAlive())
				return this;

			// TODO: figure out what to do when target is dead
			return new RandomWalkBehavior(actor);
		}
		return null;
	}

}
