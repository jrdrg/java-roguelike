package roguelike.actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.CursorResult;
import roguelike.actions.combat.Attack;
import roguelike.actors.Actor;
import roguelike.items.RangedWeapon;
import roguelike.maps.MapArea;
import roguelike.ui.AttackCursor;
import roguelike.ui.InputCommand;
import squidpony.squidgrid.util.BasicRadiusStrategy;

/**
 * This is used by the player to display a target to choose which enemy to attack - Npc's can just use AttackActions.
 * 
 * @author john
 *
 */
public class RangedAttackAction extends CursorInputRequiredAction<InputCommand> {
    private static final Logger LOG = LogManager.getLogger(RangedAttackAction.class);

	private MapArea mapArea;
	private Actor target;
	private RangedWeapon weapon;

	public RangedAttackAction(Actor actor, MapArea mapArea, RangedWeapon weapon) {
		super(actor);
		this.mapArea = mapArea;
		this.weapon = weapon;

		int maxRange = Math.min(actor.getVisionRadius(), weapon.range());
		cursor = new AttackCursor(actor.getPosition(), mapArea, maxRange, BasicRadiusStrategy.CUBE);
		showCursor(cursor);
	}

	@Override
	protected ActionResult onPerform() {

		CursorResult result = cursor.result();
		if (result.isCanceled())
			return ActionResult.failure().setMessage("Canceled");

		int x = result.position().x;
		int y = result.position().y;

		target = mapArea.getActorAt(x, y);
		if (target != null && target != actor) {
			// TODO: insert Ranged Attack animation here
			return attackTarget();
		}

		return ActionResult.success();
	}

	private ActionResult attackTarget() {
		if (!actor.isAlive()) {
			LOG.warn(">>> onPerform() >>> Actor {} is dead!", actor.getName());
			return ActionResult.failure().setMessage("Actor " + actor.getName() + " is dead!");
		}
		if (!target.isAlive()) {
			return ActionResult.alternate(new WaitAction(actor)).setMessage(">>> onPerform() >>> Target " + target.getName() + " is dead!");
		}

		Attack attack = weapon.getAttack();
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
