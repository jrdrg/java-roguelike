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
 * weapons with a reach greater than 1 suffer penalties when used at closer
 * range, i.e. polearms
 * 
 * when comparing i.e. a sword to a dagger, both have a reach of 1 square but
 * the sword is obviously longer, need to find a way to model that
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
