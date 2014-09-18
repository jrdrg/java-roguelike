package roguelike.actions;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.ui.InputCommand;
import roguelike.ui.InputManager;
import roguelike.ui.Menu;
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

			InputManager.setActiveKeybindings(Menu.KeyBindings);

		} else {
			InputCommand nextCommand = InputManager.nextCommandPreserveKeyData();
			if (nextCommand != null) {

				ActionResult result;
				switch (nextCommand) {
				case CONFIRM:

					Item activeItem = menu.getActiveItem();
					if (activeItem != null) {

						// TODO: change this to something else, equip it for now
						ItemSlot.RIGHT_ARM.equipItem(actor, activeItem);

						result = ActionResult.success().setMessage("Selected item: " + activeItem.getName());

					} else {
						result = ActionResult.failure().setMessage("Empty inventory...");

					}

				case CANCEL:
					result = ActionResult.failure().setMessage("Closed inventory menu");

					// No break statement since we fall through to this from either cancel or confirm
					InputManager.setActiveKeybindings(InputManager.DefaultKeyBindings);
					return result;

				default:
					menu.processCommand(nextCommand);
				}
			}
		}
		return ActionResult.incomplete();
	}
}
