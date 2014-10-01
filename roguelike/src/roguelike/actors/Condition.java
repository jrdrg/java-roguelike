package roguelike.actors;

public abstract class Condition {

	protected String identifier;
	protected Actor actor;
	protected int duration;

	protected Condition(String identifier, Actor actor, int duration) {
		this.identifier = identifier;
		this.actor = actor;
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public final boolean process() {
		if (duration > 0) {
			duration--;
			onProcess();
		}
		return duration <= 0;
	}

	protected abstract void onProcess();
}
