package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.AttackAction;
import roguelike.actions.RestAction;
import roguelike.actors.Actor;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.fov.BasicRadiusStrategy;
import squidpony.squidgrid.fov.RadiusStrategy;

public class TargetedAttackBehavior extends Behavior {

	private Actor target;
	private RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;
	private boolean canSeeTarget;

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

			if (actor.canSee(target, Game.current().getCurrentMapArea())) {
				canSeeTarget = true;
				return new AttackAction(actor, target);
			}
			else {
				Game.current().displayMessage(target.getName() + "is no longer in sight range.");
			}
		}

		// if we can't attack, do a rest action and switch behavior to searching for the player
		canSeeTarget = false;

		return new RestAction(actor);
	}

	@Override
	public Behavior getNextBehavior() {
		if (actor.isAlive()) {

			if (!canSeeTarget) {
				return new SearchForPlayerBehavior(actor);
			}

			if (target.isAlive() && isTargetVisible()) {
				if (actor.getEquipment().getEquippedWeapons()[0] != null)
					return this;
				else {
					Log.warning(String.format("%s has no weapon!", actor.getName()));
				}
			}

			return new SearchForPlayerBehavior(actor);
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
