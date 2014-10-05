package roguelike;

import roguelike.util.StringEx;
import roguelike.util.Utility;
import squidpony.squidcolor.SColor;

public class MessageDisplayProperties {

	private StringEx text;
	private SColor color;

	public MessageDisplayProperties(String text) {
		this(text, SColor.LIGHT_GRAY);
	}

	public MessageDisplayProperties(String text, SColor color) {
		if (text == null)
			throw new IllegalArgumentException("text cannot be null");
		this.text = new StringEx(Utility.capitalizeFirstLetter(text), color, SColor.BLACK);
		this.color = color;
	}

	public StringEx getText() {
		return text;
	}

	public SColor getColor() {
		return color;
	}
}
