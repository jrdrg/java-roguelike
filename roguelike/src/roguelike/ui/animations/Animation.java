package roguelike.ui.animations;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;

public abstract class Animation {

	protected int currentFrame = 0;
	protected int totalFrames;

	/**
	 * True if the animation should prevent user input while it's running
	 * 
	 * @return
	 */
	public abstract boolean isBlocking();

	/**
	 * Draws the current frame of the animation and advances to the next one.
	 * 
	 * @param terminal
	 * @return True if the last frame was just drawn and the animation should be
	 *         removed.
	 */
	public final boolean nextFrame(TerminalBase terminal) {
		if (currentFrame < totalFrames) {
			onNextFrame(terminal);
		}
		currentFrame++;
		boolean finished = currentFrame > totalFrames;
		return finished;
	}

	protected Point getOffsetPosition(TerminalBase terminal, Actor target) {
		Game g = Game.current();
		Rectangle termSize = terminal.size();
		Point upperLeft = g
				.getCurrentMapArea()
				.getUpperLeftScreenTile(termSize.width, termSize.height, g.getPlayer().getPosition());

		Coordinate targetPos = target.getPosition();

		Point ret = new Point(targetPos.x - upperLeft.x, targetPos.y - upperLeft.y);

		return ret;
	}

	public abstract void onNextFrame(TerminalBase terminal);
}
