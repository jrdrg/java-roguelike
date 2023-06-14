package roguelike.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actions.combat.Attack;
import roguelike.actors.Actor;

public class AttackAction extends Action {
    private static final Logger LOG = LogManager.getLogger(AttackAction.class);

	private Actor target;

	public AttackAction(Actor actor, Actor target) {
		super(actor);
		this.target = target;
	}

	@Override
	protected ActionResult onPerform() {
		if (target != null) {
			return attackTarget();
		} else {
			// choose target
			return null;
		}
	}

	private ActionResult attackTarget() {
		if (!actor.isAlive()) {
			LOG.warn(">>> onPerform() >>> Actor  = {} is dead.", actor.getName());
			return ActionResult.failure().setMessage("Actor " + actor.getName() + " is dead!");
		}
		if (!target.isAlive()) {
			return ActionResult.alternate(new WaitAction(actor)).setMessage(">>> onPerform() >>> Target " + target.getName() + " is dead!");
		}

		Attack attack = actor.combatHandler().getAttack(target);
		if (attack != null) {
			attack.perform(this, target);
			return ActionResult.success();
		}
		else {
			/*
			 * we can't attack but success the action anyway so it causes the actor to lose energy and advance to the
			 * next actor
			 */
			return ActionResult.success().setMessage("No weapon");
		}
	}
}
