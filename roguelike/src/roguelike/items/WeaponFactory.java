package roguelike.items;

import roguelike.data.DataFactory;
import roguelike.data.WeaponData;

public class WeaponFactory {

	public static Weapon create(WeaponData data) {
		if (isMeleeWeapon(data)) {
			MeleeWeapon mWpn = new MeleeWeapon(data);
			return mWpn;
		} else if (isAmmunition(data)) {
			Ammunition ammo = new Ammunition(data);
			return ammo;
		} else {
			RangedWeapon rWpn = new RangedWeapon(data);
			return rWpn;
		}
	}

	public static Weapon create(String key) {
		WeaponData data = DataFactory.instance().getWeapon(key);
		if (data != null) {
			return create(data);
		}
		return null;
	}

	private static boolean isMeleeWeapon(WeaponData data) {
		if (!data.type.equals("bow")) {
			return true;
		}
		return false;
	}

	private static boolean isAmmunition(WeaponData data) {
		if (data.type.equals("arrow") || data.type.equals("bullet")) {
			return true;
		}
		return false;
	}
}
