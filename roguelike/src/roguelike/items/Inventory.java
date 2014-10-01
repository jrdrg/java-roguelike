package roguelike.items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Inventory implements Serializable {
	private static final long serialVersionUID = 2003563004618547276L;

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
		if (item == null)
			throw new IllegalArgumentException("item cannot be null");

		items.add(item);
	}

	public boolean remove(Item item) {
		if (item == null)
			throw new IllegalArgumentException("item cannot be null");

		return items.remove(item);
	}

	public String[] getItemListAsText(int maxSize) {
		boolean displayEllipsis = false;
		if (maxSize < this.items.size()) {
			displayEllipsis = true;
		}
		String[] items = new String[Math.min(maxSize, this.items.size())];

		for (int i = 0; i < items.length; i++) {
			items[i] = this.items.get(i).getName();
		}
		if (displayEllipsis)
			items[items.length - 1] = String.format("(%d more)", this.items.size() - maxSize);

		return items;
	}
}
