package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.Item;
import roguelike.items.RangedWeapon;

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

		if (item instanceof RangedWeapon) {
			ItemSlot.RANGED.equipItem(actor, item);
			result = ActionResult.success().setMessage("Equipped ranged weapon: " + item.getName());
		} else {
			itemSlot.equipItem(actor, item);
			result = ActionResult.success().setMessage("Selected item: " + item.getName());
		}

		return result;
	}

}
