package roguelike.actions;

import java.awt.event.KeyEvent;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.items.InventoryMenu;
import roguelike.items.Item;
import roguelike.items.Equipment.ItemSlot;
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
		 * if we don't have a menu drawn yet then just return incomplete to give
		 * the UI a chance to show it
		 */
		if (menu == null) {

			menu = new InventoryMenu(actor.getInventory());

			Game.current().setActiveDialog(new InventoryDialog(menu));

		} else {
			KeyEvent nextKey = InputManager.nextKey();
			if (nextKey != null) {

				switch (nextKey.getKeyCode()) {
				case KeyEvent.VK_ENTER:

					Item activeItem = menu.getActiveItem();
					if (activeItem != null) {

						// TODO: change this to something else, equip it for now
						actor.getEquipment().equipItem(ItemSlot.RIGHT_ARM, activeItem, actor.getInventory());

						return ActionResult.success().setMessage("Selected item: " + activeItem.getName());

					} else {
						return ActionResult.failure().setMessage("Empty inventory...");

					}

				case KeyEvent.VK_ESCAPE:
					return ActionResult.failure().setMessage("Closed inventory menu");

				}
				menu.processKey(nextKey);
			}
		}
		return ActionResult.incomplete();
	}
}
