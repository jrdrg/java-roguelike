package roguelike.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roguelike.actors.Actor;
import roguelike.actors.Npc;
import roguelike.actors.behaviors.Behavior;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryBuilder;
import roguelike.items.Item;
import roguelike.items.Weapon;
import roguelike.items.WeaponFactory;
import squidpony.squidutility.SCollections;

public class EnemyFactory {

	private static InventoryBuilder inventoryBuilder = new InventoryBuilder();
	private static Map<Integer, List<EnemyData>> enemiesByDifficulty;

	public static Actor createEnemy(int x, int y, int difficulty) {
		Npc npc = getRandomEnemyByDifficulty(difficulty);

		/* if we have any items, equip the first weapon */
		if (npc.getInventory().getCount() > 0) {
			Item first = npc.getInventory().getItem(0);
			if (first instanceof Weapon) {
				ItemSlot.RIGHT_ARM.equipItem(npc, first);
			}
		}

		npc.setPosition(x, y);
		return npc;
	}

	private static Npc getRandomEnemyByDifficulty(int difficulty) {
		List<EnemyData> enemies = getEnemiesByDifficulty().get(difficulty);
		EnemyData data = SCollections.getRandomElement(enemies);
		Npc npc = createEnemy(data);
		return npc;
	}

	private static EnemyData getEnemyByType(String type, int difficulty) {
		// TODO: implement getEnemyByType
		return null;
	}

	private static Map<String, EnemyData> getEnemyData() {
		return DataFactory.instance().getMonsters();
	}

	private static Npc createEnemy(EnemyData data) {
		Npc enemy = new Npc(data);
		Behavior behavior = DataFactory.createBehavior(data.behavior, enemy);
		enemy.setBehavior(behavior);

		if (data.weapon != null) {
			if (data.weapon.equals("Random")) {
				/* create random weapon(s) */
				inventoryBuilder.populateRandomInventory(enemy);

			} else {
				Weapon defWpn = WeaponFactory.create(data.weapon);

				if (defWpn != null) {
					enemy.getInventory().add(defWpn);
				}
				else {
					System.out.println("No default weapon for " + enemy.getName());
				}
			}
		}
		return enemy;
	}

	private static Map<Integer, List<EnemyData>> getEnemiesByDifficulty() {
		if (enemiesByDifficulty == null) {
			enemiesByDifficulty = new HashMap<Integer, List<EnemyData>>();
			for (EnemyData e : getEnemyData().values()) {
				int difficulty = e.difficulty;
				List<EnemyData> enemiesOfSameDifficulty = enemiesByDifficulty.getOrDefault(difficulty, null);
				if (enemiesOfSameDifficulty == null) {
					enemiesOfSameDifficulty = new ArrayList<EnemyData>();
					enemiesByDifficulty.put(difficulty, enemiesOfSameDifficulty);
				}
				enemiesOfSameDifficulty.add(e);
			}
		}
		return enemiesByDifficulty;
	}
}
