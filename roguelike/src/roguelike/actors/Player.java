package roguelike.actors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actions.Action;
import roguelike.actors.behaviors.PlayerInputBehavior;
import roguelike.util.ActorUtils;
import squidpony.squidcolor.SColor;

public class Player extends Actor {
    private static final Logger LOG = LogManager.getLogger(Player.class);
    
	private static final long serialVersionUID = 1L;

	private String characterName;

	public Player() {
		super('@', SColor.WHITE);
		// TODO: load these during character creation somewhere
		this.statistics().speed.setBase(20);

		this.behavior = new PlayerInputBehavior(this);
	}

	public static boolean isPlayer(Actor actor) {
		return actor instanceof Player;
	}

	public String getCharacterName() {
		return characterName;
	}

	@Override
	public Action getNextAction() {
		Action action = behavior.getAction();
		return action;
	}

	@Override
	public String getName() {
		return "you";
	}

	@Override
	public String getMessageName() {
		return getName();
	}

	@Override
	public String getVerbSuffix() {
		return "";
	}

	@Override
	public String getDescription() {
		return "The player";
	}

	@Override
	public int getVisionRadius() {
		return visionRadius;
	}

	@Override
	public void onAttackedInternal(Actor attacker) {
		LOG.debug("Player attacked by {}", attacker.getName());
		LOG.debug("AttackedBy count = {}", attackedBy.size());
	}

	@Override
	public void onKilled() {
		// back to title screen
	}

	@Override
	protected String makeCorrectVerb(String message) {
		return ActorUtils.makePlayerText(message);
	}

}
