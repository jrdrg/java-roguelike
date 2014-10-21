package roguelike.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

		char[] strChars = new char[characters.size()];
		for (int x = 0; x < characters.size(); x++)
			strChars[x] = characters.get(x).symbol();

		this.text = new String(strChars);
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

	@Override
	public String toString() {
		return text;
	}

	public StringEx[] wordWrap(int lineWidth) {
		List<StringEx> lines = new ArrayList<StringEx>();
		StringEx line;

		int lastWrapPoint = 0;
		int prevLastWrapPoint = 0;
		int thisLineStart = 0;
		for (int i = 0; i < size(); i++)
		{
			CharEx c = this.get(i);

			if (c.isWhitespace())
			{
				if (lastWrapPoint > 0)
					prevLastWrapPoint = lastWrapPoint;
				lastWrapPoint = i;
			}

			// wrap if we got too long
			if (i - thisLineStart >= lineWidth)
			{
				if (lastWrapPoint != 0)
				{
					// have a recent point to wrap at, so word wrap
					if (lastWrapPoint - thisLineStart > lineWidth && lastWrapPoint > 0) {

						line = substring(thisLineStart, prevLastWrapPoint - thisLineStart);
						thisLineStart = prevLastWrapPoint;
					} else {

						line = substring(thisLineStart, lastWrapPoint - thisLineStart);
						thisLineStart = lastWrapPoint;
					}

					lastWrapPoint = 0;
				}
				else
				{
					// no convenient point to word wrap, so character wrap
					line = substring(thisLineStart, i - thisLineStart);
					thisLineStart = i;
				}

				line = line.trim();
				lines.add(line);
			}
		}

		// add the last bit
		line = substring(thisLineStart);
		line = line.trim();
		lines.add(line);

		return lines.toArray(new StringEx[0]);
	}

	public StringEx substring(int startIndex, int length)
	{
		StringEx substring = new StringEx();
		// substring.AddRange(this.Where((c, index) => (index >= startIndex) && (index < startIndex + length)));
		substring.addAll(this.stream().skip(startIndex).limit(startIndex + length).collect(Collectors.toList()));

		return substring;
	}

	public StringEx substring(int startIndex)
	{
		return substring(startIndex, size() - startIndex);
	}

	public StringEx trim()
	{
		StringEx trimmed = new StringEx(this);

		// trim from the front
		while ((trimmed.size() > 0) && (trimmed.get(0).isWhitespace()))
		{
			trimmed.remove(0);
		}

		// trim from the end
		while ((trimmed.size() > 0) && (trimmed.get(trimmed.size() - 1).isWhitespace()))
		{
			trimmed.remove(trimmed.size() - 1);
		}

		return trimmed;
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
}
