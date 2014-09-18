package roguelike.ui;

import java.util.ArrayList;

import roguelike.MessageDisplayProperties;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;

public class MessageDisplay {
	private TerminalBase terminal;
	private ArrayList<MessageDisplayProperties> messages;
	private int numLines;

	public MessageDisplay(TerminalBase terminal, int numLines) {
		this.terminal = terminal;
		this.numLines = numLines;
		this.messages = new ArrayList<MessageDisplayProperties>();
	}

	public void clear() {
		messages.clear();
	}

	public void display(String message) {
		display(new MessageDisplayProperties(message));
	}

	public void display(String message, SColor color) {
		display(new MessageDisplayProperties(message, color));
	}

	public void display(MessageDisplayProperties message) {
		messages.add(message);

		if (messages.size() > numLines)
			messages.remove(0);
	}

	public void draw() {
		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');
		for (int x = 0; x < messages.size(); x++) {
			MessageDisplayProperties props = messages.get(x);
			TerminalBase colorTerm = terminal.withColor(props.getColor());
			colorTerm.write(0, x, props.getText());
		}
	}

}
