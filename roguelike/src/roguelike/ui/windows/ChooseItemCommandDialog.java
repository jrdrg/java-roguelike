package roguelike.ui.windows;

import java.awt.Point;
import java.util.ArrayList;

import roguelike.Dialog;
import roguelike.DialogResult;
import roguelike.ui.InputCommand;
import roguelike.ui.Menu;
import roguelike.ui.MenuItem;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class ChooseItemCommandDialog extends Dialog<InputCommand> {

	Menu<InputCommand> commands;

	public ChooseItemCommandDialog() {
		super(20, 7, false);

		ArrayList<InputCommand> commandList = new ArrayList<InputCommand>();
		commandList.add(InputCommand.EQUIP);
		commandList.add(InputCommand.USE);
		commandList.add(InputCommand.DROP);

		commands = new Menu<InputCommand>(commandList) {

			@Override
			protected StringEx getTextFor(InputCommand item, int position) {
				String capitalizedFirstLetter = item.toString();
				String text = String.format("%s)%s",
						capitalizedFirstLetter.substring(0, 1).toLowerCase(), capitalizedFirstLetter.substring(1).toLowerCase());
				return new StringEx(text);
			}

			@Override
			protected int getIndexOfChar(char keyChar) {
				switch (keyChar) {
				case 'e':
					return 0;
				case 'u':
					return 1;
				case 'd':
					return 2;
				}
				return super.getIndexOfChar(keyChar);
			}
		};
	}

	@Override
	public Point getLocation() {
		Point p = super.getLocation();
		p.translate(2, 2);
		return p;
	}

	@Override
	protected DialogResult<InputCommand> onProcess(InputCommand command) {
		DialogResult<InputCommand> result = null;
		if (command != null) {
			switch (command) {
			case CONFIRM:

				InputCommand activeItem = commands.getActiveItem();
				if (activeItem != null) {
					result = DialogResult.ok(activeItem);

				} else {
					result = DialogResult.ok(null);

				}

			case CANCEL:
				if (result == null)
					result = DialogResult.cancel();

				break;

			default:
				commands.processCommand(command);
			}
		}

		return result;
	}

	@Override
	protected void onDraw() {
		SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

		// TerminalBase border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		TerminalBase border = terminal.withColor(SColor.WHITE, SColor.BLACK);
		TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
		TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);

		background.fill(0, 0, size.width, size.height, ' ');
		border.fill(0, 0, size.width, 1, ' ');

		drawBoxShape(border);

		int currentPage = commands.getCurrentPage();
		int pageCount = commands.getPageCount();

		border.write(1, 0, String.format("Action?", currentPage, pageCount));

		int displayY = 2;
		for (MenuItem<InputCommand> item : commands.currentPageItems()) {
			String color = "";
			if (item.isActive()) {
				color = "`Alizarin`";
			}
			text.write(2, displayY, color + item.getText());
			displayY++;
		}
	}

}
