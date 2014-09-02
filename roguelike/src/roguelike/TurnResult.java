package roguelike;

import java.util.ArrayList;
import java.util.List;

import squidpony.squidcolor.SColor;

public class TurnResult {
	boolean running;
	ArrayList<MessageDisplayProperties> messages;
	ArrayList<TurnEvent> events;

	public TurnResult(boolean running) {
		this.running = running;
		this.messages = new ArrayList<MessageDisplayProperties>();
		this.events = new ArrayList<TurnEvent>();
	}

	public TurnResult addMessage(String message) {
		if (message != null) {
			messages.add(new MessageDisplayProperties(message));
		}
		return this;
	}

	public TurnResult addMessage(String message, SColor color) {
		if (message != null) {
			messages.add(new MessageDisplayProperties(message, color));
		}
		return this;
	}

	public TurnResult addEvent(TurnEvent event) {
		if (event != null) {
			events.add(event);
		}
		return this;
	}

	public boolean isRunning() {
		return this.running;
	}

	public List<MessageDisplayProperties> getMessages() {
		return messages;
	}

	public List<TurnEvent> getEvents() {
		return events;
	}
}
