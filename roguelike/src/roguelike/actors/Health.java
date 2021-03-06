package roguelike.actors;

import java.io.Serializable;

public class Health implements Serializable {
	private static final long serialVersionUID = 1L;

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

	public void setMaximum(int maximum) {
		setMaximum(maximum, maximum < current);
	}

	public void setMaximum(int maximum, boolean setCurrentToMax) {
		this.maximum = maximum;
		if (setCurrentToMax)
			this.current = maximum;
	}

	public void heal(int amount) {
		this.current = Math.min(current + amount, maximum);
	}

	boolean damage(int amount) {
		this.current -= amount;
		return current <= 0;
	}
}
