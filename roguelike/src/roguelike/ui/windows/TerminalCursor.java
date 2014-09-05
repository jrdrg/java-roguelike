package roguelike.ui.windows;

abstract class TerminalCursor {
	public abstract TerminalCursor put(int x, int y, char c);

	public abstract TerminalCursor put(int x, int y, char[][] c);
}
