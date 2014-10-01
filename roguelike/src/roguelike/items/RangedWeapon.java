package roguelike.items;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actions.combat.RangedAttack;
import roguelike.data.WeaponData;

public class RangedWeapon extends Weapon {

	private static final long serialVersionUID = -340930226160562519L;

	protected int maxRange;
	protected boolean requiresAmmunition;

	protected RangedWeapon(WeaponData data) {
		super(data);

		// TODO: fix attack descriptions
		if (data.attackDescription == null || data.attackDescription.length() == 0) {
			this.attackDescription = "%s shoots with " + getName() + " at %s";
		} else {
			this.attackDescription = data.attackDescription;
		}
		// TODO: add actual max range value here
		this.maxRange = 15;

		this.requiresAmmunition = data.requiresAmmunition;
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
	public String getName() {
		return name;
	}
}
