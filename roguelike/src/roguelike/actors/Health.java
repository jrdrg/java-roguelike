package roguelike.actors;

public class Health {
	private int current;
	private int maximum;

	public Health(int maximum) {
		this.maximum = maximum;
		this.current = maximum;
	}

	public int getCurrent() {
		return this.current;
	}

	public int getMaximum() {
		return this.maximum;
	}

	public void heal(int amount) {
		this.current = Math.min(current + amount, maximum);
	}

	public boolean damage(int amount) {
		this.current -= amount;
		return current <= 0;
	}

	public void setMaximum(int maximum) {
		setMaximum(maximum, false);
	}

	public void setMaximum(int maximum, boolean setCurrentToMax) {
		this.maximum = maximum;
		if (setCurrentToMax)
			this.current = maximum;
	}
}
