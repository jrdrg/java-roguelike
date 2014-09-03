package roguelike.items;

import java.util.UUID;

public abstract class Item {

	private UUID itemId = UUID.randomUUID();

	public final UUID itemId() {
		return this.itemId;
	}

	public abstract String getName();

	public abstract String getDescription();
}
