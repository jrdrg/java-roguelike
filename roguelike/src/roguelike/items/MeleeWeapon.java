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
		return new MeleeAttack("uses " + getName() + " on", baseDamage);
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
