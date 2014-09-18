package roguelike.util;

import java.util.ArrayList;
import java.util.List;

import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

/**
 * String composed of Characters to provide color info
 * 
 * @author john
 * 
 */
public class StringEx extends ArrayList<CharEx> {
	private class CharacterParseResult {
		public List<Character> parsedColor;
		public boolean readColor;
		public boolean isTextChar;
	}

	private static final long serialVersionUID = -276852334648421745L;

	private String text;

	public StringEx() {
	}

	public StringEx(List<CharEx> characters) {
		super(characters);
	}

	public StringEx(String text) {
		this(text, CharEx.DEFAULT_FOREGROUND, CharEx.DEFAULT_BACKGROUND);
	}

	public StringEx(String text, SColor foreground, SColor background) {
		this.text = text;
		SColor fg = foreground;
		SColor bg = background;
		CharacterParseResult res = new CharacterParseResult();
		for (int x = 0; x < text.length(); x++) {
			char c = text.charAt(x);
			res = parseColor(c, res.parsedColor);
			if (res.readColor) {
				fg = toColor(res.parsedColor);
				res.parsedColor = null;
			} else if (res.isTextChar) {
				this.add(new CharEx(c, fg, bg));
			}
		}
	}

	private CharacterParseResult parseColor(Character c, List<Character> read) {
		CharacterParseResult res = new CharacterParseResult();
		if (c == '`') {
			if (read == null) {
				res.parsedColor = new ArrayList<Character>();
			} else {
				// we're done reading the string
				res.parsedColor = read;
				res.readColor = true;
			}
		} else if (read != null) {
			res.parsedColor = read;
			res.parsedColor.add(c);
		} else {
			res.isTextChar = true;
		}
		return res;
	}

	private SColor toColor(List<Character> color) {
		char[] chars = new char[color.size()];
		for (int x = 0; x < chars.length; x++)
			chars[x] = color.get(x);

		String s = new String(chars);

		return SColorFactory.colorForName(s);
	}

	@Override
	public String toString() {
		return text;
	}
}
