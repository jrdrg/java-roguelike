package roguelike.util;

public class Log {
	public static void verboseDebug(String message) {
		// System.out.println("VERBOSE: " + message);
	}

	public static void debug(String message) {
		System.out.println("DEBUG: " + message);
	}

	public static void warning(String message) {
		System.out.println("WARNING: " + message);
	}

	public static void info(String message) {
		System.out.println("INFO: " + message);
	}
}
