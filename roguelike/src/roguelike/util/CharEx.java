package roguelike.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class CharEx implements Serializable {
	private static final long serialVersionUID = -506720251888391231L;

	static SColor DEFAULT_FOREGROUND = SColor.WHITE;
	static SColor DEFAULT_BACKGROUND = SColor.BLACK;

	private transient SColor foreground;
	private transient SColor background;
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

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();

		out.writeInt(foreground.getRGB());
		out.writeInt(background.getRGB());
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		foreground = SColorFactory.asSColor(in.readInt());
		background = SColorFactory.asSColor(in.readInt());
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
			return (Character.compare(other.symbol, this.symbol) == 0 && other.background.equals(this.background) && other.foreground.equals(this.foreground));
		}
		return super.equals(obj);
	}

	public boolean isWhitespace() {
		return this.symbol == ' ';
	}
}
