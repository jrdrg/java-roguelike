package roguelike.items;

import java.util.HashMap;
import java.util.Map;

import roguelike.actions.combat.Attack;
import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.conditions.Condition;
import roguelike.functionalInterfaces.StatisticProvider;
import roguelike.items.Equipment.ItemSlot;

public abstract class Weapon extends Item {
	private static final long serialVersionUID = -7712813593206574664L;

	protected Map<DamageType, int[]> damage = new HashMap<DamageType, int[]>();
	protected WeaponCategory weaponCategory;

	protected DamageType defaultDamageType;
	protected int baseDamage;
	protected String description;
	protected String attackDescription;
	protected int defenseTargetNumber;

	protected Condition canCauseCondition;
	protected StatisticProvider defenseAgainstConditionStat;
	protected int attackSuccessesToCause;
	protected int attributeSuccessesToDefend;

	/**
	 * This decrements periodically depending on usage, when it hits 0 the weapon breaks
	 */
	protected int durability;

	protected int reach;

	protected Weapon(boolean stackable) {
		super(stackable);
		damage.put(DamageType.SLASHING, new int[2]);
		damage.put(DamageType.PIERCING, new int[2]);
		damage.put(DamageType.BLUNT, new int[2]);
	}

	@Override
	public Weapon asWeapon() {
		return this;
	}

	@Override
	public boolean canEquip(ItemSlot slot) {
		return (slot == ItemSlot.LEFT_ARM || slot == ItemSlot.RIGHT_ARM);
	}

	public int reach() {
		return this.reach;
	}

	public WeaponCategory weaponType() {
		return this.weaponCategory;
	}

	public DamageType defaultDamageType() {
		return defaultDamageType;
	}

	public int getTargetNumber(DamageType type) {
		return damage.get(type)[0];
	}

	public int getDefenseTargetNumber() {
		return defenseTargetNumber;
	}

	public int getDamageRating(DamageType type) {
		return damage.get(type)[1];
	}

	public int getReachInTiles() {
		return (int) Math.max(1, Math.floor(reach / 2f));
	}

	public abstract Attack getAttack();
}
