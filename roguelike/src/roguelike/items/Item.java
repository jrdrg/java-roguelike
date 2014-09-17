package roguelike.items;

import java.io.Serializable;
import java.util.UUID;

import squidpony.squidcolor.SColor;

public abstract class Item implements Serializable {

	private static final long serialVersionUID = -7103589500266419030L;

	private UUID itemId = UUID.randomUUID();
	protected String name;
	protected char symbol = '?';
	protected SColor color = SColor.WHITE;

	boolean droppable;

	public final UUID itemId() {
		return this.itemId;
	}

	public String getName() {
		return this.name;
	}

	public char getSymbol() {
		return this.symbol;
	}

	public SColor getColor() {
		return this.color;
	}

	public abstract String getDescription();

	boolean isDroppable() {
		return droppable;
	}

	protected Item() {
		this.droppable = true;
	}

	protected Item(ItemData data) {
		this.droppable = data.droppable;
		this.symbol = data.symbol;
		this.name = data.name;
	}
}
