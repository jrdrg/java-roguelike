package roguelike.actions.combat;

import roguelike.actors.Actor;

public abstract class Attack {

	protected String description;
	protected int baseDamage;

	protected Attack(String description, int baseDamage) {
		this.description = description;
		this.baseDamage = baseDamage;
	}

	public int getDamage() {
		return baseDamage;
	}

	public abstract boolean perform(Actor target);

	// TODO: implement various types of attacks
	/*
	 * BEAT - removes a shield/defensive weapon
	 * 
	 * WIDE SWING - hits area in 180 degrees toward target
	 * 
	 * LUNGE - can attack 2 spaces forward
	 * 
	 * CIRCULAR SWING - can hit 360 degrees around attacker
	 * 
	 * FEINT - attack has a chance to not be defended depending on defense of
	 * target area (or if using separate dmg for head/torso/arms/etc, apply
	 * defense against the part being feinted against and the attack has a
	 * chance to land somewhere else ignoring defense entirely)
	 * 
	 * DISARM - causes opponent to drop weapon
	 * 
	 * PINPOINT STRIKE - thrusting weapon, causes more damage but needs more
	 * accuracy
	 * 
	 * STUNNING BLOW - causes opponent to lose a turn
	 * 
	 * DOUBLE ATTACK - make 2 attacks determined by dual wielding skill
	 * 
	 * EVADE - make a weak attack and simultaneously move backward to avoid the
	 * counterattack
	 * 
	 * TRIP - cause the opponent to lose a turn and also the ability to defend
	 * until they get up
	 * 
	 * DISTRACTION - cause opponent's defense to be lowered against the next
	 * attack
	 * 
	 * COUNTERATTACK - defend or make a weak attack, but make a stronger attack
	 * in response to the next opposing attack
	 */
}
