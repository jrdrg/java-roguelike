package roguelike.maps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import roguelike.actors.Actor;
import roguelike.items.Inventory;
import roguelike.items.Item;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class Tile implements Serializable {
	private static final long serialVersionUID = 1L;

	protected boolean visible;
	protected boolean wall;
	protected boolean explored;
	protected float lighting;
	protected boolean isPassable;
	protected char symbol;
	protected int speedModifier;

	protected Inventory items;

	protected SColor lightValue;
	protected SColor color;
	protected SColor background;

	private Actor actor;

	Tile() {
		this.visible = false;
		this.background = SColor.BLACK;
		this.lightValue = SColor.BLACK;
		this.items = new Inventory();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	}

	/**
	 * Indicates whether this tile has been explored by the player
	 * 
	 * @return True if the player has visited this tile
	 */
	public boolean isExplored() {
		return explored;
	}

	/**
	 * Indicates this tile's visibility to the player
	 * 
	 * @return True if the player can see this tile
	 */
	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible)
			this.explored = true;
	}

	public Tile setSpeedModifier(int speedModifier) {
		this.speedModifier = speedModifier;
		return this;
	}

	/**
	 * Gets the color that this tile should be drawn in after lighting and visibility is taken into account
	 * 
	 * @return
	 */
	public SColor getLightedColorValue() {
		return this.lightValue;
	}

	/**
	 * Sets the actual color this tile will be drawn in after taking lighting and visibility into account
	 * 
	 * @param lightValue
	 */
	public void setLightedColorValue(SColor lightValue) {
		this.lightValue = lightValue;
	}

	public float getLighting() {
		return this.lighting;
	}

	public Tile setLighting(float lighting) {
		this.lighting = lighting;
		return this;
	}

	public char getSymbol() {
		if (actor != null && visible)
			return actor.symbol();

		if (items.any())
			return getTopItem().symbol();

		return this.symbol;
	}

	public SColor getColor() {
		if (!visible) {
			if (explored)
				return SColorFactory.dimmer(SColor.DARK_CERULEAN);
			// return SColorFactory.light(SColor.DARK_GRAY);

			return SColor.BLACK;
			// return SColor.DARK_INDIGO;
		}

		if (actor != null)
			return actor.color();

		if (items.any())
			return getTopItem().color();

		return this.color;
	}

	public SColor getBackground() {
		if (!visible) {
			if (explored)
				return SColorFactory.dimmest(this.background);

			return SColor.BLACK;
		}
		return this.background;
	}

	public Tile setBackground(SColor background) {
		this.background = background;
		return this;
	}

	public Actor getActor() {
		return this.actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public boolean canPass() {
		return isPassable;
	}

	public boolean isWall() {
		return wall;
	}

	Tile setValues(char symbol, boolean isPassable, SColor color) {
		this.symbol = symbol;
		this.isPassable = isPassable;
		this.color = color;

		return this;
	}

	Tile setValues(char symbol, boolean isPassable, SColor color, boolean wall) {
		setValues(symbol, isPassable, color);
		this.wall = wall;
		if (this.wall)
			this.lighting = 1f;

		return this;
	}

	boolean moveActorTo(Tile newTile) {
		if (this.actor != null) {
			newTile.actor = this.actor;
			this.actor = null;
			return true;
		} else {
			System.out.println("Tried to move a null actor");
			return false;
		}
	}

	Inventory getItems() {
		return items;
	}

	private Item getTopItem() {
		if (items.any()) {
			return items.getItem(items.getCount() - 1);
		}
		return null;
	}
}
