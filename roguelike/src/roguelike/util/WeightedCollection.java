package roguelike.util;

import java.util.ArrayList;
import java.util.Comparator;

import squidpony.squidutility.Pair;

public class WeightedCollection<T> {

	private final ArrayList<Pair<Integer, T>> table = new ArrayList<>();
	private int total = 0;

	/**
	 * Returns the first object whose weighted position is greater than the
	 * specified value.
	 * 
	 * Returns null if no elements have been put in the table.
	 * 
	 * @return
	 */
	public T getItem(int value) {
		if (table.isEmpty()) {
			return null;
		}
		int index = value;
		for (int i = 0; i < table.size(); i++) {// start looping at second item
			index -= table.get(i).getFirst();
			if (index < 0) {
				return table.get(i).getSecond();
			}
		}
		return null;// something went wrong, shouldn't have been able to get all
					// the way through without finding an item
	}

	/**
	 * Adds the given item to the table and sorts it.
	 * 
	 * Weight must be greater than 0.
	 * 
	 * @param item
	 * @param weight
	 */
	public void add(T item, int weight) {
		table.add(new Pair<Integer, T>(weight, item));
		total += weight;

		table.sort(new Comparator<Pair<Integer, T>>() {
			@Override
			public int compare(Pair<Integer, T> o1, Pair<Integer, T> o2) {
				return o1.getFirst().compareTo(o2.getFirst());
			}
		});
	}
}
