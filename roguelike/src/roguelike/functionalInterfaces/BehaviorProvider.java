package roguelike.functionalInterfaces;

import roguelike.actors.Actor;
import roguelike.actors.behaviors.Behavior;

public interface BehaviorProvider {
	public Behavior create(Actor actor);
}
