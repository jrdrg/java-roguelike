package roguelike.monsters;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import roguelike.Game;
import roguelike.MainScreen;
import roguelike.actors.Actor;
import roguelike.actors.Npc;
import roguelike.actors.behaviors.RandomWalkBehavior;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.InventoryBuilder;
import roguelike.items.Item;
import roguelike.items.MeleeWeapon;
import roguelike.items.Weapon;
import roguelike.util.Factory;
import roguelike.util.FileUtils;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidmath.RNG;

public class MonsterFactory {

	private static InventoryBuilder inventoryBuilder = new InventoryBuilder();
	private static Map<String, MonsterData> monsterData;

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
		if (monsterData == null) {
			MonsterFactory.monsterData = new HashMap<String, MonsterData>();
			initMonsterData();
		}
		return monsterData;
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
		monster.setBehavior(data.defaultBehavior.create(monster));
		if (data.defaultWeapon != null) {
			Weapon defWpn = data.defaultWeapon.create(monster);
			monster.getInventory().add(defWpn);
		}
		return monster;
	}

	private static void initMonsterData() {

		InputStream monsterConfig = MainScreen.class.getResourceAsStream("/resources/config/monsters.json");
		String configString = "";
		try {
			configString = FileUtils.readFile(monsterConfig);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* load monster data from JSON file */
		JSONObject jObj = new JSONObject(configString);
		Set<String> keys = jObj.keySet();
		for (String key : keys) {

			JSONObject dataObj = jObj.getJSONObject(key);
			String name = dataObj.getString("name");
			char symbol = dataObj.getString("symbol").charAt(0);
			SColor color = SColorFactory.colorForName(dataObj.getString("color"));
			color = color == null ? SColor.WHITE : color;
			int speed = dataObj.getInt("speed");

			MonsterData data = new MonsterData(symbol, color, name);
			data.behavior = dataObj.getString("behavior");
			data.speed = speed;

			getMonsterData().put(name, data);
		}

		MonsterData bandit = new MonsterData('b', SColor.BRIGHT_PINK, "Bandit");
		bandit.behavior = "RandomWalk";
		bandit.speed = 10;
		getMonsterData().put("bandit", bandit);

		/***************/

		MonsterData wolf = new MonsterData('w', SColor.LIGHT_GRAY, "Wolf");
		wolf.behavior = "RandomWalk";
		wolf.speed = 20;
		wolf.defaultWeapon = new Factory<Weapon>() {

			@Override
			public Weapon create(Actor actor) {
				Weapon bite = new MeleeWeapon(
						"Bite", "The bite of a wolf", 10, "%s bites %s");
				return bite;
			}
		};
		getMonsterData().put("wolf", wolf);

		/***************/

		MonsterData fireAnt = new MonsterData('a', SColor.RED, "Fire ant");
		fireAnt.behavior = "RandomWalk";
		fireAnt.speed = 15;
		fireAnt.defaultWeapon = new Factory<Weapon>() {

			@Override
			public Weapon create(Actor actor) {
				Weapon mandibles = new MeleeWeapon(
						"Mandibles", "Vicious ant-like jaws", 10, "%s snaps its mandibles at %s");

				return mandibles;
			}
		};
		getMonsterData().put("fireAnt", fireAnt);

	}
}
