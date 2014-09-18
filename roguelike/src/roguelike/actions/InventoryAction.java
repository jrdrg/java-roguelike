package roguelike.actions;

import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.windows.DialogResult;
import roguelike.ui.windows.InventoryDialog;

public class InventoryAction extends Action {

	private InventoryMenu menu;
	private InventoryDialog dialog;

	public InventoryAction(Actor actor) {
		super(actor);

		this.usesEnergy = false;
	}

	@Override
	protected ActionResult onPerform() {
		/*
		 * if we don't have a menu drawn yet then just return incomplete to give the UI a chance to show it
		 */
		if (menu == null) {

			menu = new InventoryMenu(actor.getInventory());
			dialog = new InventoryDialog(menu);

			dialog.show();

		} else {

			DialogResult<Item> choice = dialog.result();
			if (choice != null) {

				ActionResult result;
				if (choice.isCanceled()) {

					result = ActionResult.failure().setMessage("Closed inventory menu");

				} else {

					Item activeItem = choice.item();
					if (activeItem != null) {

						// TODO: change this to something else, equip it for now
						ItemSlot.RIGHT_ARM.equipItem(actor, activeItem);
						result = ActionResult.success().setMessage("Selected item: " + activeItem.getName());

					} else {
						result = ActionResult.failure().setMessage("Empty inventory...");

					}
				}
				return result;
			}

		}
		return ActionResult.incomplete();
	}
}
