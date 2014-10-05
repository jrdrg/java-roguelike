package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.actors.Condition;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

public class Poisoned extends Condition {

	public Poisoned(Actor actor, int duration) {
		super(new StringEx("Poisoned", SColor.GREEN, SColor.BLACK), actor, duration);
	}

	@Override
	protected void onProcess() {
		actor.health().damage(1);
	}

}
