package roguelike.actors.behaviors;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.WaitAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.maps.AStarPathfinder;
import roguelike.maps.MapArea;
import roguelike.maps.Path;
import roguelike.maps.Path.Step;
import roguelike.util.Log;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class MoveToRandomPointBehavior extends Behavior {
	private static final long serialVersionUID = 1L;

	transient AStarPathfinder pathfinder;
	transient Path pathToTarget;

	private Point currentTargetLocation;
	private MapArea map;
	private Point previousPosition;

	public MoveToRandomPointBehavior(Actor actor) {
		super(actor);
		this.map = Game.current().getCurrentMapArea();

		pathfinder = new AStarPathfinder(map, actor.getVisionRadius() * 2);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
	}

	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();

		pathfinder = new AStarPathfinder(map, actor.getVisionRadius() * 2);

		// Point position = actor.getPosition();
		// int sx = position.x;
		// int sy = position.y;
		// int tx = currentTargetLocation.x;
		// int ty = currentTargetLocation.y;
		// pathToTarget = pathfinder.findPath(map, sx, sy, tx, ty);
	}

	@Override
	public boolean isHostile() {
		return false;
	}

	@Override
	public Action getAction() {

		// if the NPC can see the player, just walk in his direction. if blocked or out of sight range,
		// find a path to the last place it saw the player
		Point position = actor.getPosition();

		if (currentTargetLocation != null && currentTargetLocation.equals(actor.getPosition())) {
			currentTargetLocation = null;
		}

		if (currentTargetLocation == null) {
			// pick a new target point

			int rndX = Game.current().random().between(position.x - actor.getVisionRadius() * 2, position.x + actor.getVisionRadius() * 2);
			int rndY = Game.current().random().between(position.y - actor.getVisionRadius() * 2, position.y + actor.getVisionRadius() * 2);

			int count = 0;
			while (!(map.isWithinBounds(rndX, rndY) && map.getTileAt(rndX, rndY).canPass()) && count++ < 10) {
				rndX = Game.current().random().between(position.x - actor.getVisionRadius() * 2, position.x + actor.getVisionRadius() * 2);
				rndY = Game.current().random().between(position.y - actor.getVisionRadius() * 2, position.y + actor.getVisionRadius() * 2);
			}
			if (count < 10) {
				currentTargetLocation = new Point(rndX, rndY);

				int sx = position.x;
				int sy = position.y;
				int tx = currentTargetLocation.x;
				int ty = currentTargetLocation.y;
				pathToTarget = pathfinder.findPath(map, sx, sy, tx, ty);
				if (pathToTarget != null)
					pathToTarget.nextStep(); // since the first step is just the current position

				Log.debug("CurrentTargetLocation: " + rndX + ", " + rndY + ", " + actor.getName() + " pos=" + position.x + ", " + position.y);
			}
		}

		if (currentTargetLocation != null) {

			if (position.equals(previousPosition)) {
				currentTargetLocation = null; // choose a new target point
			}
			else {
				if (pathToTarget != null) {

					Step step = pathToTarget.getCurrentStep();
					if (step != null) {
						pathToTarget.nextStep();
						int ssx = (step.getX()) - position.x;
						int ssy = (step.getY()) - position.y;

						DirectionIntercardinal direction = DirectionIntercardinal.getDirection(ssx, ssy);
						if (map.getTileAt(step.getX(), step.getY()).canPass())
							return new WalkAction(actor, map, direction);
						else
							Log.warning("Invalid walk action!!!");
					}
				}
			}
		}
		Log.verboseDebug("Resting, no path to target point...");
		return new WaitAction(actor);
	}

	@Override
	public Behavior getNextBehavior() {

		AttackAttempt lastAttackedBy = actor.getLastAttackedBy();
		if (lastAttackedBy != null && actor.isAdjacentTo(lastAttackedBy.getActor())) {

			Log.debug("MoveToRandomPointBehavior: Switching to targeted attack behavior");
			return new TargetedAttackBehavior(actor, lastAttackedBy.getActor());
		}

		Actor player = Game.current().getPlayer();
		if (actor.isAdjacentTo(player)) {

			Log.debug("Attacking Player");
			return new TargetedAttackBehavior(actor, player);
		}

		if (actor.canSee(player, map)) {
			Log.debug("MoveToRandomPointBehavior: switching to SearchForPlayerBehavior");
			return new SearchForPlayerBehavior(actor);
		}

		return this;
	}

	@Override
	public String getDescription() {
		if (currentTargetLocation != null)
			return "Moving to " + currentTargetLocation.x + "," + currentTargetLocation.y;
		else
			return "Moving to a random point";
	}
}
