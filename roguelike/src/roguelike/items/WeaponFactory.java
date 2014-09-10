package roguelike.items;

import roguelike.data.WeaponData;

public class WeaponFactory {

	public static Weapon create(WeaponData data) {
		if (isMeleeWeapon(data)) {
			MeleeWeapon mWpn = new MeleeWeapon(data.name, data.name, data.baseDamage, data.attackDescription);
			mWpn.droppable = data.droppable;
			mWpn.symbol = data.symbol;
			mWpn.color = data.color;

			return mWpn;
		}
		return null;
	}

	private static boolean isMeleeWeapon(WeaponData data) {
		if (!data.type.equals("ranged")) {
			return true;
		}
		return false;
	}
}
