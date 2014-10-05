package roguelike.actors;

import roguelike.util.StringEx;

public abstract class Condition {

	protected StringEx identifier;
	protected Actor actor;
	protected int duration;

	protected Condition(StringEx identifier, Actor actor, int duration) {
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

	public StringEx getIdentifier() {
		return identifier;
	}

	protected abstract void onProcess();
}
