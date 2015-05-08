package roguelike.screens;

import roguelike.Dialog;
import roguelike.functionalInterfaces.DialogCallback;
import roguelike.ui.windows.TerminalBase;

public class DialogScreen<T> extends Screen {

	private Dialog<T> dialog;
	private DialogCallback<T> resultCallback;

	public DialogScreen(TerminalBase terminal, Dialog<T> dialog, DialogCallback<T> resultCallback) {
		super(terminal);

		this.dialog = dialog;
		this.resultCallback = resultCallback;

		this.dialog.showInPane(terminal);
		this.dialog.show();
	}

	@Override
	public void onDraw() {
		dialog.draw();
	}

	@Override
	public void process() {
		if (dialog.process()) {
			resultCallback.setResult(dialog.result());
			restorePreviousScreen();
		}
	}

}
