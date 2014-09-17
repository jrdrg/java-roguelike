package roguelike.ui.windows;

import squidpony.squidcolor.SColor;

public class CharEx {
	static SColor DEFAULT_FOREGROUND = SColor.WHITE;
	static SColor DEFAULT_BACKGROUND = SColor.BLACK;

	private SColor foreground;
	private SColor background;
	private char symbol;

	public CharEx(char symbol) {
		this(symbol, DEFAULT_FOREGROUND, DEFAULT_BACKGROUND);
	}

	public CharEx(char symbol, SColor foreground) {
		this(symbol, foreground, DEFAULT_BACKGROUND);
	}

	public CharEx(char symbol, SColor foreground, SColor background) {
		this.symbol = symbol;
		this.foreground = foreground;
		this.background = background;
	}

	public static CharEx parse(String text) {
		String[] elements = text.split(":");

		if (elements == null || elements.length == 0)
			throw new IllegalArgumentException("Invalid text passed to Character.parse");

		return new CharEx(elements[0].charAt(0));
	}

	public char symbol() {
		return this.symbol;
	}

	public SColor foregroundColor() {
		return this.foreground;
	}

	public SColor backgroundColor() {
		return this.background;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharEx) {
			CharEx other = (CharEx) obj;
			return (other.symbol == this.symbol && other.background.equals(this.background) && other.foreground.equals(this.foreground));
		}
		return super.equals(obj);
	}
}
