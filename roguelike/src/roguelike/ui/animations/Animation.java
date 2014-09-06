package roguelike.ui.animations;

import roguelike.ui.windows.Terminal;

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
	public final boolean nextFrame(Terminal terminal) {
		if (currentFrame < totalFrames) {
			onNextFrame(terminal);
		}
		currentFrame++;
		boolean finished = currentFrame > totalFrames;
		return finished;
	}

	public abstract void onNextFrame(Terminal terminal);
}
