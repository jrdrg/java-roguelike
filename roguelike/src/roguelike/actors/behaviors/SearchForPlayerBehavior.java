package roguelike.actors.behaviors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.RestAction;
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
	private MapArea map;

	public SearchForPlayerBehavior(Actor actor) {
		super(actor);
		this.map = Game.current().getCurrentMapArea();

		pathfinder = new AStarPathfinder(map, actor.getVisionRadius() * 2);
	}

	@Override
	public Action getAction() {
		Actor player = Game.current().getPlayer();
		if (actor.canSee(player, map)) {
			// go towards player
			int sx = actor.getPosition().x;
			int sy = actor.getPosition().y;
			int tx = player.getPosition().x;
			int ty = player.getPosition().y;
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
			Log.debug("Resting, can't find path...");
			return new RestAction(actor);
		}
		else {
			// try to find a path toward the player
			Log.debug("Resting, can't see player...");
			return new RestAction(actor);
		}
	}

	@Override
	public Behavior getNextBehavior() {
		AttackAttempt lastAttackedBy = actor.getLastAttackedBy();
		if (lastAttackedBy != null) {
			Log.debug("Switching to targeted attack behavior");
			return new TargetedAttackBehavior(actor, lastAttackedBy.getActor());
		}

		return this;
	}

}
