package roguelike.actors;

public class Energy {
	private final int ACTION_THRESHOLD = 100;
	private int current;

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
