package roguelike.items;

import roguelike.actions.combat.Attack;
import squidpony.squidcolor.SColor;

public abstract class Weapon extends Item {

	public abstract Attack getAttack();

	@Override
	public char getSymbol() {
		return '?';
	}

	@Override
	public SColor getColor() {
		return SColor.WHITE;
	}
}
