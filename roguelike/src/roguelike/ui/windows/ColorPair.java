package roguelike.ui.windows;

import squidpony.squidcolor.SColor;

public class ColorPair {
	private SColor bgColor;
	private SColor fgColor;

	public ColorPair(SColor foreground, SColor background) {
		fgColor = foreground;
		bgColor = background;
	}

	public ColorPair(SColor foreground) {
		fgColor = foreground;
		bgColor = SColor.BLACK;
	}

	public SColor foreground() {
		return fgColor;
	}

	public SColor background() {
		return bgColor;
	}
}
