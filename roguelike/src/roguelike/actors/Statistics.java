package roguelike.actors;

import squidpony.squidutility.Pair;

public class Statistics {
	public class Statistic {
		private Pair<Integer, Integer> stat;

		public int getBase() {
			return stat.getFirst();
		}

		public int getBonus() {
			return stat.getSecond();
		}

		public Statistic(int base, int bonus) {
			this.stat = new Pair<Integer, Integer>(base, bonus);
		}

		public Statistic setBase(int base) {
			stat.setFirst(base);
			return this;
		}

		public Statistic setBonus(int bonus) {
			stat.setSecond(bonus);
			return this;
		}

		public int getTotalValue() {
			return stat.getFirst() + stat.getSecond();
		}
	}

	/**
	 * How fast the character's turn occurs
	 */
	public final Statistic speed = new Statistic(10, 0);

	/**
	 * The amount of damage done in melee, carrying capacity, ability to resist
	 * poison and disease, etc
	 */
	public final Statistic toughness = new Statistic(10, 0);

	/**
	 * Determines how quickly the character gets tired, hungry, thirsty
	 */
	public final Statistic conditioning = new Statistic(10, 0);

	/**
	 * Affects the vision radius, as well as the ability to exploit weak spots,
	 * pick locks, and spot traps and other hidden things
	 */
	public final Statistic perception = new Statistic(10, 0);

	/**
	 * The ability to dodge attacks
	 */
	public final Statistic quickness = new Statistic(10, 0);

	/**
	 * Determines how the character resists various conditions, as well as
	 * resistance to sorcery
	 */
	public final Statistic willpower = new Statistic(10, 0);

	/**
	 * Influences whether opponents decide to flee from combat, attempts to
	 * bribe and intimidate, anything having to do with social interaction
	 */
	public final Statistic presence = new Statistic(10, 0);
}
