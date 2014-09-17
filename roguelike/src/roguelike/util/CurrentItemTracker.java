package roguelike.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrentItemTracker<T> {

	private ArrayList<T> list;
	private int currentItem;

	public CurrentItemTracker() {
		list = new ArrayList<T>();
	}

	public int count() {
		return list.size();
	}

	public void add(T item) {
		list.add(item);
	}

	public void remove(T item) {
		list.remove(item);
		currentItem = currentItem % list.size();
	}

	public List<T> getAll() {
		return Collections.unmodifiableList(list);
	}

	public T getCurrent() {
		return list.get(currentItem);
	}

	public T peek() {
		return list.get((currentItem + 1) % list.size());
	}

	public void advance() {
		currentItem = (currentItem + 1) % list.size();
	}
}
