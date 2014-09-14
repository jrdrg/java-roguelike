package roguelike.actors.behaviors;

import roguelike.Game;
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

		Game.current().displayMessage(actor.getName() + " is now attacking " + target.getName(), SColor.PURPLE);
		this.target = target;
	}

	@Override
	public Action getAction() {

		Coordinate actorPos = actor.getPosition();
		Coordinate targetPos = target.getPosition();

		float radius = radiusStrategy.radius(actorPos.x, actorPos.y, targetPos.x, targetPos.y);

		if (canAttackTarget(radius)) {
			if (actor.canSee(target, Game.current().getCurrentMapArea()))
				return new AttackAction(actor, target);
			else
				Game.current().displayMessage(target.getName() + "is no longer in sight range.");
		}

		// walk towards the target
		Coordinate diff = actorPos.createOffsetPosition(-targetPos.x, -targetPos.y);

		DirectionIntercardinal direction = DirectionIntercardinal.getDirection(-diff.x, -diff.y);
		MapArea mapArea = Game.current().getCurrentMapArea();
		return new WalkAction(actor, mapArea, direction);
	}

	@Override
	public Behavior getNextBehavior() {
		if (actor.isAlive()) {
			if (target.isAlive() && isTargetVisible()) {
				if (actor.getEquipment().getEquippedWeapons()[0] != null)
					return this;
				else {
					System.out.println(String.format("%s has no weapon!", actor.getName()));
				}
			}
			// TODO: figure out what to do when target is dead or out of sight
			// range
			Game.current().displayMessage(actor.getName() + " is no longer targeting " + target.getName() + "...", SColor.GRAY);
			return new RandomWalkBehavior(actor);
		}
		return null;
	}

	private boolean isTargetVisible() {
		Coordinate actorPos = actor.getPosition();
		Coordinate targetPos = target.getPosition();

		float radius = radiusStrategy.radius(actorPos.x, actorPos.y, targetPos.x, targetPos.y);
		if (radius <= actor.getVisionRadius()) {
			return actor.canSee(target, Game.current().getCurrentMapArea());
		}
		return false;
	}

	private boolean canAttackTarget(float distance) {
		return (distance <= 1);
	}
}
