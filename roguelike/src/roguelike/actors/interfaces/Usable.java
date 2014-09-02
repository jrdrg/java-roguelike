package roguelike.actors.interfaces;

import roguelike.actors.Actor;

public interface Usable {

	void onUse(Actor usedBy, Actor usedOn);
}
