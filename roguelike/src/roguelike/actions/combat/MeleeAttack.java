package roguelike.actions.combat;

import roguelike.actors.Actor;

public class MeleeAttack extends Attack {

	public MeleeAttack(String description, int baseDamage) {
		super(description, baseDamage);
	}

	@Override
	public boolean perform(Actor target) {

		// if we miss, return false
		// TODO: need to make this take stats into account, etc
		if (Math.random() > 0.6)
			return false;

		// TODO: make damage also take stats into account
		return target.getCombatHandler().processAttack(null, this, target);
	}

}
