package roguelike.ui.windows;

import java.util.List;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.MessageDisplayProperties;
import roguelike.MessageLog;
import roguelike.ui.InputCommand;
import roguelike.ui.Menu;
import roguelike.ui.MenuItem;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;

public class MessageLogWindow extends Dialog<InputCommand> {

	private Menu<MessageDisplayProperties> messageMenu;

	public MessageLogWindow(int width, int height, MessageLog messages) {
		super(width, height);

		messageMenu = new Menu<MessageDisplayProperties>(messages.getAll(), 29) {

			@Override
			protected StringEx getTextFor(MessageDisplayProperties item, int position) {
				return new StringEx(item.getText(), item.getColor(), SColor.BLACK);
			}
		};
	}

	@Override
	protected DialogResult<InputCommand> onProcess(InputCommand command) {
		DialogResult<InputCommand> result = null;
		if (command != null) {

			switch (command) {
			case CONFIRM:
			case CANCEL:
				return DialogResult.ok(command);

			default:
				messageMenu.processCommand(command);
			}
		}
		return result;
	}

	@Override
	protected void onDraw() {

		terminal.withColor(SColor.MOUSY_INDIGO).fill(0, 0, size.width, size.height, ' ');
		List<MenuItem<MessageDisplayProperties>> currentPage = messageMenu.currentPageItems();
		int y = 0;
		for (MenuItem<MessageDisplayProperties> item : currentPage) {
			terminal.write(1, y, item.getText());

			y++;
		}
	}
}
