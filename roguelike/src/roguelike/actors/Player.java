package roguelike.actors;

import roguelike.actions.Action;
import roguelike.actors.behaviors.PlayerInputBehavior;
import roguelike.data.DataFactory;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;

public class Player extends Actor {
	private static final long serialVersionUID = -1439044065168378557L;

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
		Log.verboseDebug("Player attacked by " + attacker.getName());
		Log.verboseDebug("AttackedBy count=" + attackedBy.size());
	}

	@Override
	public void onKilled() {
		// back to title screen
	}

	@Override
	public void onSerialize(SerializationData data) {
		super.onSerialize(data);

		String behaviorName = behavior.getClass().getName().replace("roguelike.actors.behaviors.", "").replace("Behavior", "");

		data.setData("behavior", behaviorName);
	}

	@Override
	public void onDeserialize(SerializationData data) {
		super.onDeserialize(data);

		String behaviorName = (String) data.getData("behavior");
		Log.debug("loaded behavior=" + behaviorName);

		behavior = DataFactory.createBehavior(behaviorName, this);
	}

	@Override
	protected String makeCorrectVerb(String message) {
		return makeSecondPerson(message);
	}

	private String makeSecondPerson(String text) {
		String[] words = text.split(" ");

		if (words[0].equals("has")) {
			words[0] = "have";
		} else if (words[0].endsWith("es")) {
			words[0] = words[0].substring(0, words[0].length() - 1);
		}
		else if (words[0].endsWith("s")) {
			words[0] = words[0].substring(0, words[0].length() - 1);
		} else if (words[0].equals("'s")) {
			words[0] = "r"; // xxx's = your
		}

		StringBuilder builder = new StringBuilder();
		for (String word : words) {
			builder.append(" ");
			builder.append(word);
		}

		return builder.toString().trim();
	}
}
