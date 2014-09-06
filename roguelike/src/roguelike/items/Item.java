package roguelike.items;

import java.util.UUID;

import squidpony.squidcolor.SColor;

public abstract class Item {

	private UUID itemId = UUID.randomUUID();

	public final UUID itemId() {
		return this.itemId;
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract char getSymbol();
	
	public abstract SColor getColor();
}
