package roguelike.actions;

import roguelike.actors.Actor;

/**
 * Allows the character to hide, increasing the chances that an opponent will be unable to see him. This effect is lost
 * once the character makes a melee attack or is attacked. Ranged attacks have a percentage chance to stop hiding
 * depending on distance away as well as the type of terrain the attack was made from. The
 * 
 * chance is modified by the concealment value of the tile the character is currently on, and any opponents that come
 * into line of sight should make checks to see if they notice the hiding character.
 * 
 * Hide is influenced by the character's Hide skill, along with perception and willpower
 * 
 * @author john
 * 
 */
public class HideAction extends Action {

	protected HideAction(Actor actor) {
		super(actor);
		// TODO create HideAction
	}

	@Override
	protected ActionResult onPerform() {
		// TODO perform HideAction
		return null;
	}

}
