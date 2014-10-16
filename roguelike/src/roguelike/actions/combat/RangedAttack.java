package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Projectile;

public class RangedAttack extends Attack {

	public RangedAttack(String description, int baseDamage, Projectile weapon) {
		super(description, baseDamage, weapon);
	}

	@Override
	public boolean onPerform(Action action, Actor target) {
		return action.getActor().combatHandler().processAttack(action, this, target);
	}

}
