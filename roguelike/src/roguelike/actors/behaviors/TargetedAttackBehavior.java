package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.AttackAction;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.fov.BasicRadiusStrategy;
import squidpony.squidgrid.fov.RadiusStrategy;

public class TargetedAttackBehavior extends EnemyBehavior {

	private Actor target;
	private RadiusStrategy radiusStrategy = BasicRadiusStrategy.CIRCLE;

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
				nextBehavior = this;
				return new AttackAction(actor, target);
			}
			else {
				Game.current().displayMessage(target.getName() + " is no longer in sight range.");
			}
		} else if (target.isAlive() && isTargetVisible()) {

			if (actor.equipment().getEquippedWeapons().stream().filter(w -> w != null).findAny() != null)
				nextBehavior = this;

		}

		// if we can't attack, switch behavior to searching for the player
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

	@Override
	public String getDescription() {
		return "Attacking " + target.getMessageName();
	}
}
