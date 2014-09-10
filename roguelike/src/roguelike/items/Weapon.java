package roguelike.items;

import roguelike.actions.combat.Attack;

public abstract class Weapon extends Item {

	protected Weapon() {
		super();
	}

	public abstract Attack getAttack();

}
