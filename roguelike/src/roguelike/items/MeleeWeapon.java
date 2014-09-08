package roguelike.items;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.MeleeAttack;

public class MeleeWeapon extends Weapon {

	protected int baseDamage;
	protected String name;
	protected String description;
	protected String attackDescription;

	public MeleeWeapon(String name, String description, int baseDamage) {
		super();
		this.name = name;
		this.description = description;
		this.baseDamage = baseDamage;

		this.attackDescription = "%s swings " + getName() + " at %s";
	}

	public MeleeWeapon(String name, String description, int baseDamage, String attackDescription) {
		this(name, description, baseDamage);
		this.attackDescription = attackDescription;
	}

	@Override
	public Attack getAttack() {

		double randomFactor = Math.random() * baseDamage;
		int totalDamage = (int) (baseDamage + randomFactor / 2);

		return new MeleeAttack(attackDescription, totalDamage);
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
