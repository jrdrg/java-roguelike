package roguelike.actors;

public class AttackAttempt {
	private Actor actor;

	public AttackAttempt(Actor actor) {
		this.actor = actor;
	}

	public Actor getActor() {
		return actor;
	}
}
