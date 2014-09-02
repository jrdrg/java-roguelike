package roguelike.maps;

import roguelike.actors.Actor;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class Tile {

	protected boolean visible;
	protected boolean wall;
	protected boolean explored;
	protected float lighting;
	protected SColor lightValue;

	protected boolean isPassable;
	protected char symbol;
	protected SColor color;
	protected SColor background;
	private Actor actor;

	Tile() {
		this.visible = false;
		this.background = SColor.BLACK;
		this.lightValue = SColor.BLACK;
	}

	public boolean isExplored() {
		return explored;
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible)
			this.explored = true;
	}

	public SColor getLightedColorValue() {
		return this.lightValue;
	}

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
		if (actor != null && visible) {
			return actor.getSymbol();
		}

		return this.symbol;
	}

	public SColor getColor() {
		if (!visible) {
			if (explored)
				return SColor.DARK_GRAY;

			return SColor.BLACK;
		}
		if (actor != null) {
			return actor.getColor();
		}
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

	public Tile setValues(char symbol, boolean isPassable, SColor color) {
		this.symbol = symbol;
		this.isPassable = isPassable;
		this.color = color;

		return this;
	}

	public Tile setValues(char symbol, boolean isPassable, SColor color, boolean wall) {
		setValues(symbol, isPassable, color);
		this.wall = wall;
		if (this.wall)
			this.lighting = 1f;

		return this;
	}

	public void moveActorTo(Tile newTile) {
		if (this.actor != null) {
			newTile.actor = this.actor;
			this.actor = null;
		} else {
			System.out.println("Tried to move a null actor");
		}
	}

}
