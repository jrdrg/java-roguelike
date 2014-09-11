package roguelike.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import roguelike.MainScreen;
import roguelike.actors.Actor;
import roguelike.actors.behaviors.Behavior;
import roguelike.util.FileUtils;

/**
 * Loads all config data from JSON file and provides a central repository
 * 
 * @author john
 * 
 */
public class DataFactory {

	private static DataFactory instance = new DataFactory();
	private Map<String, MonsterData> monsterData;
	private Map<String, WeaponData> weaponData;

	private DataFactory() {
		monsterData = new HashMap<String, MonsterData>();
		weaponData = new HashMap<String, WeaponData>();

		init();
		initWeapons();
		initMonsters();
	}

	public static DataFactory instance() {
		return instance;
	}

	public Map<String, MonsterData> getMonsters() {
		return monsterData;
	}

	public Map<String, WeaponData> getWeapons() {
		return weaponData;
	}

	public MonsterData getMonster(String key) {
		return monsterData.get(key);
	}

	public WeaponData getWeapon(String key) {
		return weaponData.get(key);
	}

	public static Behavior createBehavior(String behavior, Actor actor) {
		Class<?> behaviorType;
		Constructor<?> constructor;
		Object instance = null;
		try {
			behaviorType = Class.forName("roguelike.actors.behaviors." + behavior + "Behavior");
			constructor = behaviorType.getConstructor(Actor.class);
			if (constructor != null) {
				instance = constructor.newInstance(actor);
			}
			else {
				System.out.println("Couldn't create instance of " + behavior);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return (Behavior) instance;
	}

	private void init() {
		System.out.println("Initializing data from config files...");

		InputStream config = MainScreen.class.getResourceAsStream("/resources/config/data.json");
		String configString = "";
		try {
			configString = FileUtils.readFile(config);
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* load monster data from JSON file */
		JSONObject jObj = new JSONObject(configString);

		JSONArray tables = jObj.getJSONArray("tables");
		for (int t = 0; t < tables.length(); t++) {
			JSONObject jtable = tables.getJSONObject(t);
			String tableName = jtable.getString("name");

			System.out.println("Reading " + tableName + "...");

			JSONArray jData = jtable.getJSONArray("records");

			// if (tableName.equals("monsters")) {
			// readMonsters(jData);
			// }
		}
	}

	private void initWeapons() {
		List<WeaponData> weapons = WeaponData.create();
		for (WeaponData data : weapons) {
			weaponData.put(data.name, data);
		}
	}

	private void initMonsters() {
		List<MonsterData> monsters = MonsterData.create();
		for (MonsterData data : monsters) {
			monsterData.put(data.name, data);
		}
	}
}
