package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Item;
import roguelike.items.ItemType;

public class EquipItemAction extends Action {

	private Item item;
	private ItemSlot itemSlot;

	public EquipItemAction(Actor actor, Item item, ItemSlot itemSlot) {
		super(actor);

		this.item = item;
		this.itemSlot = itemSlot;
	}

	@Override
	protected ActionResult onPerform() {
		ActionResult result = ActionResult.incomplete();

		if (item.type() == ItemType.RANGED_WEAPON) {
			ItemSlot.RANGED.equipItem(actor, item);
			result = ActionResult.success().setMessage("Equipped ranged weapon: " + item.name());
		} else {
			itemSlot.equipItem(actor, item);
			result = ActionResult.success().setMessage("Selected item: " + item.name());
		}

		return result;
	}

}
