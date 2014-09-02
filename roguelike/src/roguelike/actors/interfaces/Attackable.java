package roguelike.actors.interfaces;

import roguelike.actors.Actor;

public interface Attackable {

	void onAttacked(Actor attacker);
}
