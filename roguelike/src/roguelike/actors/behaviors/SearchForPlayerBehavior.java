package roguelike.actors.behaviors;

import java.awt.Point;

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

public class SearchForPlayerBehavior extends Behavior {

	AStarPathfinder pathfinder;
	Path pathToTarget;
	private Point lastPlayerLocation;
	private MapArea map;
	private boolean noPathToPlayer = false;

	public SearchForPlayerBehavior(Actor actor) {
		super(actor);
		this.map = Game.current().getCurrentMapArea();

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
					pathToTarget.nextStep();
					int ssx = (step.getX()) - sx;
					int ssy = (step.getY()) - sy;
					return new WalkAction(actor, map, DirectionIntercardinal.getDirection(ssx, ssy));
				}
			}
		}
		Log.verboseDebug("Resting, no path to player...");
		noPathToPlayer = true;
		return new WaitAction(actor);
	}

	@Override
	public Behavior getNextBehavior() {

		AttackAttempt lastAttackedBy = actor.getLastAttackedBy();
		if (lastAttackedBy != null && actor.isAdjacentTo(lastAttackedBy.getActor())) {

			Log.debug("SearchForPlayerBehavior: Switching to targeted attack behavior");
			return new TargetedAttackBehavior(actor, lastAttackedBy.getActor());
		}

		Actor player = Game.current().getPlayer();
		if (actor.isAdjacentTo(player)) {

			Log.debug("Attacking Player");
			return new TargetedAttackBehavior(actor, player);
		}

		if (noPathToPlayer)
			return new MoveToRandomPointBehavior(actor);

		return this;
	}

}
