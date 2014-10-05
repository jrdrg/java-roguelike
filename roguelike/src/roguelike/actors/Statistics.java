package roguelike.actors;

import java.io.Serializable;

import roguelike.data.EnemyData;
import squidpony.squidutility.Pair;

public class Statistics implements Serializable {
	private static final long serialVersionUID = 7046880693122391945L;

	public class Statistic implements Serializable {
		private static final long serialVersionUID = -159928992533926261L;

		private int[] stat = new int[2];

		public int getBase() {
			return stat[0];
		}

		public int getBonus() {
			return stat[1];
		}

		public Statistic(int base, int bonus) {
			this.stat = new int[] { base, bonus };
		}

		public Statistic setBase(int base) {
			stat[0] = base;
			return this;
		}

		public Statistic setBonus(int bonus) {
			stat[1] = bonus;
			return this;
		}

		public int getTotalValue() {
			return stat[0] + stat[1];
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
