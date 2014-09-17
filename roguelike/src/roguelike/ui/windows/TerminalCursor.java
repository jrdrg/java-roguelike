package roguelike.ui.windows;

abstract class TerminalCursor {
	public abstract boolean put(int x, int y, CharEx c);

	public abstract boolean bg(int x, int y);
}
