package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Weapon;

public class MeleeAttack extends Attack {

	public MeleeAttack(String description, int baseDamage, Weapon weapon) {
		super(description, baseDamage, weapon);
	}

	@Override
	public boolean onPerform(Action action, Actor target) {
		return action.getActor().getCombatHandler().processAttack(action, this, target);
	}

}
