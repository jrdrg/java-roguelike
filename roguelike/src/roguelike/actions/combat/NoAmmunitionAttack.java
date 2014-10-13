package roguelike.actions.combat;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.RangedWeapon;
import squidpony.squidcolor.SColor;

public class NoAmmunitionAttack extends Attack {

	public NoAmmunitionAttack(int baseDamage, RangedWeapon weapon) {
		super("does not have any " + weapon.ammunitionType() + "s ready with which to shoot the %s", baseDamage, weapon);
	}

	@Override
	protected boolean onPerform(Action action, Actor target) {
		Actor actor = action.getActor();
		actor.behavior().onNoAmmunition();
		Game.current().displayMessage(actor.doAction("has no %ss readied.", ((RangedWeapon) weapon).ammunitionType()), SColor.GRAPE_MOUSE);
		return false;
	}

}
