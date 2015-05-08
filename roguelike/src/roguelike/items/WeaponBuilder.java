package roguelike.items;

import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.conditions.Condition;
import roguelike.functionalInterfaces.StatisticProvider;
import roguelike.items.Equipment.ItemSlot;
import squidpony.squidcolor.SColor;

public class WeaponBuilder extends ItemBuilder {

	private Weapon weapon;

	private WeaponBuilder(Weapon weapon) {
		super(weapon);

		this.weapon = weapon;
	}

	private Weapon weapon() {
		return (Weapon) item;
	}

	public static WeaponBuilder melee(String name, String attackDescription, char symbol, SColor color) {
		Weapon weapon = new MeleeWeapon();
		weapon.name = name;
		weapon.attackDescription = attackDescription;
		weapon.symbol = symbol;
		weapon.color = color;

		WeaponBuilder wb = new WeaponBuilder(weapon);
		return wb;
	}

	public static WeaponBuilder ranged(String name, String attackDescription, char symbol, SColor color, int maxRange, WeaponCategory projectileType) {
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

		WeaponBuilder wb = new WeaponBuilder(w);
		return wb;
	}

	public static WeaponBuilder projectile(String name, String attackDescription, char symbol, SColor color) {
		Projectile w = new Projectile();
		w.name = name;
		w.attackDescription = attackDescription;
		w.symbol = symbol;
		w.color = color;

		WeaponBuilder wb = new WeaponBuilder(w);
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

	public WeaponBuilder canEquip(ItemSlot equippable) {
		weapon.equippable = equippable;
		return this;
	}

	public WeaponBuilder withReach(MeleeRange reach) {
		weapon.reach = reach.reach;
		return this;
	}

	public WeaponBuilder withRange(int range) {
		((RangedWeapon) weapon).maxRange = range;
		return this;
	}

	public WeaponBuilder withDefaultDamageType(DamageType damageType) {
		weapon.defaultDamageType = damageType;
		return this;
	}
	
	public WeaponBuilder withDroppable(boolean droppable) {
		return (WeaponBuilder) super.withDroppable(droppable);
	}
	
	public WeaponBuilder withWeight(int weight) {
		return (WeaponBuilder) super.withWeight(weight);
	}

	public Weapon build() {
		/* validate */
		if (weapon.defaultDamageType == null)
			throw new RuntimeException("Default damage type cannot be null");
		if (weapon.weaponCategory == null)
			throw new RuntimeException("Weapon category cannot be null");

		return weapon;
	}
}
