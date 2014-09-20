package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.RangedWeapon;
import roguelike.items.Weapon;

public class RangedAttack extends Attack {

	public RangedAttack(String description, int baseDamage, RangedWeapon weapon) {
		super(description, baseDamage, weapon);
	}

	@Override
	public boolean perform(Action action, Actor target) {
		return action.getActor().getCombatHandler().processAttack(action, this, target);
	}

}
