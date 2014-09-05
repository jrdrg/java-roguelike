package roguelike.actions.combat;

import roguelike.actors.Actor;

public abstract class Attack {

	protected String description;
	protected int baseDamage;

	protected Attack(String description, int baseDamage) {
		this.description = description;
		this.baseDamage = baseDamage;
	}

	public int getDamage() {
		return baseDamage;
	}

	public abstract boolean perform(Actor target);
}
