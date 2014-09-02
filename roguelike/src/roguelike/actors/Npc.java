package roguelike.actors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import squidpony.squidcolor.SColor;

public class Npc extends Actor {

	private Behavior behavior;
	private String name;

	public Npc(char symbol, SColor color, Game game, String name) {
		super(symbol, color, game);
		this.name = name;
	}

	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}

	@Override
	public Action getNextAction() {
		if (behavior != null) {
			return behavior.getAction();
		}
		return null;
	}

	@Override
	public String getName() {
		return name + " [" + getHealth().getCurrent() + "]";
	}

	@Override
	public void onAttacked(Actor attacker) {
		attackedBy.add(new AttackAttempt(attacker));
	}

	@Override
	public void onTurnFinished() {
		if (behavior != null) {
			behavior = behavior.getNextBehavior();
		}
	}

	@Override
	public void onKilled() {
		behavior = null;
	}
}
