package roguelike.monsters;

import java.util.Map;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Npc;
import roguelike.actors.behaviors.Behavior;
import roguelike.actors.behaviors.RandomWalkBehavior;
import roguelike.data.DataFactory;
import roguelike.data.MonsterData;
import roguelike.data.WeaponData;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryBuilder;
import roguelike.items.Item;
import roguelike.items.Weapon;
import roguelike.items.WeaponFactory;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidmath.RNG;

public class MonsterFactory {

	private static InventoryBuilder inventoryBuilder = new InventoryBuilder();

	public static Actor createMonster(int x, int y) {
		Npc npc = getRandomMonster();

		npc.getStatistics().speed.setBase(10).setBonus(0);
		npc.setBehavior(new RandomWalkBehavior(npc));

		/* if we have any items, equip the first weapon */
		if (npc.getInventory().getCount() > 0) {
			Item first = npc.getInventory().getItem(0);
			if (first instanceof Weapon) {
				npc.getEquipment().equipItem(ItemSlot.RIGHT_ARM, first, npc.getInventory());
			}
		}

		npc.setPosition(x, y);
		return npc;
	}

	private static Map<String, MonsterData> getMonsterData() {
		return DataFactory.instance().getMonsters();
	}

	private static Npc getRandomMonster() {
		RNG rng = Game.current().random();
		MonsterData data = null;

		String key = "";

		int type = rng.between(0, 3);
		switch (type) {
		case 0:
			SColor color = SColorFactory.colorForName("Bright Pink");
			Npc bandit = new Npc('b', color, "Bandit");
			inventoryBuilder.populateRandomInventory(bandit);
			return bandit;
		case 1:
			key = "fireAnt";

		case 2:
		default:
			key = "wolf";
		}

		data = getMonsterData().get(key);
		Npc monster = createMonster(data);
		return monster;
	}

	private static Npc createMonster(MonsterData data) {
		Npc monster = new Npc(data.symbol, data.color, data.name);
		Behavior behavior = DataFactory.createBehavior(data.behavior, monster);
		monster.setBehavior(behavior);

		if (data.weapon != null) {
			if (data.weapon.equals("Random")) {
				/* create random weapon(s) */

			} else {
				WeaponData wData = DataFactory.instance().getWeapons().get(data.weapon);
				Weapon defWpn = WeaponFactory.create(wData);

				if (defWpn != null) {
					monster.getInventory().add(defWpn);
				}
				else {
					System.out.println("No default weapon for " + monster.getName());
				}
			}
		}
		return monster;
	}
}
