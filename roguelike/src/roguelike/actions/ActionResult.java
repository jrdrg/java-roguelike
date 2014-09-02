package roguelike.actions;

public class ActionResult {
	final boolean success;
	final boolean completed;
	final Action alternateAction;
	String message;

	private ActionResult(boolean success) {
		this(success, true, null);
	}

	private ActionResult(boolean success, boolean completed, Action alternateAction) {
		this.success = success;
		this.completed = completed;
		this.alternateAction = alternateAction;
	}

	public static ActionResult success() {
		return new ActionResult(true);
	}

	public static ActionResult failure() {
		return new ActionResult(false);
	}

	public static ActionResult alternate(Action action) {
		return new ActionResult(false, true, action);
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public Action getAlternateAction() {
		return alternateAction;
	}

	public String getMessage() {
		return message;
	}

	public ActionResult setMessage(String message) {
		this.message = message;
		return this;
	}

}
