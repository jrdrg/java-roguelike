package roguelike.actions.combat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import roguelike.util.Utility;
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
public class CombatHandler implements Serializable {
    private static final Logger LOG = LogManager.getLogger(CombatHandler.class);
    
	private static final long serialVersionUID = 1L;

	protected transient Game game = Game.current();
	private Actor actor;

	public CombatHandler(Actor actor) {
		this.actor = actor;
	}

	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		game = Game.current();
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
		Weapon weapon = actor.equipment().getEquippedWeapons().stream().filter(w -> w != null).findFirst().orElse(null);
		if (weapon != null) {
			weapon.onAttacking(actor, target);
			return weapon.getAttack();
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
		Statistics attackerStats = attacker.statistics();
		Statistics defenderStats = actor.statistics();

		int aWeaponProficiency = 3;
		int dWeaponProficiency = 3;
		int attackManeuver = 0; // modifier for different attack types, etc
		int armorValue = getArmorValue(); // armor value defender is wearing
		int defenseManeuver = 0; // modifier for defense like evade, dodge, etc

		int attackWeaponTN = 7; // TODO: get these from weapon data or maneuver
		int defendWeaponTN = getDefenderTargetNumber(attack);

		int attackSuccessPool = getAttackerSuccessPool(attackerStats, aWeaponProficiency, attackManeuver);
		int defendSuccessPool = getDefenderSuccessPool(defenderStats, dWeaponProficiency, defenseManeuver, attack);

		int reachDiff = determineReachDifference(attacker, attack);

		if (reachDiff > 0) {
			attackSuccessPool += reachDiff;
		} else if (reachDiff < 0) {
			defendSuccessPool += -reachDiff;
		}

		// This determines whether the attack landed or not
		int attackerSuccesses = DiceRolls.roll(attackSuccessPool, attackWeaponTN);
		int defenderSuccesses = DiceRolls.roll(defendSuccessPool, defendWeaponTN);

		int total = attackerSuccesses - defenderSuccesses;
		LOG.debug("S (A): {}. TN = {}. pool = {}", attackerSuccesses, attackWeaponTN, attackSuccessPool);
		LOG.debug("S (D): {}, TN = {}, pool = {}", defenderSuccesses, defendWeaponTN, defendSuccessPool);

		logCombatMessage(attacker.doAction("rolls %d (%d) successes against %s: total %d (%s)",
				attackerSuccesses,
				defenderSuccesses,
				actor.getMessageName(),
				total,
				(total > 0 ? "Hit" : "Miss")));

		if (total > 0) {

			int baseDamage = getTotalDamage(attacker, attack, armorValue, total);

			attack.baseDamage = baseDamage;
			return attack;
		}
		else {
			return new MeleeAttack("misses %s!", 0, attack.getWeapon());
		}
	}

	/**
	 * Called when an attack connects
	 * 
	 * @param attack
	 * @param attacker
	 * @return True if the attack killed the target
	 */
	public void onDamaged(Attack attack, Actor attacker) {
		actor.onDamaged(attack.baseDamage);
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
		game = Game.current();
		attack = target.combatHandler().defend(actor, attack);
		boolean isDead;
		if (attack.baseDamage > 0) {
			target.combatHandler().onDamaged(attack, actor);
			isDead = !target.isAlive();

			String message = getAttackMessage(target, attack);

			SColor color = SColor.ORANGE;
			if (Player.isPlayer(target))
				color = SColor.RED;

			game.displayMessage(actor.doAction(message), color);

			/* add an event so we can show an animation */
			game.addEvent(
					TurnEvent.attack(actor, target, "" + attack.getDamage(), attack));

		} else {
			isDead = false;
			game.displayMessage(actor.getMessageName() + " missed " + target.getMessageName() + ".", SColor.DARK_TAN);

			game.addEvent(TurnEvent.attackMissed(actor, target, "Missed"));
		}
		target.onAttacked(actor);

		// return true if this attack killed the target
		return isDead;
	}

	private int getAttackerSuccessPool(Statistics attackerStats, int aWeaponProficiency, int attackManeuver) {
		return attackerStats.baseMeleePool(aWeaponProficiency) + attackManeuver;
	}

	private int getDefenderSuccessPool(Statistics defenderStats, int dWeaponProficiency, int defenseManeuver, Attack attack) {
		if (attack instanceof RangedAttack) {
			return defenderStats.baseEvadePool();
		}
		return defenderStats.baseMeleePool(dWeaponProficiency) + defenseManeuver;
	}

	private int getDefenderTargetNumber(Attack attack) {
		if (attack instanceof RangedAttack)
			return 8; // TODO: change this?

		Weapon firstWeapon = actor.equipment().getEquippedWeapons().stream().filter(w -> w != null).findFirst().orElse(null);
		if (firstWeapon != null)
			return firstWeapon.getDefenseTargetNumber();

		return 8; // default TN with no weapon
	}

	private int determineReachDifference(Actor attacker, Attack attack) {

		Weapon defendingWeapon = ItemSlot.RIGHT_HAND.getEquippedWeapon(actor);

		int attackingReach = attack.getWeapon().reach();
		int defendingReach = defendingWeapon == null ? 0 : defendingWeapon.reach();
		int reachDiff = attackingReach - defendingReach;

		// TODO: need to switch this penalty to the combatant who was damaged most recently
		// TODO: maybe can use a Condition for this? with a duration of 1 turn that removes itself when an attack hits

		if (attacker.wasAttackedThisRound()) {
			reachDiff *= -1;
			logCombatMessage(String.format("Reach advantage applied to %s", actor.getMessageName()));
		}
		else if (actor.wasAttackedThisRound()) {

		}

		LOG.debug("Attacker reach: {}", attackingReach);
		LOG.debug("Defender reach: {}", defendingReach);

		logCombatMessage(String.format("%s has reach of %d, %s has reach of %d",
				attacker.getMessageName(), attackingReach, actor.getMessageName(), defendingReach));
		return reachDiff;
	}

	private int getTotalDamage(Actor attacker, Attack attack, int armorValue, int total) {

		// determine damage of the attack based on how successful it was and the stats of attacker/defender
		int baseDamage = total + attack.getWeapon().getDamageRating(attack.getDamageType());

		baseDamage += (attacker.statistics().toughness.getTotalValue() / 2.0f);
		baseDamage -= (actor.statistics().toughness.getTotalValue() / 2.0f);

		// TODO: get armor here
		baseDamage -= armorValue;
		return baseDamage;
	}

	private void logCombatMessage(String message) {
		Game.current().messages().add(new MessageDisplayProperties(message));
	}

	private String getAttackMessage(Actor target, Attack attack) {
		int targetHealth = target.health().getCurrent();

		String message = "";
		try {
			String attackDescription = String.format(attack.description, target.getMessageName());
			message = String.format("%s for %d %s damage", attackDescription, attack.baseDamage, attack.damageType);
			if (targetHealth > 0)
				message += ". (" + targetHealth + " left)";
			else
				message += (". " + Utility.capitalizeFirstLetter(target.getMessageName() + " is dead."));
		} catch (Exception e) {
			message = "ERROR: " + attack.description;
		}
		return message;
	}

	private int getArmorValue() {
		return 0;
	}
}
