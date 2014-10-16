package roguelike.items;

public enum MeleeRange {
	CLOSE(1),
	SHORT(2),
	MEDIUM(3),
	LONG(4),
	VERY_LONG(5);

	public final int reach;

	MeleeRange(int reach) {
		this.reach = reach;
	}
}
