package roguelike.items;

import java.util.HashMap;
import java.util.Map;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.DamageType;
import roguelike.data.WeaponData;
import squidpony.squidutility.Pair;

public abstract class Weapon extends Item {

	private static final long serialVersionUID = -7712813593206574664L;

	public static final Weapon DEFAULT = new Weapon(WeaponData.DEFAULT) {
		private static final long serialVersionUID = 1L;

		@Override
		public String getDescription() {
			return "Null";
		}

		@Override
		public Attack getAttack() {
			return null;
		}
	};

	private Map<DamageType, Pair<Integer, Integer>> damageValues;

	protected DamageType defaultDamageType;

	protected Weapon(WeaponData data) {
		super();

		this.damageValues = new HashMap<DamageType, Pair<Integer, Integer>>();
		damageValues.put(DamageType.SLASHING, new Pair<Integer, Integer>(data.slashTargetNumber, data.slashDamageRating));
		damageValues.put(DamageType.PIERCING, new Pair<Integer, Integer>(data.thrustTargetNumber, data.thrustDamageRating));
		damageValues.put(DamageType.BLUNT, new Pair<Integer, Integer>(data.bluntTargetNumber, data.bluntDamageRating));

		this.reach = data.reach;

		defaultDamageType = DamageType.BLUNT;
		if (data.slashTargetNumber >= data.thrustTargetNumber && data.slashTargetNumber >= data.bluntTargetNumber) {
			defaultDamageType = DamageType.SLASHING;
		} else if (data.thrustTargetNumber >= data.slashTargetNumber && data.thrustTargetNumber >= data.bluntTargetNumber) {
			defaultDamageType = DamageType.PIERCING;
		} else if (data.bluntTargetNumber >= data.thrustTargetNumber && data.bluntTargetNumber >= data.slashTargetNumber) {
			defaultDamageType = DamageType.BLUNT;
		}
	}

	public DamageType getDefaultDamageType() {
		return defaultDamageType;
	}

	public int getTargetNumber(DamageType type) {
		return damageValues.get(type).getFirst();
	}

	public int getDamageRating(DamageType type) {
		return damageValues.get(type).getSecond();
	}

	public abstract Attack getAttack();

	public final int reach;
}
