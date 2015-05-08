package roguelike.screens;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Statistics;
import roguelike.functionalInterfaces.CursorCallback;
import roguelike.items.Inventory;
import roguelike.items.Weapon;
import roguelike.items.Equipment.ItemSlot;
import roguelike.maps.MapArea;
import roguelike.ui.LookCursor;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;
import roguelike.util.Coordinate;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class LookScreen extends CursorScreen {

	private class InformationPanel extends TextWindow {
		private final int BOTTOM_MARGIN = 1;
		private final int TOP_MARGIN = 1;

		public InformationPanel(int width, int height) {
			super(width, height);
		}

		public void draw(TerminalBase terminal, MapArea map, int x, int y) {
			Rectangle bounds = new Rectangle(lookPoint.x + 1, lookPoint.y + 1, size.width, size.height);
			if (isWithinTerminalBounds((int) bounds.getMaxX(), 1)) {
				// draw the box to the right

			} else {
				// draw the box to the left
				bounds.x -= (bounds.width + 1);
			}

			if (isWithinTerminalBounds(1, (int) bounds.getMaxY())) {
				// draw box down
			}
			else {
				// draw box up
				bounds.y -= (bounds.height + 1);
			}

			TerminalBase lookTerm = terminal.getWindow(bounds.x, bounds.y, bounds.width, bounds.height);
			this.drawBoxShape(lookTerm);

			ArrayList<StringEx> lines = getTextLines(map, x, y, true);
			drawInfo(lookTerm, lines, 0, bounds.height);

		}

		private boolean isWithinTerminalBounds(int x, int y) {
			Rectangle terminalBounds = terminal.size();
			return terminalBounds.contains(x, y);
		}

		private void drawInfo(TerminalBase terminal, ArrayList<StringEx> textLines, int top, int height) {
			SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

			// Terminal border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
			TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
			TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);
			int textY = TOP_MARGIN + top;

			background.fill(1, 1 + top, size.width - 2, height - 2, ' '); // size.height - 2, ' ');

			for (int i = 0; i < textLines.size(); i++) {
				text.write(2, i + textY, textLines.get(i));
				if ((i + textY) >= (height + top)) {
					text.write(3, i + textY + 2, "...");
					break;
				}
			}
		}

		private ArrayList<StringEx> getTextLines(MapArea map, int x, int y, boolean drawActor) {
			ArrayList<StringEx> textList = new ArrayList<StringEx>();

			Actor actor = drawActor ? map.getActorAt(x, y) : null;
			if (actor != null) {
				add(textList, "`" + actor.color().getName() + "`" + actor.getName() + " =" + actor.behavior().getDescription());
				add(textList, actor.getDescription());
				Weapon equipped = ItemSlot.RIGHT_HAND.getEquippedWeapon(actor);
				add(textList, " `Gray`Weapon");
				add(textList, "`White`" + equipped.name() + " (" + equipped.defaultDamageType().name() + ")");
				add(textList, "");
				Statistics stats = actor.statistics();
				add(textList, String.format("`Bronze`Ref:`White`%3d `Bronze`Aim:`White`%3d `Bronze`Spd:`White`%3d",
						stats.reflexes(), stats.aiming(), actor.effectiveSpeed(map)));

				add(textList, String.format(" `Bronze`To:`White`%3d `Bronze`Co:`White`%3d `Bronze`Pe:`White`%3d ",
						stats.toughness.getTotalValue(), stats.conditioning.getTotalValue(), stats.perception.getTotalValue()));
				add(textList, String.format(" `Bronze`Qu:`White`%3d `Bronze`Wi:`White`%3d `Bronze`Pr:`White`%3d",
						stats.agility.getTotalValue(), stats.willpower.getTotalValue(), stats.presence.getTotalValue()));

				add(textList, String.format(" `Red`H:`White`%3d  `Bronze`MP:`White`%3d `Bronze`RP:`White`%3d ",
						actor.health().getCurrent(), stats.baseMeleePool(0), stats.baseRangedPool(0)));
				// add(textList, "");
				add(textList, String.format(" Can see player? `Red`%s", actor.canSee(Game.current().getPlayer(), map)));
			}
			Inventory inventory = map.getItemsAt(x, y);
			add(textList, "");

			if (drawActor)
				add(textList, "On ground:");

			int itemSize = (this.size.height - (BOTTOM_MARGIN + TOP_MARGIN)) - textList.size();
			// String[] itemDescriptions = inventory.getItemListAsText(itemSize - BOTTOM_MARGIN);
			String[] itemDescriptions = inventory.getGroupedItemListAsText(itemSize - BOTTOM_MARGIN);

			for (String string : itemDescriptions)
				add(textList, " " + string);

			return textList;
		}

		private void add(ArrayList<StringEx> list, String string) {
			StringEx str = new StringEx(string);
			StringEx[] lines = str.wordWrap(size.width - 2);
			for (StringEx line : lines) {
				list.add(line);
			}
		}
	}

	private InformationPanel lookDisplay;
	private Point lookPoint;

	public LookScreen(TerminalBase terminal, LookCursor cursor, CursorCallback resultCallback) {
		super(terminal, cursor, resultCallback);

		lookDisplay = new InformationPanel(40, 20);
		cursor.setLookScreen(this);
	}

	/**
	 * Sets the map coordinates that the LookDisplay should show information about. If this is null, then the
	 * LookDisplay will be hidden.
	 * 
	 * @param map
	 * @param position
	 */
	public void lookAt(MapArea map, Point position) {
		lookPoint = position;
	}

	@Override
	protected void onDrawAdditional(MapArea currentMap, Coordinate centerPosition, Rectangle screenArea) {
		/* draw the look description box if there's anything here */
		drawLookDisplay(currentMap);
	}

	private void drawLookDisplay(MapArea currentMap) {
		if (lookPoint == null)
			return;

		lookDisplay.draw(terminal, currentMap, lookPoint.x, lookPoint.y);
	}
}
