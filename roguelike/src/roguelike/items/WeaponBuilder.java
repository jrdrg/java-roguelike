package roguelike.items;

import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.conditions.Condition;
import roguelike.functionalInterfaces.StatisticProvider;
import squidpony.squidcolor.SColor;

public class WeaponBuilder {

	private Weapon weapon;

	private WeaponBuilder() {

	}

	public static WeaponBuilder melee(String name, String attackDescription, char symbol, SColor color) {
		WeaponBuilder wb = new WeaponBuilder();
		wb.weapon = new MeleeWeapon();
		wb.weapon.name = name;
		wb.weapon.attackDescription = attackDescription;
		wb.weapon.symbol = symbol;
		wb.weapon.color = color;
		return wb;
	}

	public static WeaponBuilder ranged(String name, String attackDescription, char symbol, SColor color, int maxRange, WeaponCategory projectileType) {
		WeaponBuilder wb = new WeaponBuilder();
		RangedWeapon w = new RangedWeapon();
		w.name = name;
		w.attackDescription = attackDescription;
		w.symbol = symbol;
		w.color = color;
		w.maxRange = maxRange;

		if (projectileType == null) {
			w.projectileType = null;
			w.requiresProjectiles = false;
		} else {
			w.projectileType = projectileType;
			w.requiresProjectiles = true;
		}

		wb.weapon = w;
		return wb;
	}

	public static WeaponBuilder projectile(String name, String attackDescription, char symbol, SColor color) {
		WeaponBuilder wb = new WeaponBuilder();
		Projectile w = new Projectile();
		w.name = name;
		w.attackDescription = attackDescription;
		w.symbol = symbol;
		w.color = color;

		wb.weapon = w;
		return wb;
	}

	public WeaponBuilder withDescription(String description) {
		weapon.description = description;
		return this;
	}

	public WeaponBuilder withCategory(WeaponCategory category) {
		weapon.weaponCategory = category;
		return this;
	}

	public WeaponBuilder withTargetNumberAndDamageValue(DamageType damageType, int targetNumber, int damageValue) {
		weapon.damage.put(damageType, new int[] { targetNumber, damageValue });
		return this;
	}

	public WeaponBuilder withDefenseTargetNumber(int targetNumber) {
		weapon.defenseTargetNumber = targetNumber;
		return this;
	}

	public WeaponBuilder canCauseCondition(Condition condition, int attackSuccesses, StatisticProvider statistic, int attributeSuccesses) {
		weapon.canCauseCondition = condition;
		weapon.attackSuccessesToCause = attackSuccesses;
		weapon.defenseAgainstConditionStat = statistic;
		weapon.attributeSuccessesToDefend = attributeSuccesses;
		return this;
	}

	public WeaponBuilder withReach(MeleeRange reach) {
		weapon.reach = reach.reach;
		return this;
	}

	public WeaponBuilder withDefaultDamageType(DamageType damageType) {
		weapon.defaultDamageType = damageType;
		return this;
	}

	public WeaponBuilder withDroppable(boolean droppable) {
		weapon.droppable = droppable;
		return this;
	}

	public Weapon build() {
		return weapon;
	}
}
