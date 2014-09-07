package roguelike.actors;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

public class Npc extends Actor {

	private Behavior behavior;
	private String name;

	public Npc(char symbol, SColor color, String name) {
		super(symbol, color);
		this.name = name;
	}

	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}

	@Override
	public Action getNextAction() {
		if (behavior != null) {
			return behavior.getAction();
		}
		return null;
	}

	@Override
	public String getName() {
		return name + " [" + getHealth().getCurrent() + "]";
	}

	@Override
	public void onAttacked(Actor attacker) {
		attackedBy.add(new AttackAttempt(attacker));
		if (behavior != null)
			behavior = behavior.getNextBehavior();
	}

	@Override
	public void onTurnFinished() {
		if (behavior != null) {
			behavior = behavior.getNextBehavior();
		}
	}

	@Override
	public void onKilled() {
		behavior = null;

		// chance to drop whatever is in inventory
		Inventory inventory = this.getInventory();
		MapArea map = Game.current().getCurrentMapArea();

		for (int x = 0; x < inventory.getCount(); x++) {

			if (Math.random() < 0.7) {

				Item i = inventory.getItem(x);
				map.addItem(i, getPosition().x, getPosition().y);

				Game.current().displayMessage("Dropped " + i.getName(), SColor.GREEN);
			}

		}
	}
}
