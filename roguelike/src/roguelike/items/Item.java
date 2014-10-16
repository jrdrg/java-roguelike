package roguelike.items;

import java.io.Serializable;
import java.util.UUID;

import roguelike.actors.Actor;
import squidpony.squidcolor.SColor;

public abstract class Item implements Serializable {

	private static final long serialVersionUID = -7103589500266419030L;

	private UUID itemId = UUID.randomUUID();
	protected String name;
	protected char symbol = '?';
	protected SColor color = SColor.WHITE;
	protected int weight;

	boolean droppable;
	boolean stackable;

	public final UUID itemId() {
		return this.itemId;
	}

	public String name() {
		if (name == null)
			return "???";

		return this.name;
	}

	public char symbol() {
		return this.symbol;
	}

	public SColor color() {
		return this.color;
	}

	public int weight() {
		return this.weight;
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

	public void onEquipped(Actor actor) {
	}

	public void onRemoved(Actor actor) {
	}

	public void onUsed(Actor actor) {
	}

	public void onThrown(Actor actor, Actor target) {
	}

	public void onAttacked(Actor actor, Actor target) {
	}

	public void onAttacking(Actor actor, Actor target) {
	}
}
