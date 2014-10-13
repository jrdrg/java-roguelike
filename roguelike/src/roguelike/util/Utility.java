package roguelike.util;

public class Utility {
	public static String capitalizeFirstLetter(String text) {
		text = text.replace("!", "!_");
		text = text.replace(".", "._");
		// return String.format("%s%s", text.substring(0, 1).toUpperCase(), text.substring(1).toLowerCase());
		String[] sentences = text.split("[_]");
		StringBuilder sb = new StringBuilder();
		for (String s : sentences) {
			s = s.trim();
			if (s.length() > 1) {
				sb.append(String.format("%s%s", s.substring(0, 1).toUpperCase(), s.substring(1).toLowerCase()));
				if (!s.endsWith(".") && !s.endsWith("!"))
					sb.append(".");

				sb.append(" ");
			} else {
				sb.append(s);
			}
		}
		return sb.toString();
	}
}
