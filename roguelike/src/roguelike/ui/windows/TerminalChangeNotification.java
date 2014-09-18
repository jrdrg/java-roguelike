package roguelike.ui.windows;

import roguelike.util.CharEx;

public abstract class TerminalChangeNotification {

	public abstract void onChanged(int x, int y, CharEx c);
}
