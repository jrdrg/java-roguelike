package roguelike.actions;

import roguelike.DialogResult;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.items.RangedWeapon;
import roguelike.ui.windows.InventoryDialog;

public class InventoryAction extends InputRequiredAction<Item> {

	public InventoryAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;
		dialog = new InventoryDialog(new InventoryMenu(actor.getInventory()));
		dialog.show();
	}

	@Override
	protected ActionResult onPerform() {

		DialogResult<Item> choice = dialog.result();
		if (choice != null) {

			ActionResult result;
			if (choice.isCanceled()) {

				result = ActionResult.failure().setMessage("Closed inventory menu");

			} else {

				Item activeItem = choice.item();
				if (activeItem != null) {

					// TODO: change this to something else, equip it for now
					if (activeItem instanceof RangedWeapon) {
						ItemSlot.RANGED.equipItem(actor, activeItem);
						result = ActionResult.success().setMessage("Equipped ranged weapon: " + activeItem.getName());
					} else {
						ItemSlot.RIGHT_ARM.equipItem(actor, activeItem);
						result = ActionResult.success().setMessage("Selected item: " + activeItem.getName());
					}

				} else {
					result = ActionResult.failure().setMessage("Empty inventory...");

				}
			}
			return result;
		}
		return ActionResult.incomplete();
	}
}
