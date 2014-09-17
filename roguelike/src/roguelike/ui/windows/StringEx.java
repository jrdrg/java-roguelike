package roguelike.ui.windows;

import java.util.ArrayList;
import java.util.List;

import squidpony.squidcolor.SColor;

/**
 * String composed of Characters to provide color info
 * 
 * @author john
 * 
 */
public class StringEx extends ArrayList<CharEx> {

	private static final long serialVersionUID = -276852334648421745L;

	public StringEx() {
	}

	public StringEx(List<CharEx> characters) {
		super(characters);
	}

	public StringEx(String text) {
		for (int x = 0; x < text.length(); x++) {
			this.add(new CharEx(text.charAt(x)));
		}
	}

	public StringEx(String text, SColor foreground, SColor background) {
		for (int x = 0; x < text.length(); x++) {
			this.add(new CharEx(text.charAt(x), foreground, background));
		}
	}
}
