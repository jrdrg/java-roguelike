package roguelike.util;

public class Utility {
	public static String capitalizeFirstLetter(String text) {
		return String.format("%s%s", text.substring(0, 1).toUpperCase(), text.substring(1).toLowerCase());
	}
}
