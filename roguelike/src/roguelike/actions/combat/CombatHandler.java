package roguelike.actions.combat;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import squidpony.squidcolor.SColor;

/**
 * Handles combat
 * 
 * some ideas: weapon reach should factor into this
 * 
 * for example, using a short weapon against a longer one should incur penalties
 * (amount of dice to roll, etc)
 * 
 * conversely, a shorter weapon might increase the action speed of the user
 * 
 * reach can be a number from 1-5, where 1:fists, 2:daggers, 3:swords,
 * 4:spears/polearms, 5: extra long weapons
 * 
 * the attack can hit a number of squares away equal to reach/2, rounded down,
 * with minimum of 1 - so reach 1-3 can hit the adjacent square, and 4-5 can hit
 * one square away
 * 
 * weapons with a reach greater than 1 square suffer penalties when used at
 * closer range (reach of 4 or 5)
 * 
 * when combatants use weapons of different reaches but the same number of
 * squares, the last weapon to cause damage inflicts penalties on the opponent
 * until the opponent causes damage (so polearms incur double penalties once the
 * user of the shorter weapon scores a hit)
 * 
 * 
 * @author john
 * 
 */
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

		// this will be based on some kind of successes method, where a number
		// of rolls are made against a target number (i.e. 1-10, target number
		// 6) and the number of successes are compared between the attacker and
		// defender, modified by anything applicable
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

		Game.current().displayMessage(message, color);

		return isDead;
	}
}
