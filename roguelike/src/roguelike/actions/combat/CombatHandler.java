package roguelike.actions.combat;

import roguelike.Game;
import roguelike.MessageDisplayProperties;
import roguelike.TurnEvent;
import roguelike.actions.Action;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.actors.Statistics;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Weapon;
import roguelike.util.DiceRolls;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;

/**
 * Handles combat
 * 
 * some ideas: weapon reach should factor into this
 * 
 * for example, using a short weapon against a longer one should incur penalties (amount of dice to roll, etc)
 * 
 * conversely, a shorter weapon might increase the action speed of the user
 * 
 * reach can be a number from 1-5, where 1:fists, 2:daggers, 3:swords, 4:spears/polearms, 5: extra long weapons
 * 
 * the attack can hit a number of squares away equal to reach/2, rounded down, with minimum of 1 - so reach 1-3 can hit
 * the adjacent square, and 4-5 can hit one square away
 * 
 * weapons with a reach greater than 1 square suffer penalties when used at closer range (reach of 4 or 5)
 * 
 * when combatants use weapons of different reaches but the same number of squares, the last weapon to cause damage
 * inflicts penalties on the opponent until the opponent causes damage (so polearms incur double penalties once the user
 * of the shorter weapon scores a hit)
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
		Weapon[] weapon = actor.getEquipment().getEquippedWeapons();
		if (weapon[0] != null) {
			return weapon[0].getAttack();
		}
		return null;
	}

	/**
	 * Resolves the attack, and returns either the original attack or a modified attack taking into account the
	 * defender's stats, resistances, etc
	 * 
	 * @param attacker
	 * @param attack
	 * @return
	 */
	public Attack defend(Actor attacker, Attack attack) {
		// TODO: implement defending behavior, resistances, etc

		int aWeaponProficiency = 3;
		int dWeaponProficiency = 3;
		int attackManeuver = 0; // modifier for different attack types, etc
		int armorValue = 0; // armor value defender is wearing
		int defenseManeuver = 0; // modifier for defense like evade, dodge, etc

		int attackWeaponTN = 7; // TODO: get these from weapon data or maneuver
		int defendWeaponTN = 7;

		Statistics attackerStats = attacker.getStatistics();
		Statistics defenderStats = actor.getStatistics();

		int attackSuccessPool = attackerStats.baseMeleePool(aWeaponProficiency) + attackManeuver;
		int defendSuccessPool = defenderStats.baseMeleePool(dWeaponProficiency) + defenseManeuver;

		// TODO: need to switch this penalty to the combatant who was damaged most recently
		// TODO: maybe can use a Condition for this? with a duration of 1 turn that removes itself when an attack hits

		Weapon defendingWeapon = ItemSlot.RIGHT_ARM.getEquippedWeapon(actor);
		int attackingReach = attack.getWeapon().reach;
		int defendingReach = defendingWeapon == null ? 0 : defendingWeapon.reach;
		int reachDiff = attackingReach - defendingReach;
		if (attacker.wasAttackedThisRound()) {
			reachDiff *= -1;
			logCombatMessage(String.format("Reach advantage applied to %s", actor.getName()));
		}
		else if (actor.wasAttackedThisRound()) {

		}

		if (reachDiff > 0) {
			attackSuccessPool += reachDiff;
		} else if (reachDiff < 0) {
			defendSuccessPool += -reachDiff;
		}

		Log.debug("Attacker reach: " + attackingReach);
		Log.debug("Defender reach: " + defendingReach);

		logCombatMessage(String.format("%s has reach of %d, %s has reach of %d",
				attacker.getName(), attackingReach, actor.getName(), defendingReach));

		// This determines whether the attack landed or not
		int attackerSuccesses = DiceRolls.roll(attackSuccessPool, attackWeaponTN);
		int defenderSuccesses = DiceRolls.roll(defendSuccessPool, defendWeaponTN);

		int total = attackerSuccesses - defenderSuccesses;
		Log.debug("S (A): " + attackerSuccesses + ", TN=" + attackWeaponTN + ", pool=" + attackSuccessPool);
		Log.debug("S (D): " + defenderSuccesses + ", TN=" + defendWeaponTN + ", pool=" + defendSuccessPool);

		logCombatMessage(String.format("%s rolls %d successes, %s rolls %d: total %d (%s)",
				attacker.getName(), attackerSuccesses, actor.getName(), defenderSuccesses, total, (total > 0 ? "Hit" : "Miss")));

		if (total > 0) {

			// Currently just return the original attack - need to take other stuff into account later

			attack.baseDamage -= armorValue; // placeholder logic here
			return attack;
		}
		else {
			return new MeleeAttack("%s misses %s!", 0, attack.getWeapon());
		}
	}

	/**
	 * Processes the attack after the target has defended it
	 * 
	 * @param action
	 * @param attack
	 * @param target
	 * @return The result from onDamaged() - true if the attack killed the target
	 */
	boolean processAttack(Action action, Attack attack, Actor target) {
		attack = target.getCombatHandler().defend(actor, attack);
		boolean isDead;
		if (attack.baseDamage > 0) {
			isDead = target.getCombatHandler().onDamaged(attack, actor);

			/* add an event so we can show an animation */
			Game.current().addEvent(TurnEvent.attack(actor, target, "" + attack.getDamage()));

		} else {
			isDead = false;
			Game.current().displayMessage(actor.getName() + " missed " + target.getName() + "!", SColor.DARK_TAN);

			Game.current().addEvent(TurnEvent.attackMissed(actor, target, "Missed"));
		}
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

	private void logCombatMessage(String message) {
		Game.current().messages().add(new MessageDisplayProperties(message));
	}
}
