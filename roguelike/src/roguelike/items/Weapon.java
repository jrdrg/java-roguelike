package roguelike.items;

import roguelike.actions.combat.Attack;

public abstract class Weapon extends Item {

	public abstract Attack getAttack();
}
