package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import squidpony.squidcolor.SColor;

public class CombatHandler {
	private Actor actor;

	public CombatHandler(Actor actor) {
		this.actor = actor;
	}

	public Attack getAttack(Actor target) {
		// TODO: implement attack choosing behavior, etc
		Weapon weapon = actor.getEquipment().getEquippedWeapon(ItemSlot.RIGHT_ARM);
		if (weapon != null) {
			return weapon.getAttack();
		}
		return new MeleeAttack("punches", 10);
	}

	public Attack defend(Actor attacker, Attack attack) {
		// TODO: implement defending behavior, resistances, etc
		return attack;
	}

	public boolean processAttack(Action action, Attack attack, Actor target) {
		attack = target.getCombatHandler().defend(actor, attack);
		boolean isDead = target.getCombatHandler().onDamaged(attack, actor);

		target.onAttacked(actor);

		// return true if this attack killed the target
		return isDead;
	}

	/**
	 * Called when an attack connects
	 * 
	 * @param attack
	 * @return True if the attack killed the target
	 */
	public boolean onDamaged(Attack attack, Actor attacker) {
		boolean isDead = actor.getHealth().damage(attack.baseDamage);

		String message = String.format("%s %s %s for %d damage!", attacker.getName(), attack.description, actor.getName(), attack.baseDamage);
		message += "(" + actor.getHealth().getCurrent() + " left)";

		actor.getGame().displayMessage(message, SColor.RED);

		return isDead;
	}
}
