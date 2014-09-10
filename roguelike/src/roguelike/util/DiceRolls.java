package roguelike.util;

import roguelike.Game;
import squidpony.squidmath.RNG;

public class DiceRolls {
	private final static int TARGET_NUMBER = 6;
	private final static int DICE_TYPE = 10;

	/**
	 * Makes the number of rolls indicated and returns the amount of successes
	 * 
	 * @param poolSize
	 */
	public static int roll(int poolSize) {
		RNG rng = Game.current().random();

		int successes = 0;
		for (int x = 0; x < poolSize; x++) {
			int result = rng.between(1, DICE_TYPE + 1); // +1 because max is
														// exclusive
			if (result > TARGET_NUMBER) {
				successes++;
			}
		}
		return successes;
	}
}
