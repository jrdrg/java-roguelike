package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;

public abstract class Condition {

	protected StringEx identifier;
	protected int duration, initialDuration;

	protected Condition(StringEx identifier, int duration) {
		this.identifier = identifier;
		this.duration = duration;
		this.initialDuration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public final boolean process(Actor actor) {
		if (duration > 0) {
			duration--;
			onProcess(actor);
		}
		if (duration == 0) {
			onConditionRemoved(actor);
			return true;
		}
		return false;
	}

	public StringEx identifier() {
		return identifier;
	}

	public void onConditionAdded(Actor actor) {
	}

	protected abstract void onProcess(Actor actor);

	protected void onConditionRemoved(Actor actor) {
	}
}
