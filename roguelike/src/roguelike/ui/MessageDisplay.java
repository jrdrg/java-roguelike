package roguelike.ui;

import roguelike.MessageDisplayProperties;
import roguelike.MessageLog;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class MessageDisplay {
	private TerminalBase terminal;
	private int numLines;
	private MessageLog messages;

	public MessageDisplay(MessageLog messages, TerminalBase terminal, int numLines) {
		this.terminal = terminal;
		this.numLines = numLines;
		this.messages = messages;
	}

	public void display(String message) {
		display(new MessageDisplayProperties(message));
	}

	public void display(String message, SColor color) {
		display(new MessageDisplayProperties(message, color));
	}

	public void display(MessageDisplayProperties message) {

		messages.add(message);
	}

	public void draw() {
		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');
		for (int x = 0; x < messages.size(numLines); x++) {
			MessageDisplayProperties props = messages.get(x);
			TerminalBase colorTerm = terminal.withColor(SColorFactory.blend(props.getColor(), SColor.BLACK_CHESTNUT_OAK, (x / (float) numLines)));
			colorTerm.write(0, x, props.getText().toString());
		}
	}
}
