package roguelike;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import roguelike.screens.Screen;
import squidpony.squidutility.Pair;

public class TurnResult {

	boolean running;
	boolean playerActed;
	ArrayList<MessageDisplayProperties> messages;
	ArrayList<TurnEvent> events;
	Pair<Point, Boolean> currentLook = new Pair<Point, Boolean>(null, false);
	Screen subScreen;

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
		result.messages.clear();
		result.events.clear();
		result.currentLook.setFirst(null);
		result.currentLook.setSecond(true);
		result.subScreen = null;

		return result;
	}

	public boolean playerActedThisTurn() {
		return this.playerActed;
	}

	public void playerActed() {
		this.playerActed = true;
	}

	public Screen getNextScreen() {
		return subScreen;
	}

	public TurnResult setNextScreen(Screen screen) {
		this.subScreen = screen;
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
