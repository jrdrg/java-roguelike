package roguelike.data;

import java.util.HashMap;
import java.util.Map;

import roguelike.actors.Actor;
import roguelike.actors.EnemyType;
import roguelike.actors.Npc;
import roguelike.actors.NpcBuilder;
import roguelike.actors.behaviors.MoveToRandomPointBehavior;
import roguelike.actors.behaviors.SearchForPlayerBehavior;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryBuilder;
import roguelike.items.Item;
import roguelike.items.Weapon;
import roguelike.items.WeaponFactory;
import roguelike.items.WeaponType;
import roguelike.util.CollectionUtils;
import squidpony.squidcolor.SColor;

public class EnemyFactory {

	private interface NpcBuilderFactory {
		public Npc create();
	}

	private static EnemyFactory factory = new EnemyFactory();
	private static InventoryBuilder inventoryBuilder = new InventoryBuilder();

	private Map<EnemyType, NpcBuilderFactory> npcBuilders = new HashMap<EnemyType, EnemyFactory.NpcBuilderFactory>();

	private EnemyFactory() {

		npcBuilders.put(EnemyType.WOLF,
				() -> NpcBuilder
						.withIdentifiers("wolf", 'w', SColor.LIGHT_GRAY, 1)
						.withHealth(15)
						.withVisionRadius(16)
						.withDescription("It looks at you with sinister eyes and snarls menacingly.")
						.withSpeed(30)
						.withBehavior(a -> new SearchForPlayerBehavior(a))
						.withStats(5, 6, 6, 6, 4, 3)
						.equipItem(WeaponFactory.create(WeaponType.BITE), ItemSlot.RIGHT_ARM) // yes, this is funny
						.buildNpc());

		npcBuilders.put(EnemyType.FIRE_ANT,
				() -> NpcBuilder
						.withIdentifiers("fire ant", 'f', SColor.RED, 1)
						.withHealth(8)
						.withDescription("A large ant, whose bite is said to cause immense pain.")
						.withSpeed(15)
						.withBehavior(a -> new SearchForPlayerBehavior(a))
						.withStats(4, 3, 4, 4, 4, 4)
						.equipItem(WeaponFactory.create(WeaponType.MANDIBLES), ItemSlot.RIGHT_ARM)
						.buildNpc());

		npcBuilders.put(EnemyType.BANDIT,
				() -> NpcBuilder
						.withIdentifiers("bandit", 'b', SColor.BRIGHT_PINK, 1)
						.withHealth(25)
						.withDescription("Waiting in ambush for their victims, these scoundrels prey upon the weak.")
						.withSpeed(10)
						.withBehavior(a -> new SearchForPlayerBehavior(a))
						.withStats(6, 7, 7, 5, 6, 6)
						.withInventory(inventoryBuilder.populateRandomInventory())
						.buildNpc());

		npcBuilders.put(EnemyType.SNAKE,
				() -> NpcBuilder
						.withIdentifiers("snake", 's', SColor.LIME, 1)
						.withHealth(10)
						.withDescription("A small snake, slithering towards you.")
						.withSpeed(20)
						.withBehavior(a -> new SearchForPlayerBehavior(a))
						.withStats(4, 5, 9, 8, 3, 3)
						.equipItem(WeaponFactory.create(WeaponType.BITE), ItemSlot.RIGHT_ARM)
						.buildNpc());

		npcBuilders.put(EnemyType.ARCHER,
				() -> NpcBuilder
						.withIdentifiers("archer", 'a', SColor.LIGHT_MAROON, 1)
						.withHealth(20)
						.withDescription("Probably a deserter from some king's army.")
						.withSpeed(10)
						.withBehavior(a -> new MoveToRandomPointBehavior(a))
						.withStats(5, 4, 8, 8, 5, 4)
						.equipItem(WeaponFactory.create(WeaponType.SHORT_BOW), ItemSlot.RIGHT_ARM)
						.equipItem(WeaponFactory.create(WeaponType.ARROW), ItemSlot.PROJECTILE)
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.addItem(WeaponFactory.create(WeaponType.ARROW))
						.withAimingBonus(1)
						.buildNpc());

	}

	public static Actor createEnemy(int x, int y, int difficulty) {
		Npc npc = factory.getRandomEnemyByDifficulty(difficulty);

		/* if we have any items, equip the first weapon */
		if (npc.inventory().getCount() > 0) {
			Item first = npc.inventory().getItem(0);
			if (first instanceof Weapon) {
				ItemSlot.RIGHT_ARM.equipItem(npc, first);
			}
		}

		npc.setPosition(x, y);
		return npc;
	}

	private Npc getRandomEnemyByDifficulty(int difficulty) {
		EnemyType[] types = new EnemyType[] { EnemyType.WOLF, EnemyType.FIRE_ANT, EnemyType.BANDIT, EnemyType.SNAKE, EnemyType.ARCHER };

		EnemyType randomType = CollectionUtils.getRandomElement(types);
		NpcBuilderFactory factory = npcBuilders.get(randomType);

		Npc npc = factory.create();
		return npc;
	}

}
