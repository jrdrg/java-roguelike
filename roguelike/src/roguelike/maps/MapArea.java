package roguelike.maps;

import java.awt.Rectangle;
import java.util.List;

import roguelike.actors.Actor;
import roguelike.util.Coordinate;
import roguelike.util.CurrentItemTracker;

public class MapArea {

	private Tile[][] map;
	private float[][] lightResistances;
	private CurrentItemTracker<Actor> actors;
	private int width, height;

	public MapArea(int width, int height, MapBuilder mapBuilder) {
		actors = new CurrentItemTracker<Actor>();
		this.width = width;
		this.height = height;

		buildMapArea(mapBuilder);
	}

	public Coordinate getUpperLeftScreenTile(int screenCellsX, int screenCellsY, Coordinate center) {
		int left = (int) Math.round(center.x - (screenCellsX / 2.0));
		int top = (int) Math.round(center.y - (screenCellsY / 2.0));

		left = Math.min(Math.max(left, 0), Math.max(width - screenCellsX, 0));
		top = Math.min(Math.max(top, 0), Math.max(height - screenCellsY, 0));

		return new Coordinate(left, top);
	}

	public Rectangle getAreaInTiles(int screenCellsX, int screenCellsY, Coordinate center) {
		Coordinate upperLeft = getUpperLeftScreenTile(screenCellsX, screenCellsY, center);
		int w;
		int h;

		w = Math.min(this.width - upperLeft.x, screenCellsX);
		h = Math.min(this.height - upperLeft.y, screenCellsY);

		return new Rectangle(upperLeft.x, upperLeft.y, w, h);
	}

	public List<Actor> getAllActors() {
		return actors.getAll();
	}

	public Actor getCurrentActor() {
		return actors.getCurrent();
	}

	public void nextActor() {
		actors.advance();
	}

	public boolean addActor(Actor actor) {
		Coordinate pos = actor.getPosition();
		Tile tile = getTileAt(pos.x, pos.y);
		if (tile.getActor() != null)
			return false;

		actors.add(actor);
		tile.setActor(actor);
		return true;
	}

	public boolean moveActor(Actor actor, Coordinate newPosition) {
		Coordinate pos = actor.getPosition();
		Tile tile = getTileAt(pos.x, pos.y);
		if (tile.getActor() != null) {
			if (tile.moveActorTo(getTileAt(newPosition))) {
				actor.setPosition(newPosition.x, newPosition.y);
				return true;
			}
		}
		return false;
	}

	public boolean removeActor(Actor actor) {
		System.out.println("Removing actor " + actor.getName());

		Coordinate pos = actor.getPosition();
		Tile tile = getTileAt(pos.x, pos.y);
		if (tile.getActor() == null) {
			System.out.println("Failed!  actor=" + actor.getName());
			return false;
		}
		System.out.println("Success!");

		actors.remove(actor);
		System.out.println("     > actors count: " + actors.getAll().size());
		tile.setActor(null);
		return true;
	}

	public Actor getActorAt(int x, int y) {
		if (!isWithinBounds(x, y))
			return null;

		return getTileAt(x, y).getActor();
	}

	public Tile getTileAt(Coordinate position) {
		return getTileAt(position.x, position.y);
	}

	public Tile getTileAt(int x, int y) {
		if (!isWithinBounds(x, y))
			return null;

		return map[x][y];
	}

	public boolean canMoveTo(Actor actor, Coordinate position) {
		return canMoveTo(actor, position.x, position.y);
	}

	public boolean canMoveTo(Actor actor, int x, int y) {
		// check for out of bounds, etc
		if (!isWithinBounds(x, y))
			return false;

		Tile tile = map[x][y];
		if (tile.canPass()) {
			return actor.onMoveAttempting(this, tile);
		}

		return false;
	}

	public boolean isWithinBounds(int x, int y) {
		if (x < 0 || x >= this.width)
			return false;

		if (y < 0 || y >= this.height)
			return false;

		return true;
	}

	private void buildMapArea(MapBuilder mapBuilder) {
		map = new Tile[width][height];

		mapBuilder.buildMap(map);

		lightResistances = new float[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				lightResistances[x][y] = map[x][y].getLighting();
			}
		}
	}
}
