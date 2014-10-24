package roguelike.items;

import java.util.HashMap;
import java.util.Map;

import roguelike.actions.combat.DamageType;
import roguelike.actions.combat.WeaponCategory;
import roguelike.actors.conditions.Poisoned;
import roguelike.items.Equipment.ItemSlot;
import squidpony.squidcolor.SColor;

public class WeaponFactory {

	private interface WeaponBuilderFactory {
		public Weapon create();
	}

	private static WeaponFactory factory = new WeaponFactory();

	private transient Map<WeaponType, WeaponBuilderFactory> weaponBuilders = new HashMap<WeaponType, WeaponBuilderFactory>();

	private WeaponFactory() {
		createNaturalWeapons();

		createMeleeWeapons();

		createRangedWeapons();

		createProjectiles();
	}

	public static Weapon create(WeaponType type) {
		WeaponBuilderFactory builderFactory = factory.weaponBuilders.get(type);
		if (builderFactory != null) {
			Weapon weapon = builderFactory.create();
			return weapon;
		}
		return null;
	}

	private void createMeleeWeapons() {

		weaponBuilders.put(WeaponType.AXE,
				() -> WeaponBuilder
						.melee("axe", "slashes at %s", ']', SColor.LIGHT_BLUE)
						.withDescription("A heavy axe, serviceable but dull.")
						.withCategory(WeaponCategory.AXE)
						.withDefaultDamageType(DamageType.SLASHING)
						.withTargetNumberAndDamageValue(DamageType.SLASHING, 6, 1)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 6, 0)
						.withReach(MeleeRange.MEDIUM)
						.withDroppable(true)
						.withDefenseTargetNumber(9)
						.build());

		weaponBuilders.put(WeaponType.DAGGER,
				() -> WeaponBuilder
						.melee("dagger", "stabs at %s", ']', SColor.LIGHT_BLUE)
						.withDescription("A small dagger, able to find weak points in any armor.")
						.withCategory(WeaponCategory.DAGGER)
						.withDefaultDamageType(DamageType.PIERCING)
						.withTargetNumberAndDamageValue(DamageType.PIERCING, 6, 1)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 9, -2)
						.withReach(MeleeRange.SHORT)
						.withDroppable(true)
						.withDefenseTargetNumber(8)
						.build());

		weaponBuilders.put(WeaponType.SHORT_SWORD,
				() -> WeaponBuilder
						.melee("short sword", "slashes at %s", ']', SColor.LIGHT_BLUE)
						.withDescription("It looks like this sword has seen much use.")
						.withCategory(WeaponCategory.SWORD)
						.withDefaultDamageType(DamageType.SLASHING)
						.withTargetNumberAndDamageValue(DamageType.SLASHING, 6, 1)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 8, 0)
						.withReach(MeleeRange.MEDIUM)
						.withDroppable(true)
						.withDefenseTargetNumber(8)
						.build());

		weaponBuilders.put(WeaponType.SPEAR,
				() -> WeaponBuilder
						.melee("spear", "stabs at %s", ']', SColor.LIGHT_BLUE)
						.withDescription("A solid looking spear, to keep your foes at bay.")
						.withCategory(WeaponCategory.SWORD)
						.withDefaultDamageType(DamageType.PIERCING)
						.withTargetNumberAndDamageValue(DamageType.PIERCING, 6, 2)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 8, 0)
						.withReach(MeleeRange.LONG)
						.withDroppable(true)
						.withDefenseTargetNumber(8)
						.build());
	}

	private void createRangedWeapons() {

		weaponBuilders.put(WeaponType.SHORT_BOW,
				() -> WeaponBuilder
						.ranged("short bow", "swings the bow at %s", ')', SColor.BRONZE, 15, WeaponCategory.ARROW)
						.withDescription("A short bow, able to send arrows flying a respectable distance.")
						.withCategory(WeaponCategory.BOW)
						.withDefaultDamageType(DamageType.BLUNT)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 9, 0)
						.withReach(MeleeRange.SHORT)
						.withDroppable(true)
						.withDefenseTargetNumber(9)
						.withRange(10)
						.build());
	}

	private void createProjectiles() {

		weaponBuilders.put(WeaponType.ARROW,
				() -> WeaponBuilder
						.projectile("arrow", "shoots %s", '/', SColor.BRONZE)
						.withDescription("A normal-looking arrow.")
						.withCategory(WeaponCategory.BOW)
						.withDefaultDamageType(DamageType.PIERCING)
						.withTargetNumberAndDamageValue(DamageType.PIERCING, 6, 0)
						.withReach(MeleeRange.SHORT)
						.withDroppable(true)
						.canEquip(ItemSlot.PROJECTILE)
						.build());
	}

	private void createNaturalWeapons() {

		weaponBuilders.put(WeaponType.BITE,
				() -> WeaponBuilder
						.melee("bite", "bites %s", 'b', SColor.YELLOW)
						.withDescription("A vicious bite that can cause serious damage.")
						.withCategory(WeaponCategory.NATURAL)
						.withDefaultDamageType(DamageType.PIERCING)
						.withTargetNumberAndDamageValue(DamageType.PIERCING, 6, 1)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 8, -1)
						.withReach(MeleeRange.CLOSE)
						.withDroppable(false)
						.withDefenseTargetNumber(8)
						.build());

		weaponBuilders.put(WeaponType.MANDIBLES,
				() -> WeaponBuilder
						.melee("mandibles", "slices %s with its mandibles", 'm', SColor.RED)
						.withDescription("Large razor sharp mandibles.")
						.withCategory(WeaponCategory.NATURAL)
						.withDefaultDamageType(DamageType.SLASHING)
						.withTargetNumberAndDamageValue(DamageType.SLASHING, 6, 1)
						.withTargetNumberAndDamageValue(DamageType.BLUNT, 7, 0)
						.withReach(MeleeRange.CLOSE)
						.withDroppable(false)
						.withDefenseTargetNumber(8)
						.canCauseCondition(new Poisoned(10), 2, s -> s.toughness, 1)
						.build());
	}

}
