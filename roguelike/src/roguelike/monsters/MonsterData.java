package roguelike.monsters;

import roguelike.items.Weapon;
import squidpony.squidcolor.SColor;

public class MonsterData {

	public char symbol;

	public SColor color;

	public String name;

	public int speed;

	public Weapon defaultWeapon;

	public MonsterData(char symbol, SColor color, String name) {
		this.symbol = symbol;
		this.color = color;
		this.name = name;
	}
}
