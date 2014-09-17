package roguelike.actors;

public abstract class Condition {

	protected Actor actor;
	protected int duration;

	protected Condition(Actor actor, int duration) {
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
