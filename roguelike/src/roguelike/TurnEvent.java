package roguelike;

import roguelike.actors.Actor;

public class TurnEvent {
	private Actor initiator;
	private Actor target;
	private String message;

	public static int ATTACKED = 1;

	private int type;

	private TurnEvent(Actor initiator, Actor target, int type) {
		this.initiator = initiator;
		this.target = target;
		this.type = type;
	}

	public static TurnEvent Attack(Actor initiator, Actor target, String message) {
		return new TurnEvent(initiator, target, ATTACKED).setMessage(message);
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

	private TurnEvent setMessage(String message) {
		this.message = message;
		return this;
	}
}
