package roguelike.actions;

import roguelike.TurnEvent;
import roguelike.actions.combat.Attack;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.maps.MapArea;

public class AttackAction extends Action {

	private Actor target;

	public AttackAction(Actor actor, Actor target) {
		super(actor);
		this.target = target;
	}

	@Override
	protected ActionResult onPerform() {
		if (!actor.isAlive()) {
			System.out.println(">>> onPerform() >>> Actor " + actor.getName() + " is dead!");
			return ActionResult.failure().setMessage("Actor " + actor.getName() + " is dead!");
		}
		if (!target.isAlive()) {
			return ActionResult.alternate(new RestAction(actor)).setMessage(">>> onPerform() >>> Target " + target.getName() + " is dead!");
		}

		Attack attack = actor.getCombatHandler().getAttack(target);
		boolean isTargetDead = actor.getCombatHandler().processAttack(this, attack, target);

		if (isTargetDead) {
			target.onKilled();

			MapArea currentArea = actor.getGame().getCurrentMapArea();
			if (Player.isPlayer(target)) {
				target.getGame().reset();
			}
			currentArea.removeActor(target);

			actor.getGame().displayMessage("Target is dead");
		} else {
			actor.getGame().addEvent(TurnEvent.Attack(actor, target));
		}

		return ActionResult.success();
	}
}
