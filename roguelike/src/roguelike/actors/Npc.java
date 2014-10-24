package roguelike.actors;

import java.util.List;

import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

public class Npc extends Actor {
	private static final long serialVersionUID = 1L;

	protected String name = "";
	protected String description = "";
	int difficulty = 1;

	Npc(char symbol, SColor color, String name) {
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
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void onAttackedInternal(Actor attacker) {
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
		Inventory inventory = this.inventory();
		MapArea map = game.getCurrentMapArea();

		List<Item> droppableItems = inventory.getDroppableItems();

		for (int x = 0; x < droppableItems.size(); x++) {

			if (game.random().nextFloat() < 0.7) {

				Item i = droppableItems.get(x);
				map.addItem(i, getPosition().x, getPosition().y);

				game.displayMessage("Dropped " + i.name(), SColor.GREEN);
			}

		}
	}

	@Override
	protected String makeCorrectVerb(String message) {
		return message;
	}
}
