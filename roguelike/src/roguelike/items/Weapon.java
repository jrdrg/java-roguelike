package roguelike.items;

import java.util.HashMap;
import java.util.Map;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponType;
import roguelike.data.WeaponData;

public abstract class Weapon extends Item {
	private static final long serialVersionUID = -7712813593206574664L;

	private class DamageValue implements Comparable<DamageValue> {
		public DamageType type;
		public int targetNumber;
		public int damageRating;

		public DamageValue(DamageType type, int targetNumber, int damageRating) {
			this.type = type;
			this.targetNumber = targetNumber;
			this.damageRating = damageRating;

			if (this.targetNumber <= 0) {
				this.targetNumber = 10; // max value
			}
		}

		@Override
		public int compareTo(DamageValue o) {
			float f = targetNumber;
			float of = o.targetNumber;

			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	protected DamageType defaultDamageType;
	private Map<DamageType, DamageValue> damageValues;
	private WeaponType weaponType;

	protected Weapon(WeaponData data) {
		super();

		this.damageValues = new HashMap<DamageType, DamageValue>();
		damageValues.put(DamageType.SLASHING, new DamageValue(DamageType.SLASHING, data.slashTargetNumber, data.slashDamageRating));
		damageValues.put(DamageType.PIERCING, new DamageValue(DamageType.PIERCING, data.thrustTargetNumber, data.thrustDamageRating));
		damageValues.put(DamageType.BLUNT, new DamageValue(DamageType.BLUNT, data.bluntTargetNumber, data.bluntDamageRating));

		DamageType sortedDv = damageValues
				.values()
				.stream()
				.filter((DamageValue dv) -> dv.targetNumber > 0)
				.sorted((DamageValue d1, DamageValue d2) -> {
					if (d1.targetNumber < d2.targetNumber)
						return -1;
					if (d1.targetNumber > d2.targetNumber)
						return 1;
					if (d1.damageRating > d2.damageRating)
						return -1;
					if (d1.damageRating < d2.damageRating)
						return 1;
					return 0;
				})
				.findFirst().get().type;

		defaultDamageType = sortedDv;

		this.reach = data.reach;
		this.weaponType = WeaponType.fromString(data.type);
	}

	public WeaponType weaponType() {
		return this.weaponType;
	}

	public DamageType defaultDamageType() {
		return defaultDamageType;
	}

	public int getTargetNumber(DamageType type) {
		return damageValues.get(type).targetNumber;
	}

	public int getDamageRating(DamageType type) {
		return damageValues.get(type).damageRating;
	}

	public abstract Attack getAttack();

	public final int reach;
}
