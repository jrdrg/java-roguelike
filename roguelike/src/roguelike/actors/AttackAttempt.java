package roguelike.actors;

import java.io.Serializable;

public class AttackAttempt implements Serializable {
	private static final long serialVersionUID = 1L;

	private Actor actor;

	public AttackAttempt(Actor actor) {
		this.actor = actor;
	}

	public Actor getActor() {
		return actor;
	}
}
