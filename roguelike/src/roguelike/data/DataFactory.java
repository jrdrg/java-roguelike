package roguelike.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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

	private DataFactory() {
	}

	public static DataFactory instance() {
		return instance;
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

}
