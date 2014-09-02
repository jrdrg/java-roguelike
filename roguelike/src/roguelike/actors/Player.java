package roguelike.actors;

import java.awt.event.KeyEvent;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.FailAction;
import roguelike.actions.QuitAction;
import roguelike.actions.RestAction;
import roguelike.actions.WalkAction;
import roguelike.actors.interfaces.Attackable;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.gui.SGKeyListener;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class Player extends Actor implements Attackable {

	private SGKeyListener keyListener;

	public Player(Game game, SGKeyListener keyListener) {
		super('@', SColor.WHITE, game);
		this.keyListener = keyListener;

		// TODO: load these during character creation somewhere
		this.getStatistics().speed.setBase(20);
	}

	public static boolean isPlayer(Actor actor) {
		return actor instanceof Player;
	}

	@Override
	public Action getNextAction() {
		KeyEvent input = keyListener.next();

		switch (input.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			return new QuitAction(this);

		case KeyEvent.VK_LEFT:
			return new WalkAction(this, game.getCurrentMapArea(), DirectionIntercardinal.LEFT);

		case KeyEvent.VK_RIGHT:
			return new WalkAction(this, game.getCurrentMapArea(), DirectionIntercardinal.RIGHT);

		case KeyEvent.VK_UP:
			return new WalkAction(this, game.getCurrentMapArea(), DirectionIntercardinal.UP);

		case KeyEvent.VK_DOWN:
			return new WalkAction(this, game.getCurrentMapArea(), DirectionIntercardinal.DOWN);

		case KeyEvent.VK_PERIOD:
			return new RestAction(this);
		}
		return new FailAction(this);
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
		return 30;
	}

	@Override
	public void onAttacked(Actor attacker) {
	}

	@Override
	public void onKilled() {
		// back to title screen
	}
}
