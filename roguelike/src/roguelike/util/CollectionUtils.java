package roguelike.util;

import java.util.List;

import roguelike.Game;
import squidpony.squidmath.RNG;

public class CollectionUtils {
	private static final RNG rng = Game.current().random();

	/**
	 * Returns a random element from the provided list. If the list is empty then null is returned.
	 *
	 * @param <T>
	 * @param list
	 * @return
	 */
	public static <T> T getRandomElement(List<T> list) {
		if (list.size() <= 0) {
			return null;
		}
		return list.get(rng.nextInt(list.size()));
	}

	public static <T> T getRandomElement(T[] list) {
		if (list.length <= 0) {
			return null;
		}
		return list[rng.nextInt(list.length)];
	}

	private CollectionUtils() {
	}
}
