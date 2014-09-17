package roguelike.items;

import roguelike.ui.Menu;

public class InventoryMenu extends Menu<Item> {

	private Inventory inventory;

	public InventoryMenu(Inventory inventory) {
		super(inventory.allItems(), 3);
		this.inventory = inventory;
	}

	public int totalItems() {
		return inventory.getCount();
	}
}
