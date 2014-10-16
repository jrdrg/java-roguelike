package roguelike.items;

public enum WeaponType {
	FISTS("fist"),

	DAGGER("dagger"),
	STILETTO("stiletto"),

	AXE("axe"),

	SHORT_SWORD("short sword"),
	LONG_SWORD("long sword"),
	GREATSWORD("greatsword"),
	CUTLASS("cutlass"),
	SMALLSWORD("smallsword"),
	RAPIER("rapier"),

	SPEAR("spear"),
	PIKE("pike"),
	LANCE("lance"),
	JAVELIN("javelin"),
	HALBERD("halberd"),

	SHORT_BOW("short bow"),
	LONG_BOW("long bow"),
	COMPOUND_BOW("compound bow"),
	CROSSBOW("crossbow"),

	MUSKET("musket"),
	PISTOL("pistol"),
	BLUNDERBUSS("blunderbuss"),

	ARROW("arrow"),
	BULLET("bullet"),

	EXPLOSIVE("explosive grenade"),

	BITE("bite"),
	MANDIBLES("mandibles"),
	CLAWS("claws");

	public final String name;

	WeaponType(String name) {
		this.name = name;
	}
}
