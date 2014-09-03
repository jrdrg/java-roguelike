package roguelike.items;

import java.awt.event.KeyEvent;

public class InventoryMenu {

	private Inventory inventory;
	private int activeIndex;

	public InventoryMenu(Inventory inventory) {
		this.inventory = inventory;
		this.activeIndex = 0;
	}

	public void processKey(KeyEvent key) {

		if (inventory.getCount() > 0) {

			switch (key.getKeyCode()) {
			case KeyEvent.VK_UP:
				activeIndex = Math.max(0, activeIndex - 1);
				break;
			case KeyEvent.VK_DOWN:
				activeIndex = Math.min(inventory.allItems().size() - 1, activeIndex + 1);
				break;
			}
		}
		System.out.println("Active item index: " + activeIndex);
	}

	public Item getActiveItem() {
		if (activeIndex >= 0)
			return inventory.getItem(activeIndex);

		return null;
	}
}
