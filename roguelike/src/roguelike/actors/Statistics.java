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

	public final Statistic speed = new Statistic(0, 0);
	public final Statistic strength = new Statistic(0, 0);
	public final Statistic agility = new Statistic(0, 0);
}
