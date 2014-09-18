package roguelike.actions;

import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.util.Log;

public class AttackAction extends Action {

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
			Log.warning(">>> onPerform() >>> Actor " + actor.getName() + " is dead!");
			return ActionResult.failure().setMessage("Actor " + actor.getName() + " is dead!");
		}
		if (!target.isAlive()) {
			return ActionResult.alternate(new RestAction(actor)).setMessage(">>> onPerform() >>> Target " + target.getName() + " is dead!");
		}

		Attack attack = actor.getCombatHandler().getAttack(target);
		if (attack != null) {
			boolean isTargetDead = attack.perform(this, target);

			if (isTargetDead) {
				target.onKilled();

				MapArea currentArea = Game.current().getCurrentMapArea();
				if (Player.isPlayer(target)) {
					target.finishTurn();
					Game.current().reset();
				}
				currentArea.removeActor(target);

				Game.current().displayMessage("Target is dead");
			}

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
