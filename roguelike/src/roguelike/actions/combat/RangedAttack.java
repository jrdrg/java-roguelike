package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Weapon;

public class RangedAttack extends Attack {

	protected RangedAttack(String description, int baseDamage, Weapon weapon) {
		super(description, baseDamage, weapon);
	}

	@Override
	public boolean perform(Action action, Actor target) {
		return action.getActor().getCombatHandler().processAttack(action, this, target);
	}

}
