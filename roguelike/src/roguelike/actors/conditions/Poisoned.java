package roguelike.actors.conditions;

import roguelike.actors.Actor;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

public class Poisoned extends Condition {
	private static final long serialVersionUID = 1028488464330263032L;

	public Poisoned(int duration) {
		super(new StringEx("Poisoned", SColor.GREEN, SColor.BLACK), duration);
	}

	@Override
	protected void onProcess(Actor actor) {
		actor.onDamaged(1);
	}

}
