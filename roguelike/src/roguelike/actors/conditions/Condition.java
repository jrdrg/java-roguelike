package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;

public abstract class Condition {

	protected StringEx identifier;
	protected int duration;

	protected Condition(StringEx identifier, int duration) {
		this.identifier = identifier;
		this.duration = duration;
	}

	public int getDuration() {
		return duration;
	}

	public final boolean process(Actor actor) {
		if (duration > 0) {
			duration--;
			onProcess(actor);
		}
		return duration <= 0;
	}

	public StringEx identifier() {
		return identifier;
	}

	protected abstract void onProcess(Actor actor);

	protected void onConditionRemoved(Actor actor) {
	}
}
