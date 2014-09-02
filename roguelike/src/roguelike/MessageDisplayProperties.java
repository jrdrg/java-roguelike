package roguelike;

import squidpony.squidcolor.SColor;

public class MessageDisplayProperties {

	private String text;
	private SColor color;

	public MessageDisplayProperties(String text) {
		this(text, SColor.LIGHT_GRAY);
	}

	public MessageDisplayProperties(String text, SColor color) {
		this.text = text;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public SColor getColor() {
		return color;
	}
}
