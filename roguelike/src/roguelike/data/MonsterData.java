package roguelike.data;

import squidpony.squidcolor.SColor;

public class MonsterData {

	public char symbol;

	public SColor color;

	public String name;

	public int speed;

	public String behavior;

	public String weapon;

	public MonsterData(char symbol, SColor color, String name) {
		this.symbol = symbol;
		this.color = color;
		this.name = name;
	}

}
