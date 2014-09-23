package roguelike.actions;

import roguelike.CursorResult;
import roguelike.Game;
import roguelike.actions.combat.Attack;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.items.RangedWeapon;
import roguelike.maps.MapArea;
import roguelike.ui.AttackCursor;
import roguelike.ui.InputCommand;
import roguelike.util.Log;
import squidpony.squidgrid.util.BasicRadiusStrategy;

public class RangedAttackAction extends InputRequiredAction<InputCommand> {

	private MapArea mapArea;
	private Actor target;
	private RangedWeapon weapon;

	public RangedAttackAction(Actor actor, MapArea mapArea, RangedWeapon weapon) {
		super(actor);
		this.mapArea = mapArea;
		this.weapon = weapon;

		int maxRange = 10; // TODO: replace with weapon's max range
		this.cursor = new AttackCursor(actor.getPosition(), mapArea, maxRange, BasicRadiusStrategy.CUBE);
		cursor.show();
	}

	@Override
	protected ActionResult onPerform() {

		CursorResult result = cursor.result();
		if (result.isCanceled())
			return ActionResult.failure().setMessage("Canceled");

		int x = result.position().x;
		int y = result.position().y;

		target = mapArea.getActorAt(x, y);
		if (target != null) {
			// TODO: insert animation here
			return attackTarget();
		}

		return ActionResult.success();
	}

	private ActionResult attackTarget() {
		if (!actor.isAlive()) {
			Log.warning(">>> onPerform() >>> Actor " + actor.getName() + " is dead!");
			return ActionResult.failure().setMessage("Actor " + actor.getName() + " is dead!");
		}
		if (!target.isAlive()) {
			return ActionResult.alternate(new RestAction(actor)).setMessage(">>> onPerform() >>> Target " + target.getName() + " is dead!");
		}

		Attack attack = weapon.getAttack();
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
