package roguelike.actors.behaviors;

import roguelike.actors.Actor;
import roguelike.items.Weapon;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.BasicRadiusStrategy;

public abstract class EnemyBehavior extends Behavior {
	private static final long serialVersionUID = 1L;

	protected Behavior nextBehavior;

	protected EnemyBehavior(Actor actor) {
		super(actor);
	}

	@Override
	public boolean isHostile() {
		return true;
	}

	protected boolean canAttackTarget(float distance) {
		int range = 1;
		Weapon maxRange = actor.equipment().getEquippedWeapons().stream().max((w1, w2) -> {
			if (w1 == null || w2 == null)
				return 0;
			return Integer.compare(w1.reach(), w2.reach());
		}).orElse(null);

		if (maxRange != null)
			range = maxRange.getReachInTiles();

		return (distance <= range);
	}

	protected boolean canAttackTarget(Actor target) {
		Coordinate actorPos = this.actor.getPosition();
		Coordinate targetPos = target.getPosition();

		return canAttackTarget(targetPos.distance(actorPos, BasicRadiusStrategy.CIRCLE));
	}
}
