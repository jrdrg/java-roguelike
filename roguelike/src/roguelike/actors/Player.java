package roguelike.actors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.actors.behaviors.PlayerInputBehavior;
import roguelike.actors.interfaces.Attackable;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.gui.SGKeyListener;

public class Player extends Actor implements Attackable {

	private SGKeyListener keyListener;
	private Behavior behavior;

	public Player(Game game, SGKeyListener keyListener) {
		super('@', SColor.WHITE, game);
		this.keyListener = keyListener;

		// TODO: load these during character creation somewhere
		this.getStatistics().speed.setBase(20);

		this.behavior = new PlayerInputBehavior(this, keyListener);
	}

	public static boolean isPlayer(Actor actor) {
		return actor instanceof Player;
	}

	@Override
	public Action getNextAction() {
		return behavior.getAction();
	}

	@Override
	public String getName() {
		return "Player";
	}

	@Override
	public String getDescription() {
		return "The player";
	}

	@Override
	public int getVisionRadius() {
		return 25;
	}

	@Override
	public void onAttacked(Actor attacker) {
	}

	@Override
	public void onKilled() {
		// back to title screen
	}
}
