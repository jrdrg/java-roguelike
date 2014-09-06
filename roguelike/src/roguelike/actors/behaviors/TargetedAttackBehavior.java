package roguelike.actors.behaviors;

import roguelike.actions.Action;
import roguelike.actions.AttackAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.fov.BasicRadiusStrategy;
import squidpony.squidgrid.fov.RadiusStrategy;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class TargetedAttackBehavior extends Behavior {

	private Actor target;
	private RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

	protected TargetedAttackBehavior(Actor actor, Actor target) {
		super(actor);

		actor.getGame().displayMessage(actor.getName() + " is now attacking " + target.getName(), SColor.PURPLE);
		this.target = target;
	}

	@Override
	public Action getAction() {

		Coordinate actorPos = actor.getPosition();
		Coordinate targetPos = target.getPosition();

		float radius = radiusStrategy.radius(actorPos.x, actorPos.y, targetPos.x, targetPos.y);

		if (canAttackTarget(radius)) {
			return new AttackAction(actor, target);
		}

		// walk towards the target
		Coordinate diff = actorPos.createOffsetPosition(-targetPos.x, -targetPos.y);

		DirectionIntercardinal direction = DirectionIntercardinal.getDirection(-diff.x, -diff.y);
		MapArea mapArea = actor.getGame().getCurrentMapArea();
		return new WalkAction(actor, mapArea, direction);
	}

	@Override
	public Behavior getNextBehavior() {
		if (actor.isAlive()) {
			if (target.isAlive() && isTargetVisible())
				return this;

			// TODO: figure out what to do when target is dead or out of sight
			// range
			actor.getGame().displayMessage(target.getName() + " has gone out of sight range...", SColor.GRAY);
			return new RandomWalkBehavior(actor);
		}
		return null;
	}

	private boolean isTargetVisible() {
		Coordinate actorPos = actor.getPosition();
		Coordinate targetPos = target.getPosition();

		float radius = radiusStrategy.radius(actorPos.x, actorPos.y, targetPos.x, targetPos.y);
		return (radius <= actor.getVisionRadius());
	}

	private boolean canAttackTarget(float distance) {
		return (distance <= 1);
	}
}
