package roguelike.items;

import roguelike.ui.Menu;
import roguelike.util.StringEx;

public class InventoryMenu extends Menu<Item> {

	public InventoryMenu(Inventory inventory) {
		super(inventory.allItems(), 3);
	}

	@Override
	protected StringEx getTextFor(Item item, int position) {
		return new StringEx(getCharForIndex(position) + ") " + item.getDescription());
	}
}
