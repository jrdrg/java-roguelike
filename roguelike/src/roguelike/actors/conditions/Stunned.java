package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

public class Stunned extends Condition {
	private static final long serialVersionUID = -5457623122809872866L;

	public Stunned(int duration) {
		super(new StringEx("Stunned", SColor.ZINNWALDITE, SColor.BLACK), duration);
	}

	@Override
	public void onConditionAdded(Actor actor) {
		actor.statistics().reflexBonus -= 5;
		actor.statistics().aimingBonus -= 5;
	}

	@Override
	protected void onProcess(Actor actor) {
	}

	@Override
	protected void onConditionRemoved(Actor actor) {
		actor.statistics().reflexBonus += 5;
		actor.statistics().aimingBonus += 5;
	}
}
