package roguelike.actors.behaviors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actions.WaitAction;
import roguelike.actions.WalkAction;
import roguelike.actors.Actor;
import roguelike.actors.AttackAttempt;
import roguelike.maps.MapArea;
import roguelike.util.Coordinate;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class RandomWalkBehavior extends Behavior {
    private static final Logger LOG = LogManager.getLogger(RandomWalkBehavior.class);

    private static final long serialVersionUID = 1L;

    public RandomWalkBehavior(Actor actor) {
        super(actor);
    }

    @Override
    public boolean isHostile() {
        return false;
    }

    @Override
    public Action getAction() {
        MapArea map = Game.current().getCurrentMapArea();
        double rnd = Game.current().random().nextDouble();
        DirectionIntercardinal direction;
        if (rnd < 0.25) {
            direction = DirectionIntercardinal.UP;
        }
        else if (rnd < 0.5) {
            direction = DirectionIntercardinal.LEFT;
        }
        else if (rnd < 0.75) {
            direction = DirectionIntercardinal.DOWN;
        }
        else {
            direction = DirectionIntercardinal.RIGHT;
        }

        Coordinate pos = actor.getPosition().createOffsetPosition(direction);
        if (map.canMoveTo(actor, pos) && map.getActorAt(pos.x, pos.y) == null)
            return new WalkAction(actor, map, direction);

        return new WaitAction(actor);
    }

    @Override
    public Behavior getNextBehavior() {
        AttackAttempt lastAttackedBy = actor.getLastAttackedBy();
        if (lastAttackedBy != null) {
            LOG.debug("RandomWalkBehavior: Switching to targeted attack behavior");
            return new TargetedAttackBehavior(actor, lastAttackedBy.getActor());
        }

        return this;
    }

    @Override
    public String getDescription() {
        return "Walking randomly";
    }
}
