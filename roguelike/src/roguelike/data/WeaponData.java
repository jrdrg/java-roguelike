package roguelike.data;

import squidpony.squidcolor.SColor;

public class WeaponData {

	public String name;

	public char symbol;

	public SColor color;

	public String type;

	public int baseDamage;

	public int reach;

	public String attackDescription;

	public boolean droppable;

	public WeaponData(String name, int baseDamage, int reach, boolean droppable, String type, String attackDescription) {
		this.name = name;
		this.baseDamage = baseDamage;
		this.reach = reach;
		this.droppable = droppable;
		this.type = type;
		this.attackDescription = attackDescription;
	}
}
