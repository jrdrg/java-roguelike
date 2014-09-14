package roguelike.actions.combat;

import roguelike.items.Weapon;

public enum DamageType {
	SLASHING,
	PIERCING,
	BLUNT;

	public int getTargetNumber(Weapon weapon) {
		return weapon.getTargetNumber(this);
	}

	public int getDamageRating(Weapon weapon) {
		return weapon.getDamageRating(this);
	}
}
