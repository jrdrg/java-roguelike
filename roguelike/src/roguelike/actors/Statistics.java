package roguelike.actors;

import roguelike.data.EnemyData;
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
	 * The amount of damage done in melee, carrying capacity, ability to resist poison and disease, etc
	 */
	public final Statistic toughness = new Statistic(10, 0);

	/**
	 * Determines how quickly the character gets tired, hungry, thirsty
	 */
	public final Statistic conditioning = new Statistic(10, 0);

	/**
	 * Affects the vision radius, as well as the ability to exploit weak spots, pick locks, and spot traps and other
	 * hidden things
	 */
	public final Statistic perception = new Statistic(10, 0);

	/**
	 * The ability to dodge attacks and avoid effects of traps
	 */
	public final Statistic agility = new Statistic(10, 0);

	/**
	 * Determines how the character resists various conditions, as well as resistance to sorcery
	 */
	public final Statistic willpower = new Statistic(10, 0);

	/**
	 * Influences whether opponents decide to flee from combat, attempts to bribe and intimidate, anything having to do
	 * with social interaction. Also influences the bonuses gained from taverns, brothels, etc.
	 */
	public final Statistic presence = new Statistic(10, 0);

	public int reflexBonus;

	public int aimingBonus;

	/**
	 * Determines how difficult it is to knock the character down or disarm him
	 * 
	 * @return
	 */
	public int knockdown() {
		return (toughness.getTotalValue() + conditioning.getTotalValue()) / 2;
	}

	/**
	 * Determines how quickly the character can respond to an attack - this is the base number of dice that will be
	 * rolled to determine successes for a melee attack
	 * 
	 * @return
	 */
	public int reflexes() {
		return ((perception.getTotalValue() + agility.getTotalValue()) / 2) + reflexBonus;
	}

	/**
	 * Determines how effective the character is at aiming ranged weapons - this is the base number of dice that will be
	 * rolled to determine successes for a ranged attack
	 * 
	 * @return
	 */
	public int aiming() {
		return ((perception.getTotalValue() + willpower.getTotalValue()) / 2) + aimingBonus;
	}

	public int baseMeleePool(int weaponProficiency) {
		return reflexes() + weaponProficiency;
	}

	public int baseRangedPool(int weaponProficiency) {
		return aiming() + weaponProficiency;
	}

	public int baseEvadePool() {
		// get evade type
		// - partial evade: -2 to MP for this turn, but TN=6
		// - full evade: 0 MP penalty, TN=7

		return reflexes();
	}

	void setValues(EnemyData data) {
		this.speed.setBase(data.speed);
		this.toughness.setBase(data.toughness);
		this.conditioning.setBase(data.conditioning);
		this.perception.setBase(data.perception);
		this.agility.setBase(data.agility);
		this.willpower.setBase(data.willpower);
		this.presence.setBase(data.presence);

		this.reflexBonus = data.reflexBonus;
		this.aimingBonus = data.aimingBonus;
	}
}
