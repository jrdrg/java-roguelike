package roguelike.maps;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AStarPathfinder {
    private static final Logger LOG = LogManager.getLogger(AStarPathfinder.class);
    
	/** The set of nodes that have been searched through */
	private ArrayList<Node> closed = new ArrayList<Node>();
	/** The set of nodes that we do not yet consider fully searched */
	private SortedList open = new SortedList();

	/** The map being searched */
	// private int[][] map;
	private MapArea map;
	/** The maximum depth of search we're willing to accept before giving up */
	private int maxSearchDistance;

	/** The complete set of nodes across the map */
	private Node[][] nodes;

	/**
	 * Create a path finder
	 * 
	 * @param heuristic
	 *            The heuristic used to determine the search order of the map
	 * @param map
	 *            The map to be searched
	 * @param maxSearchDistance
	 *            The maximum depth we'll search before giving up
	 * @param allowDiagMovement
	 *            True if the search should try diaganol movement
	 */
	public AStarPathfinder(MapArea map, int maxSearchDistance) {
		this.map = map;
		this.maxSearchDistance = maxSearchDistance;

		// nodes = new Node[map.length][map[0].length];
		nodes = new Node[map.width()][map.height()];
		for (int x = 0; x < map.width(); x++) {
			for (int y = 0; y < map.height(); y++) {
				nodes[x][y] = new Node(x, y);
			}
		}
	}

	/**
	 * @see PathFinder#findPath(Mover, int, int, int, int)
	 */
	public Path findPath(MapArea map, int sx, int sy, int tx, int ty) {

		LOG.debug("Finding path from {}, {} to {}, {}", sx, sy, tx, ty);

		// easy first check, if the destination is blocked, we can't get there
		// if (map.blocked(mover, tx, ty)) {
		// return null;
		// }

		// initial state for A*. The closed group is empty. Only the starting
		// tile is in the open list and it's cost is zero, i.e. we're already
		// there
		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);

		nodes[tx][ty].parent = null;

		// while we haven't found the goal and haven't exceeded our max search
		// depth
		int maxDepth = 0;
		while ((maxDepth < maxSearchDistance) && (open.size() != 0)) {
			// pull out the first node in our open list, this is determined to
			// be the most likely to be the next step based on our heuristic
			Node current = getFirstInOpen();
			if (current == nodes[tx][ty]) {
				break;
			}

			removeFromOpen(current);
			addToClosed(current);

			ArrayList<Point> neighbors = MapHelpers.getNeighbors(map, current.x, current.y, 1);
			// search through all the neighbours of the current node evaluating
			// them as next steps
			for (Point n : neighbors) {
				// if (!isValidLocation(sx, sy, n.x, n.y))
				// continue;

				int xp = n.x;
				int yp = n.y;
				float nextStepCost = current.cost + getMovementCost(current.x, current.y, xp, yp);
				Node neighbor = nodes[xp][yp];
				if (nextStepCost < neighbor.cost) {
					if (inOpenList(neighbor))
						removeFromOpen(neighbor);
					if (inClosedList(neighbor))
						removeFromClosed(neighbor);
				}
				if (!inOpenList(neighbor) && !inClosedList(neighbor)) {
					neighbor.cost = nextStepCost;
					neighbor.heuristic = (float) MapHelpers.distance(xp, yp, tx, ty);
					maxDepth = Math.max(maxDepth, neighbor.setParent(current));
					addToOpen(neighbor);
				}
			}
		}

		// since we've got an empty open list or we've run out of search
		// there was no path. Just return null
		if (nodes[tx][ty].parent == null) {
			return null;
		}

		// At this point we've definitely found a path so we can uses the parent
		// references of the nodes to find out way from the target location back
		// to the start recording the nodes on the way.
		Path path = new Path();
		Node target = nodes[tx][ty];
		while (target != nodes[sx][sy]) {
			path.prependStep(target.x, target.y);
			target = target.parent;
		}
		path.prependStep(sx, sy);

		// thats it, we have our path
		return path;
	}

	/**
	 * Get the first element from the open list. This is the next one to be searched.
	 * 
	 * @return The first element in the open list
	 */
	protected Node getFirstInOpen() {
		return (Node) open.first();
	}

	/**
	 * Add a node to the open list
	 * 
	 * @param node
	 *            The node to be added to the open list
	 */
	protected void addToOpen(Node node) {
		open.add(node);
	}

	/**
	 * Check if a node is in the open list
	 * 
	 * @param node
	 *            The node to check for
	 * @return True if the node given is in the open list
	 */
	protected boolean inOpenList(Node node) {
		return open.contains(node);
	}

	/**
	 * Remove a node from the open list
	 * 
	 * @param node
	 *            The node to remove from the open list
	 */
	protected void removeFromOpen(Node node) {
		open.remove(node);
	}

	/**
	 * Add a node to the closed list
	 * 
	 * @param node
	 *            The node to add to the closed list
	 */
	protected void addToClosed(Node node) {
		closed.add(node);
	}

	/**
	 * Check if the node supplied is in the closed list
	 * 
	 * @param node
	 *            The node to search for
	 * @return True if the node specified is in the closed list
	 */
	protected boolean inClosedList(Node node) {
		return closed.contains(node);
	}

	/**
	 * Remove a node from the closed list
	 * 
	 * @param node
	 *            The node to remove from the closed list
	 */
	protected void removeFromClosed(Node node) {
		closed.remove(node);
	}

	/**
	 * Check if a given location is valid for the supplied mover
	 * 
	 * @param mover
	 *            The mover that would hold a given location
	 * @param sx
	 *            The starting x coordinate
	 * @param sy
	 *            The starting y coordinate
	 * @param x
	 *            The x coordinate of the location to check
	 * @param y
	 *            The y coordinate of the location to check
	 * @return True if the location is valid for the given mover
	 */
	protected boolean isValidLocation(int sx, int sy, int x, int y) {
		// boolean invalid = (x < 0) || (y < 0) || (x >= map.length) || (y >=
		// map[0].length);
		boolean invalid = !map.isWithinBounds(x, y);

		if ((!invalid) && ((sx != x) || (sy != y))) {
			invalid = MapHelpers.isBlocked(map, x, y, true);
			// invalid = map.blocked(mover, x, y);
			// invalid = (map.getActorAt(x, y) != null) || !map.getTileAt(x, y).canPass();
			// invalid = map.cellBlocked(x, y) || map.cellOccupied(x, y);
		}

		return !invalid;
	}

	/**
	 * Get the cost to move through a given location
	 * 
	 * @param mover
	 *            The entity that is being moved
	 * @param sx
	 *            The x coordinate of the tile whose cost is being determined
	 * @param sy
	 *            The y coordiante of the tile whose cost is being determined
	 * @param tx
	 *            The x coordinate of the target location
	 * @param ty
	 *            The y coordinate of the target location
	 * @return The cost of movement through the given tile
	 */
	public float getMovementCost(int sx, int sy, int tx, int ty) {
		// return (float) map[tx][ty];
		// return (map.cellBlocked(tx, ty) || map.cellOccupied(tx, ty)) ? 999 : 0;

		return ((map.getActorAt(tx, ty) != null) || !map.getTileAt(tx, ty).canPass()) ? 999 : 0;

		// return map.getCost(mover, sx, sy, tx, ty);
	}

	/**
	 * Get the heuristic cost for the given location. This determines in which order the locations are processed.
	 * 
	 * @param mover
	 *            The entity that is being moved
	 * @param x
	 *            The x coordinate of the tile whose cost is being determined
	 * @param y
	 *            The y coordiante of the tile whose cost is being determined
	 * @param tx
	 *            The x coordinate of the target location
	 * @param ty
	 *            The y coordinate of the target location
	 * @return The heuristic cost assigned to the tile
	 */
	public float getHeuristicCost(int x, int y, int tx, int ty) {
		// return MapHelper.distance(x, y, tx, ty);

		return (float) Math.floor(MapHelpers.distanceSq(x, y, tx, ty));

		// return heuristic.getCost(map, mover, x, y, tx, ty);
	}

	/**
	 * A simple sorted list
	 * 
	 * @author kevin
	 */
	private class SortedList {
		/** The list of elements */
		private ArrayList<Node> list = new ArrayList<Node>();

		/**
		 * Retrieve the first element from the list
		 * 
		 * @return The first element from the list
		 */
		public Object first() {
			return list.get(0);
		}

		/**
		 * Empty the list
		 */
		public void clear() {
			list.clear();
		}

		/**
		 * Add an element to the list - causes sorting
		 * 
		 * @param o
		 *            The element to add
		 */
		public void add(Node o) {
			list.add(o);
			// list.sort();
			Collections.sort(list);
		}

		/**
		 * Remove an element from the list
		 * 
		 * @param o
		 *            The element to remove
		 */
		public void remove(Node o) {
			list.remove(o);
		}

		/**
		 * Get the number of elements in the list
		 * 
		 * @return The number of element in the list
		 */
		public int size() {
			return list.size();
		}

		/**
		 * Check if an element is in the list
		 * 
		 * @param o
		 *            The element to search for
		 * @return True if the element is in the list
		 */
		public boolean contains(Node o) {
			return list.contains(o);
		}
	}

	/**
	 * A single node in the search graph
	 */
	private class Node implements Comparable<Node> {
		/** The x coordinate of the node */
		private int x;
		/** The y coordinate of the node */
		private int y;
		/** The path cost for this node */
		private float cost;
		/** The parent of this node, how we reached it in the search */
		private Node parent;
		/** The heuristic cost of this node */
		private float heuristic;
		/** The search depth of this node */
		private int depth;

		/**
		 * Create a new node
		 * 
		 * @param x
		 *            The x coordinate of the node
		 * @param y
		 *            The y coordinate of the node
		 */
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Set the parent of this node
		 * 
		 * @param parent
		 *            The parent node which lead us to this node
		 * @return The depth we have no reached in searching
		 */
		public int setParent(Node parent) {
			depth = parent.depth + 1;
			this.parent = parent;

			return depth;
		}

		/**
		 * @see Comparable#compareTo(Node)
		 */
		public int compareTo(Node other) {
			Node o = other;

			float f = heuristic + cost;
			float of = o.heuristic + o.cost;

			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}

		/**
		 * @see Object#equals(Object)
		 */
		public boolean equals(Object other) {
			if (other instanceof Node) {
				Node o = (Node) other;

				return (o.x == x) && (o.y == y);
			}

			return false;
		}
	}

}
