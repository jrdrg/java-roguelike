package roguelike.actions.combat;

/**
 * Represents a condition such as poison, blood loss, or disease that has some
 * effect every turn until its duration expires
 * 
 * @author john
 * 
 */
public class Condition {

	protected int duration;

	public int getDuration() {
		return duration;
	}
}
