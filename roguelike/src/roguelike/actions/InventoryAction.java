package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.windows.InventoryDialog;

public class InventoryAction extends Action {

	private InventoryMenu menu;

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

			Game.current().setActiveDialog(new InventoryDialog(menu));

		} else {
			InputCommand nextCommand = InputManager.nextCommandPreserveKeyData();
			if (nextCommand != null) {

				switch (nextCommand) {
				case CONFIRM:

					Item activeItem = menu.getActiveItem();
					if (activeItem != null) {

						// TODO: change this to something else, equip it for now
						ItemSlot.RIGHT_ARM.equipItem(actor, activeItem);

						return ActionResult.success().setMessage("Selected item: " + activeItem.getName());

					} else {
						return ActionResult.failure().setMessage("Empty inventory...");

					}

				case CANCEL:
					return ActionResult.failure().setMessage("Closed inventory menu");

				default:
					menu.processCommand(nextCommand);
				}
			}
		}
		return ActionResult.incomplete();
	}
}
