package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

public class ReducedVision extends Condition {
	private static final long serialVersionUID = 7729159918086465466L;

	private int oldVisionRadius;

	public ReducedVision(int duration) {
		super(new StringEx("Reduced Vision", SColor.BLUE_VIOLET, SColor.BLACK), duration);
	}

	@Override
	public void onConditionAdded(Actor actor) {
		oldVisionRadius = actor.getVisionRadius();
		actor.setVisionRadius((int) (oldVisionRadius * 0.33));
		actor.doAction("can no longer see as well.");
	}

	@Override
	protected void onProcess(Actor actor) {
	}

	@Override
	protected void onConditionRemoved(Actor actor) {
		actor.setVisionRadius(oldVisionRadius);
		actor.doAction("'s vision has returned to normal.");
	}
}
