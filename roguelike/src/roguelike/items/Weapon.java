package roguelike.items;

import roguelike.actions.combat.Attack;
import roguelike.data.WeaponData;

public abstract class Weapon extends Item {
	public static final Weapon DEFAULT = new Weapon(WeaponData.DEFAULT) {

		@Override
		public String getDescription() {
			return "Null";
		}

		@Override
		public Attack getAttack() {
			return null;
		}
	};

	protected Weapon(WeaponData data) {
		super();

		this.reach = data.reach;
	}

	public abstract Attack getAttack();

	public final int reach;
}
