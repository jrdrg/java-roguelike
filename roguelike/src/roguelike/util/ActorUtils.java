package roguelike.util;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.maps.MapArea;
import squidpony.squidgrid.los.BresenhamLOS;
import squidpony.squidgrid.los.LOSSolver;
import squidpony.squidgrid.util.BasicRadiusStrategy;

public class ActorUtils {
	private static LOSSolver losSolver = new BresenhamLOS();

	private ActorUtils() {
	}

	public static boolean canSee(Actor actor, Actor other, MapArea mapArea) {
		Point position = actor.getPosition();
		int startx = position.x, starty = position.y, targetx = other.getPosition().x, targety = other.getPosition().y;
		float force = 1;
		float decay = 1 / actor.getVisionRadius();
		boolean visible = losSolver.isReachable(mapArea.getLightValues(), startx, starty, targetx, targety, force, decay, BasicRadiusStrategy.CIRCLE);

		Log.verboseDebug(actor.getName() + " canSee " + other.getName() + "=" + visible);

		return visible;
	}

	public static String makePlayerText(String text) {
		String[] words = text.split(" ");

		if (words[0].equals("has")) {
			words[0] = "have";

		} else if (words[0].endsWith("Es")) {
			words[0] = words[0].substring(0, words[0].length() - 1);

		} else if (words[0].endsWith("es")) {
			words[0] = words[0].substring(0, words[0].length() - 2);

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
