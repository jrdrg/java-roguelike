package roguelike.monsters;

import roguelike.actors.Actor;
import roguelike.actors.behaviors.Behavior;
import roguelike.items.Weapon;
import roguelike.util.Factory;
import squidpony.squidcolor.SColor;

public class MonsterData {

	public char symbol;

	public SColor color;

	public String name;

	public int speed;

	public String behavior;

	public String weapon;

	public Factory<Weapon> defaultWeapon;

	public Factory<Behavior> defaultBehavior;

	public MonsterData(char symbol, SColor color, String name) {
		this.symbol = symbol;
		this.color = color;
		this.name = name;
		this.defaultBehavior = new Factory<Behavior>() {

			@Override
			public Behavior create(Actor actor) {
				// TODO : add create()
				return null;
			}
		};
	}

}
