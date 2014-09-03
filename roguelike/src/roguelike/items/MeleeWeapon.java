package roguelike.items;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.MeleeAttack;

public class MeleeWeapon extends Weapon {

	protected int baseDamage;
	protected String name;
	protected String description;

	public MeleeWeapon(String name, String description, int baseDamage) {
		this.name = name;
		this.description = description;
		this.baseDamage = baseDamage;
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Math.random() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new MeleeAttack("%s swings " + getName() + " at %s", totalDamage);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
