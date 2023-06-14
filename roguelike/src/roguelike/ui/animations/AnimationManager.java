package roguelike.ui.animations;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.ui.InputManager;
import roguelike.ui.windows.TerminalBase;

public class AnimationManager {
    private static final Logger LOG = LogManager.getLogger(AnimationManager.class);

	private ArrayList<Animation> animations;
	private boolean refresh;

	public AnimationManager() {
		animations = new ArrayList<Animation>();
	}

	public void addAnimation(Animation animation) {
		animations.add(animation);

		if (animation.isBlocking())
			InputManager.setInputEnabled(false);
	}

	public boolean shouldRefresh() {
		if (refresh) {
			LOG.debug("AnimationManager refresh = true");
			refresh = false;
			return true;
		}
		return false;
	}

	/**
	 * Returns true if a frame of any animation was processed
	 * 
	 * @param terminal
	 * @return
	 */
	public boolean nextFrame(TerminalBase terminal) {
		boolean anyBlocking = false;
		boolean anyAnimations = false;

		ArrayList<Animation> toRemove = new ArrayList<Animation>();
		for (Animation animation : animations) {
			if (animation.nextFrame(terminal)) {
				toRemove.add(animation);
			} else {
				if (!anyBlocking && animation.isBlocking())
					anyBlocking = true;
			}
			anyAnimations = true;
		}

		for (Animation a : toRemove) {
			animations.remove(a);
		}

		if (!anyBlocking)
			InputManager.setInputEnabled(true);

		if (toRemove.size() > 0)
			refresh = true;

		return anyAnimations;
	}

	public void clear() {
		animations.clear();
	}
}
