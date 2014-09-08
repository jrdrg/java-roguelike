package roguelike.items;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory {

	private ArrayList<Item> items;

	public Inventory() {
		items = new ArrayList<Item>();
	}

	public boolean any() {
		return items.size() > 0;
	}

	public int getCount() {
		return items.size();
	}

	public Item getItem(int index) {
		return items.get(index);
	}

	public Item getItem(UUID itemId) {
		for (Item i : items)
			if (i.itemId() == itemId) {
				return i;
			}
		return null;
	}

	public List<Item> getDroppableItems() {
		ArrayList<Item> droppable = new ArrayList<>();
		for (Item i : items) {
			if (i.isDroppable()) {
				droppable.add(i);
			}
		}
		return droppable;
	}

	public List<Item> allItems() {
		return items;
	}

	public void add(Item item) {
		items.add(item);
	}

	public boolean remove(Item item) {
		return items.remove(item);
	}
}
