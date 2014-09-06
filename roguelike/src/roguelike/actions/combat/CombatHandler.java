package roguelike.actions.combat;

import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import squidpony.squidcolor.SColor;

public class CombatHandler {
	private Actor actor;

	public CombatHandler(Actor actor) {
		this.actor = actor;
	}

	/**
	 * Returns an attack that the actor will use to try and hit the target
	 * 
	 * @param target
	 * @return
	 */
	public Attack getAttack(Actor target) {
		// TODO: implement attack choosing behavior, etc
		// TODO: dual wielding should return 1 attack dependent on skill
		Weapon weapon = actor.getEquipment().getEquippedWeapon(ItemSlot.RIGHT_ARM);
		if (weapon != null) {
			return weapon.getAttack();
		}
		return new MeleeAttack("%s punches %s", 10);
	}

	/**
	 * Allows the defender to modify the attack depending on armor, stats,
	 * effects, etc
	 * 
	 * @param attacker
	 * @param attack
	 * @return
	 */
	public Attack defend(Actor attacker, Attack attack) {
		// TODO: implement defending behavior, resistances, etc
		return attack;
	}

	/**
	 * Processes the attack after the target has defended it
	 * 
	 * @param action
	 * @param attack
	 * @param target
	 * @return The result from onDamaged() - true if the attack killed the
	 *         target
	 */
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
	 * @param attacker
	 * @return True if the attack killed the target
	 */
	public boolean onDamaged(Attack attack, Actor attacker) {
		boolean isDead = actor.getHealth().damage(attack.baseDamage);

		String attackDescription = String.format(attack.description, attacker.getName(), actor.getName());
		String message = String.format("%s for %d damage!", attackDescription, attack.baseDamage);
		message += "(" + actor.getHealth().getCurrent() + " left)";

		SColor color = SColor.ORANGE;
		if (Player.isPlayer(actor))
			color = SColor.RED;

		actor.getGame().displayMessage(message, color);

		return isDead;
	}
}
