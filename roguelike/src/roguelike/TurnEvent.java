package roguelike;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.RangedAttack;
import roguelike.actors.Actor;
import roguelike.ui.animations.Animation;

public class TurnEvent {
	public static final int ATTACKED = 1;
	public static final int ATTACK_MISSED = 2;
	public static final int RANGED_ATTACKED = 3;

	private Actor initiator;
	private Actor target;
	private String message;
	private Animation animation;

	private int type;

	private TurnEvent(Actor initiator, Actor target, int type) {
		this.initiator = initiator;
		this.target = target;
		this.type = type;
	}

	public static TurnEvent attack(Actor initiator, Actor target, String message, Attack attack) {
		if (attack instanceof RangedAttack)
			return new TurnEvent(initiator, target, RANGED_ATTACKED).setMessage(message);

		return new TurnEvent(initiator, target, ATTACKED).setMessage(message);
	}

	public static TurnEvent attackMissed(Actor initiator, Actor target, String message) {
		return new TurnEvent(initiator, target, ATTACK_MISSED).setMessage(message);
	}

	public static TurnEvent rangedAttack(Actor initiator, Actor target, String message) {
		return new TurnEvent(initiator, target, RANGED_ATTACKED).setMessage(message);
	}

	public int getType() {
		return type;
	}

	public Actor getInitiator() {
		return initiator;
	}

	public Actor getTarget() {
		return target;
	}

	public String getMessage() {
		return message;
	}

	public TurnEvent setAnimation(Animation animation) {
		this.animation = animation;
		return this;
	}

	private TurnEvent setMessage(String message) {
		this.message = message;
		return this;
	}
}
