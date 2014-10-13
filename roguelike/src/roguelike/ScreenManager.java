package roguelike;

import roguelike.screens.Screen;

public class ScreenManager {

	public abstract interface ScreenFactory {
		public abstract Screen create(Screen self);
	}

	private static ScreenFactory nextScreen;

	public static void setNextScreen(ScreenFactory screenFactory) {
		ScreenManager.nextScreen = screenFactory;
	}

	public static Screen getNextScreen(Screen first) {
		if (nextScreen == null)
			return null;

		Screen next = nextScreen.create(first);
		nextScreen = null;
		return next;
	}
}
