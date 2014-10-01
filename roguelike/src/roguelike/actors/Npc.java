package roguelike.actors;

import java.util.List;

import roguelike.Game;
import roguelike.actions.Action;
import roguelike.actors.behaviors.Behavior;
import roguelike.data.EnemyData;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

public class Npc extends Actor {
	private static final long serialVersionUID = 3959590031674627194L;

	private Behavior behavior;
	private String name;

	public Npc(char symbol, SColor color, String name) {
		super(symbol, color);
		this.name = name;
	}

	public Npc(EnemyData data) {
		super(data.symbol(), data.color());
		this.name = data.name;

		Statistics stats = statistics();
		stats.setValues(data);

		this.health().setMaximum(data.health);
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
		// return name + " [" + getHealth().getCurrent() + "]";
		return name;
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
		MapArea map = Game.current().getCurrentMapArea();

		List<Item> droppableItems = inventory.getDroppableItems();

		for (int x = 0; x < droppableItems.size(); x++) {

			if (Game.current().random().nextFloat() < 0.7) {

				Item i = droppableItems.get(x);
				map.addItem(i, getPosition().x, getPosition().y);

				Game.current().displayMessage("Dropped " + i.getName(), SColor.GREEN);
			}

		}
	}
}
