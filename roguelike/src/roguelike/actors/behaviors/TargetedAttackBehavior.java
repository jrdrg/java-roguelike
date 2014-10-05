package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.AttackAction;
import roguelike.actions.WaitAction;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.items.Weapon;
import roguelike.util.Coordinate;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.fov.BasicRadiusStrategy;
import squidpony.squidgrid.fov.RadiusStrategy;

public class TargetedAttackBehavior extends Behavior {

	private Actor target;
	private RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;
	private boolean canSeeTarget;
	private Behavior nextBehavior;

	protected TargetedAttackBehavior(Actor actor, Actor target) {
		super(actor);

		Game.current().displayMessage(actor.getName() + " is now attacking " + target.getName(), SColor.PURPLE);
		this.target = target;
		this.nextBehavior = this;
	}

	@Override
	public boolean isHostile() {
		return Player.isPlayer(target);
	}

	@Override
	public Action getAction() {

		Coordinate actorPos = actor.getPosition();
		Coordinate targetPos = target.getPosition();

		float radius = radiusStrategy.radius(actorPos.x, actorPos.y, targetPos.x, targetPos.y);

		if (canAttackTarget(radius)) {

			if (actor.canSee(target, Game.current().getCurrentMapArea())) {
				canSeeTarget = true;
				nextBehavior = this;
				return new AttackAction(actor, target);
			}
			else {
				Game.current().displayMessage(target.getName() + "is no longer in sight range.");
			}
		} else if (target.isAlive() && isTargetVisible()) {

			if (actor.equipment().getEquippedWeapons().stream().filter(w -> w != null).findAny() != null)
				nextBehavior = this;

		}

		// if we can't attack, do a rest action and switch behavior to searching for the player
		canSeeTarget = false;

		nextBehavior = new SearchForPlayerBehavior(actor);
		return nextBehavior.getAction();
	}

	@Override
	public Behavior getNextBehavior() {
		if (actor.isAlive()) {
			return nextBehavior;
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
		int range = 1;
		Weapon maxRange = actor.equipment().getEquippedWeapons().stream().max((w1, w2) -> {
			if (w1 == null || w2 == null)
				return 0;
			return Integer.compare(w1.reach, w2.reach);
		}).orElse(null);

		if (maxRange != null)
			range = maxRange.getReachInTiles();

		return (distance <= range);
	}
}
