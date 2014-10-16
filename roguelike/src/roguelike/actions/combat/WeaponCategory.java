package roguelike.actions.combat;

public enum WeaponCategory {
	SWORD,
	AXE,
	DAGGER,
	CLUB,
	SPEAR,
	POLEARM,
	BOW,
	THROWN,
	GUN,
	ARROW,
	BULLET,

	NATURAL, // for monsters, beasts, etc
	DEFAULT;

	public static WeaponCategory fromString(String type) {
		return WeaponCategory.valueOf(type.toUpperCase());
	}
}
