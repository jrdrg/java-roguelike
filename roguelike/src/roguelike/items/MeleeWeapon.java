package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.MeleeAttack;

public class MeleeWeapon extends Weapon {

	private static final long serialVersionUID = 682348343481995648L;

	protected MeleeWeapon() {
		super();
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Game.current().random().nextDouble() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new MeleeAttack(attackDescription, totalDamage, this);
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
