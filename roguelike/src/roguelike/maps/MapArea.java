package roguelike.maps;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.EnemyFactory;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import roguelike.util.CurrentItemTracker;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.RNG;

public class MapArea implements Serializable {
    private static final Logger LOG = LogManager.getLogger(MapArea.class);
    
	private static final long serialVersionUID = 1L;

	private Tile[][] map;
	private float[][] lightResistances;
	private boolean[][] walls;

	protected CurrentItemTracker<Actor> actors;
	protected int width, height;
	protected int difficulty; // controls how difficult random enemies are here

	protected String name;

	// private PointGraph pointGraph;

	// private MapArea() {
	// }

	protected MapArea(int width, int height, MapBuilderBase mapBuilder) {
		actors = new CurrentItemTracker<Actor>();
		this.width = width;
		this.height = height;
		this.difficulty = 1;

		buildMapArea(mapBuilder);
	}

	public static MapArea build(int width, int height, MapBuilderBase mapBuilder) {
		return new Dungeon(width, height, mapBuilder, 1, 10);
	}

	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		LOG.debug("Read map");
	}

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}

	public String name() {
		return this.name;
	}

	public void spawnMonsters() {
	    LOG.debug("spawnMonsters");
		// int maxActors = 70;
		int maxActors = 10;
		if (actors.count() < maxActors && Game.current().random().nextInt(10) > 6) {
			/* create a new one somewhere close to the player */

			Coordinate position = findRandomNonVisibleTile();
			if (position != null) {

				Actor npc = EnemyFactory.createEnemy(position.x, position.y, difficulty);

				if (addActor(npc)) {
					Game.current().displayMessage(npc.getName() + " created at " + position.x + ", " + position.y, SColor.ALOEWOOD_BROWN);
				}
			}
		}
	}

	private Coordinate findRandomNonVisibleTile() {
		Coordinate playerPos = Game.current().getPlayer().getPosition();

		RNG rng = Game.current().random();

		for (int i = 0; i < 5; i++) {
			int x = rng.between(playerPos.x - 50, playerPos.x + 50);
			int y = rng.between(playerPos.y - 50, playerPos.y + 50);

			x = Math.max(0, Math.min(x, width - 1));
			y = Math.max(0, Math.min(y, height - 1));

			Tile tile = getTileAt(x, y);
			if (!tile.visible && !tile.isWall() && tile.canPass()) {
				return new Coordinate(x, y);
			}
		}
		return null;
	}

	public float[][] getLightValues() {
		return lightResistances;
	}

	public boolean[][] getWalls() {
		return walls;
	}

	/**
	 * Updates internal arrays tracking light values and walls for FOV calculations. This should not change very often.
	 */
	public void updateValues() {
		lightResistances = new float[width][height];
		walls = new boolean[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				lightResistances[x][y] = map[x][y].getLighting();
				walls[x][y] = map[x][y].isWall();
			}
		}
	}

	/**
	 * Determines the location of the upper-left position of the visible area, based on the provided screen size and
	 * center point (generally the player's location).
	 * 
	 * @param screenCellsX
	 *            The width of the screen in cells
	 * @param screenCellsY
	 *            The height of the screen in cells
	 * @param center
	 *            The point at which the screen should be centered on
	 * @return The location of the upper left point, in cells, after adjusting for map boundaries
	 */
	public Coordinate getUpperLeftScreenTile(int screenCellsX, int screenCellsY, Coordinate center) {
		int left = (int) Math.round(center.x - (screenCellsX / 2.0));
		int top = (int) Math.round(center.y - (screenCellsY / 2.0));

		left = Math.min(Math.max(left, 0), Math.max(width - screenCellsX, 0));
		top = Math.min(Math.max(top, 0), Math.max(height - screenCellsY, 0));

		return new Coordinate(left, top);
	}

	/**
	 * Determines the visible screen area, in cells
	 * 
	 * @param screenCellsX
	 *            The width of the screen in cells
	 * @param screenCellsY
	 *            The height of the screen in cells
	 * @param center
	 *            The point at which the screen is centered on
	 * @return A Rectangle representing the area that should be drawn to the screen, in cells
	 */
	public Rectangle getVisibleAreaInTiles(int screenCellsX, int screenCellsY, Coordinate center) {
		Coordinate upperLeft = getUpperLeftScreenTile(screenCellsX, screenCellsY, center);
		int w;
		int h;

		w = Math.min(this.width - upperLeft.x, screenCellsX);
		h = Math.min(this.height - upperLeft.y, screenCellsY);

		return new Rectangle(upperLeft.x, upperLeft.y, w, h);
	}

	public Rectangle getVisibleAreaInTiles(TerminalBase terminal, Coordinate center) {
		return getVisibleAreaInTiles(terminal.size().width, terminal.size().height, center);
	}

	/**
	 * Adds an item to the tile at x,y
	 * 
	 * @param item
	 * @param x
	 * @param y
	 */
	public void addItem(Item item, int x, int y) {
		Inventory items = getItemsAt(x, y);
		items.add(item);
	}

	/**
	 * Removes an item from the tile at x,y
	 * 
	 * @param item
	 * @param x
	 * @param y
	 * @return True if the item was removed, false if there are no items on the tile or the specific item wasn't in the
	 *         list.
	 */
	public boolean removeItem(Item item, int x, int y) {
		Inventory items = getItemsAt(x, y);
		if (!items.any()) {
		    LOG.warn("Failed! no items at {}, {}", x, y);
			return false;
		}
		boolean removed = items.remove(item);
		return removed;
	}

	/**
	 * Returns an Inventory object with all the items at the specified tile
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Inventory getItemsAt(int x, int y) {
		Tile tile = getTileAt(x, y);
		return tile.getItems();
	}

	/**
	 * Returns a list of all actors in this map
	 * 
	 * @return
	 */
	public List<Actor> getAllActors() {
		return actors.getAll();
	}

	/**
	 * Returns the actor that is currently waiting to act.
	 * 
	 * @return
	 */
	public Actor getCurrentActor() {
		return actors.getCurrent();
	}

	public Actor peekNextActor() {
		return actors.peek();
	}

	/**
	 * Advances the current actor to the next in the queue.
	 */
	public void nextActor(String reason) {
		actors.advance();

		// Log.debug("Current actor: " + getCurrentActor().getName() + " => " + reason);
	}

	/**
	 * Returns the actor at the given position.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Actor getActorAt(int x, int y) {
		if (!isWithinBounds(x, y))
			return null;

		return getTileAt(x, y).getActor();
	}

	/**
	 * Adds an actor to this map.
	 * 
	 * @param actor
	 * @return True if the actor was added, false if there was already an actor at the location specified by
	 *         actor.getPosition().
	 */
	public boolean addActor(Actor actor) {
		Coordinate pos = actor.getPosition();
		Tile tile = getTileAt(pos.x, pos.y);
		if (tile.getActor() != null)
			return false;

		actors.add(actor);
		tile.setActor(actor);
		return true;
	}

	/**
	 * Moves an actor from one tile to another.
	 * 
	 * @param actor
	 * @param newPosition
	 * @return True if the move was successful, false otherwise.
	 */
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

	/**
	 * Removes an actor from the map.
	 * 
	 * @param actor
	 * @return True if the actor could be removed, false otherwise (for instance, if the tile at the actor's position
	 *         actually has no actor, which probably indicates a bug)
	 */
	public boolean removeActor(Actor actor) {
		LOG.debug("Removing actor {}", actor.getName());

		Coordinate pos = actor.getPosition();
		Tile tile = getTileAt(pos.x, pos.y);
		if (tile.getActor() == null) {
			LOG.warn("Failed!  actor = {}", actor.getName());
			return false;
		}
		LOG.debug("Success!");

		actors.remove(actor);
		LOG.debug("     > actors count: {}", actors.getAll().size());
		tile.setActor(null);
		return true;
	}

	/**
	 * Returns the tile at the given position.
	 * 
	 * @param position
	 * @return
	 */
	public Tile getTileAt(Point position) {
		return getTileAt(position.x, position.y);
	}

	public Tile getTileAt(int x, int y) {
		if (!isWithinBounds(x, y))
			return null;

		return map[x][y];
	}

	public int getSpeedModifier(Coordinate position) {
		if (!isWithinBounds(position.x, position.y))
			return 0;

		return map[position.x][position.y].speedModifier;
	}

	/**
	 * Determines if the actor can move to the specified position.
	 * 
	 * @param actor
	 * @param position
	 * @return True if a move is allowed, false otherwise.
	 */
	public boolean canMoveTo(Actor actor, Coordinate position) {
		return canMoveTo(actor, position.x, position.y);
	}

	/**
	 * Determines if the actor can move to the specified position.
	 * 
	 * @param actor
	 * @param x
	 * @param y
	 * @return True if a move is allowed, false otherwise.
	 */
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

	public boolean isVisible(Point position) {
		Tile tile = getTileAt(position);
		if (tile == null)
			return false;

		return tile.isVisible();
	}

	/**
	 * Returns true if the given location is within the boundaries of this map.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isWithinBounds(int x, int y) {
		if (x < 0 || x >= this.width)
			return false;

		if (y < 0 || y >= this.height)
			return false;

		return true;
	}

	/**
	 * Populates this map's tiles.
	 * 
	 * @param mapBuilder
	 *            The MapBuilder used to construct this map.
	 */
	private void buildMapArea(MapBuilderBase mapBuilder) {
		map = new Tile[width][height];

		this.name = mapBuilder.buildMap(map);
		updateValues();

		// TODO: pathfinding precalculations?

		// Log.debug("Calculating path maps...");
		// pointGraph = new PointGraph();
		// for (int x = 0; x < width; x++) {
		// for (int y = 0; y < height; y++) {
		// if (getTileAt(x, y).isPassable)
		// pointGraph.addVertex(new Vertex(new Point(x, y)));
		// }
		// }
		// pointGraph.calculateEdges();
		// Log.debug("done");
	}

}
