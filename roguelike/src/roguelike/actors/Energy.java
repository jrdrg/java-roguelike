package roguelike.actors;

import java.io.Serializable;

public class Energy implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final int ACTION_THRESHOLD = 100;
	int current;

	public int getCurrent() {
		return current;
	}

	public boolean increase(int amount) {
		current += amount;
		return canAct();
	}

	public void act() {
		if (canAct()) {
			current -= ACTION_THRESHOLD;
		}
	}

	public boolean canAct() {
		return current >= ACTION_THRESHOLD;
	}
}
