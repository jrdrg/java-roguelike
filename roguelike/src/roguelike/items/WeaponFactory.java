package roguelike.items;

import roguelike.data.DataFactory;
import roguelike.data.WeaponData;

public class WeaponFactory {

	public static Weapon create(WeaponData data) {
		if (isMeleeWeapon(data)) {
			MeleeWeapon mWpn = new MeleeWeapon(data);
			return mWpn;
		}
		return null;
	}

	public static Weapon create(String key) {
		WeaponData data = DataFactory.instance().getWeapon(key);
		if (data != null) {
			return create(data);
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
