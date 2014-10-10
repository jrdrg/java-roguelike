package roguelike.items;

import roguelike.Game;
import roguelike.ui.Menu;
import roguelike.util.StringEx;

public class InventoryMenu extends Menu<Item> {

	public InventoryMenu(Inventory inventory) {
		super(inventory.allItems(), 26);
	}

	@Override
	protected StringEx getTextFor(Item item, int position) {

		String textLine = String.format("%-40s %12d", item.getDescription(), 111);

		return new StringEx(getCharForIndex(position) + ") " + textLine);
	}
}
