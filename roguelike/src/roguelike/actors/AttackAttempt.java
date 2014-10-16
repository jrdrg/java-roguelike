package roguelike.actors;

import java.io.Serializable;

public class AttackAttempt implements Serializable {
	private static final long serialVersionUID = -5168348162191174142L;

	private Actor actor;

	public AttackAttempt(Actor actor) {
		this.actor = actor;
	}

	public Actor getActor() {
		return actor;
	}
}
