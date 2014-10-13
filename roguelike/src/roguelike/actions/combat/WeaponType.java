package roguelike.actions.combat;

public enum WeaponType {
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

	public static WeaponType fromString(String type) {
		return WeaponType.valueOf(type.toUpperCase());
	}
}
