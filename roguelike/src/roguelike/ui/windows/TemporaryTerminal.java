package roguelike.ui.windows;

import squidpony.squidcolor.SColor;

public class TemporaryTerminal extends Terminal {

	protected TemporaryTerminal(TerminalChangeNotification terminalChanged) {
		super(terminalChanged);
		
		//TODO: add ability to erase previous steps
		/*
		 * create an arraylist of CharEx to hold previous values
		 * when put() is called, store the previous values in the arraylist
		 * override fill() to loop through the arraylist and replace the current values with the stored ones
		 * once that is done, clear the arraylist
		 * this can be used for animations - at the beginning of the frame, call fill() and then draw
		 * 
		 */
	}

	@Override
	public Terminal getWindow(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Terminal withColor(SColor color) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Terminal withColor(SColor foreground, SColor background) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Terminal fill(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

}
