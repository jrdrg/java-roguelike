package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.items.Inventory;
import roguelike.items.Item;
import roguelike.maps.MapArea;
import squidpony.squidcolor.SColor;

public class GetItemAction extends Action {

	private MapArea map;
	private Item item;

	public GetItemAction(Actor actor, MapArea map) {
		super(actor);
		this.map = map;
	}

	public GetItemAction(Actor actor, MapArea map, Item item) {
		super(actor);
		this.map = map;
		this.item = item;
	}

	@Override
	protected ActionResult onPerform() {
		if (item != null) {

			Inventory inventory = map.getItemsAt(actor.getPosition().x, actor.getPosition().y);
			Item pickUp = inventory.getItem(this.item.itemId());

			return pickUpItem(inventory, pickUp);

		} else {
			Inventory inventory = map.getItemsAt(actor.getPosition().x, actor.getPosition().y);

			if (!inventory.any())
				return ActionResult.failure().setMessage("No items here!");

			// TODO: display menu allowing player to choose item
			Item firstItem = inventory.getItem(inventory.getCount() - 1);
			return pickUpItem(inventory, firstItem);
		}
	}

	private ActionResult pickUpItem(Inventory inventory, Item pickUp) {
		if (pickUp != null) {

			actor.inventory().add(pickUp);
			inventory.remove(pickUp);

			Game.current().displayMessage(String.format("%s pick%s up %s", actor.getName(), actor.getVerbSuffix(), pickUp.getName(), SColor.LIGHT_BLUE));

			return ActionResult.success();
		}
		return ActionResult.failure().setMessage("Selected item doesn't exist...");
	}
}
