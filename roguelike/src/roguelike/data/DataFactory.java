package roguelike.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roguelike.actors.Actor;
import roguelike.actors.behaviors.Behavior;

/**
 * Loads all config data from JSON file and provides a central repository
 * 
 * @author john
 * 
 */
public class DataFactory {

	private static DataFactory instance = new DataFactory();
	private Map<String, EnemyData> monsterData;
	private Map<String, WeaponData> weaponData;

	private DataFactory() {
		monsterData = new HashMap<String, EnemyData>();
		weaponData = new HashMap<String, WeaponData>();

		initWeapons();
		initMonsters();
	}

	public static DataFactory instance() {
		return instance;
	}

	public Map<String, EnemyData> getMonsters() {
		return monsterData;
	}

	public Map<String, WeaponData> getWeapons() {
		return weaponData;
	}

	public EnemyData getMonster(String key) {
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

	private void initWeapons() {
		List<WeaponData> weapons = WeaponData.create();
		for (WeaponData data : weapons) {
			weaponData.put(data.name, data);
		}
	}

	private void initMonsters() {
		List<EnemyData> monsters = EnemyData.create();
		for (EnemyData data : monsters) {
			monsterData.put(data.name, data);
		}
	}
}
