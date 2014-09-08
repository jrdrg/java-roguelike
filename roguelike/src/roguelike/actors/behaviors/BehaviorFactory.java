package roguelike.actors.behaviors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import roguelike.actors.Actor;

public class BehaviorFactory {

	public Behavior create(String behavior, Actor actor) {
		return getBehavior(behavior, actor);
	}

	private Behavior getBehavior(String behavior, Actor actor) {
		Class<?> behaviorType;
		Constructor<?> constructor;
		Object instance = null;
		try {
			behaviorType = Class.forName("roguelike.actors.behaviors." + behavior + "Behavior");
			constructor = behaviorType.getConstructor(String.class, Integer.class);
			instance = constructor.newInstance("actor", actor);
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
