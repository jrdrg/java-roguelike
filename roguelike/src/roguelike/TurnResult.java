package roguelike;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import squidpony.squidutility.Pair;

public class TurnResult {
	boolean running;
	boolean playerActed;
	ArrayList<MessageDisplayProperties> messages;
	ArrayList<TurnEvent> events;
	boolean needsInput;
	Pair<Point, Boolean> currentLook = new Pair<Point, Boolean>(null, false);

	private TurnResult(boolean running) {
		this.running = running;
		this.messages = new ArrayList<MessageDisplayProperties>();
		this.events = new ArrayList<TurnEvent>();
	}

	public static TurnResult reset(TurnResult result, boolean running) {
		if (result == null)
			result = new TurnResult(running);

		result.running = running;
		result.playerActed = false;
		result.needsInput = true;
		result.messages.clear();
		result.events.clear();
		result.currentLook.setFirst(null);
		result.currentLook.setSecond(true);

		return result;
	}

	public boolean playerActedThisTurn() {
		return this.playerActed;
	}

	public void playerActed() {
		this.playerActed = true;
	}

	public TurnResult setNeedsInput(boolean needsInput) {
		this.needsInput = needsInput;
		return this;
	}

	public TurnResult addEvent(TurnEvent event) {
		if (event != null) {
			events.add(event);
		}
		return this;
	}

	public Dialog<?> getActiveWindow() {
		return Game.current().activeWindow;
	}

	public Cursor getCursor() {
		return Game.current().activeCursor;
	}

	public boolean isRunning() {
		return this.running;
	}

	public List<TurnEvent> getEvents() {
		return events;
	}

	public Pair<Point, Boolean> getCurrentLook() {
		return currentLook;
	}

	public void setCurrentLook(Point point, boolean drawActor) {
		this.currentLook.setFirst(point);
		this.currentLook.setSecond(drawActor);
	}
}
