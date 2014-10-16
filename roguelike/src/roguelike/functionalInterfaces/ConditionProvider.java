package roguelike.functionalInterfaces;

import roguelike.actors.conditions.Condition;

public interface ConditionProvider {
	public Condition get(int duration);
}
