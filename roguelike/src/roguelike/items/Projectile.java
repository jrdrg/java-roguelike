package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.RangedAttack;
import roguelike.actors.Actor;

public class Projectile extends Weapon {
	private static final long serialVersionUID = -5440292549086531435L;

	protected Projectile() {
		super();
		this.stackable = true;
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Game.current().random().nextDouble() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new RangedAttack(attackDescription, totalDamage, this);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void onEquipped(Actor actor) {
	}

	@Override
	public void onRemoved(Actor actor) {
	}
}
