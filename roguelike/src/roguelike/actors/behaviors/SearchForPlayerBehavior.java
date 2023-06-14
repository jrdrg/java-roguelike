package roguelike.actors.behaviors;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.WaitAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.maps.AStarPathfinder;
import roguelike.maps.MapArea;
import roguelike.maps.Path;
import roguelike.maps.Path.Step;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class SearchForPlayerBehavior extends EnemyBehavior {
    private static final Logger LOG = LogManager.getLogger(SearchForPlayerBehavior.class);
    
	private static final long serialVersionUID = 1L;

	private Point lastPlayerLocation;
	private transient AStarPathfinder pathfinder;
	private transient Path pathToTarget;
	private MapArea map;

	public SearchForPlayerBehavior(Actor actor) {
		super(actor);
		this.map = Game.current().getCurrentMapArea();

		pathfinder = new AStarPathfinder(map, actor.getVisionRadius() * 2);
	}

	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();

		pathfinder = new AStarPathfinder(map, actor.getVisionRadius() * 2);
	}

	@Override
	public boolean isHostile() {
		return true;
	}

	@Override
	public Action getAction() {

		// if the NPC can see the player, just walk in his direction. if blocked or out of sight range,
		// find a path to the last place it saw the player

		if (lastPlayerLocation != null && lastPlayerLocation.equals(actor.getPosition())) {
			lastPlayerLocation = null;
		}

		Actor player = Game.current().getPlayer();
		if (actor.canSee(player, map)) {
			lastPlayerLocation = player.getPosition();
		}

		// go towards player location
		if (lastPlayerLocation != null) {

			int sx = actor.getPosition().x;
			int sy = actor.getPosition().y;
			int tx = lastPlayerLocation.x;
			int ty = lastPlayerLocation.y;
			pathToTarget = pathfinder.findPath(map, sx, sy, tx, ty);
			if (pathToTarget != null) {
				pathToTarget.nextStep();
				Step step = pathToTarget.getCurrentStep();
				if (step != null) {
					nextBehavior = this;

					pathToTarget.nextStep();
					int ssx = (step.getX()) - sx;
					int ssy = (step.getY()) - sy;
					return new WalkAction(actor, map, DirectionIntercardinal.getDirection(ssx, ssy));
				}
			}
		}
		LOG.debug("Resting, no path to player...");
		nextBehavior = new MoveToRandomPointBehavior(actor);
		return new WaitAction(actor);
	}

	@Override
	public void onAttacked(Actor attacker) {
	    LOG.debug("SearchForPlayerBehavior: Switching to targeted attack behavior");
		nextBehavior = new TargetedAttackBehavior(actor, attacker);
	}

	@Override
	public Behavior getNextBehavior() {

		Actor player = Game.current().getPlayer();
		if (actor.canSee(player, map)) {
			if (canAttackTarget(player))
				nextBehavior = new TargetedAttackBehavior(actor, player);
		}

		if (actor.isAdjacentTo(player)) {

		    LOG.debug("Attacking Player");
			return new TargetedAttackBehavior(actor, player);
		}

		return nextBehavior;
	}

	@Override
	public String getDescription() {
		return "Looking for you";
	}
}
