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
				activeIndex = Math.min(inventory.getCount() - 1, activeIndex + 1);
				break;

			default:
				activeIndex = Math.min(inventory.getCount() - 1, Math.max(0, getIndexOfChar(key)));
			}
		}
	}

	public Item getActiveItem() {
		if (activeIndex >= 0)
			return inventory.getItem(activeIndex);

		return null;
	}

	public Item getItemAt(int index) {
		if (index >= 0)
			return inventory.getItem(index);

		return null;
	}

	public int getActiveItemIndex() {
		return activeIndex;
	}

	public int totalItems() {
		return inventory.getCount();
	}

	private int getIndexOfChar(KeyEvent key) {
		char keyChar = key.getKeyChar();
		if (keyChar >= 97 && keyChar <= 122)
			return keyChar - 97; // a-z, returns 0-26
		if (keyChar >= 65 && keyChar <= 90)
			return keyChar - 65; // A-Z, returns 0-26

		return -1; // invalid character pressed
	}
}
