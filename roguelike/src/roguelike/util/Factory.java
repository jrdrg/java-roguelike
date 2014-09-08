package roguelike.util;

import roguelike.actors.Actor;

public abstract class Factory<T> {

	public abstract T create(Actor actor);
}
