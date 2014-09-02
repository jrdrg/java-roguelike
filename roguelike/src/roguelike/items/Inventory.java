package roguelike.items;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

	private ArrayList<Item> items;

	public Inventory() {
		items = new ArrayList<Item>();
	}

	public Item getItem(int index) {
		return items.get(index);
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
