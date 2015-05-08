package roguelike.ui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Statistics;
import roguelike.items.Inventory;
import roguelike.items.Weapon;
import roguelike.items.Equipment.ItemSlot;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.ui.windows.TextWindow;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class LookDisplay extends TextWindow {
	private final int BOTTOM_MARGIN = 1;
	private final int TOP_MARGIN = 1;
	private TerminalBase terminal;
	private ArrayList<StringEx> textLines;

	public LookDisplay(TerminalBase terminal, int width, int height) {
		super(width, height);
		// this.terminal = terminal;
		setTerminal(terminal);
	}

	public LookDisplay setTerminal(TerminalBase terminal)
	{
		this.size = new Rectangle(0, 0, terminal.size().width, terminal.size().height);
		this.terminal = terminal;
		return this;
	}

	public void draw(MapArea map, int x, int y, Point screenLookPoint) {
		int height = getHeight(map, x, y, true, "Looking at");
		draw(height);
	}

	public int getHeight(MapArea map, int x, int y, boolean drawActor, String caption) {
		textLines = getTextLines(map, x, y, drawActor);
		int height = Math.min(textLines.size(), this.size.height - 4);
		height += BOTTOM_MARGIN + TOP_MARGIN;

		return height;

	}

	public void draw(int height) {
		int top = terminal.size().height - height - 1;
		if (terminal.size().y > Game.current().getPlayer().position.y)
			top = 0;

		this.drawBoxShape(terminal, top, height + 1, true);
		// terminal.write(2, 0, caption);
		drawInfo(textLines, top, height);
	}

	public void erase() {
		terminal.fill(0, 0, size.width, size.height, ' ');
	}

	private ArrayList<StringEx> getTextLines(MapArea map, int x, int y, boolean drawActor) {
		ArrayList<StringEx> textList = new ArrayList<StringEx>();

		// border.fill(0, 0, size.width, 1, ' ');

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

	private void drawInfo(ArrayList<StringEx> textLines, int top, int height) {
		SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

		// Terminal border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
		TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);
		int textY = TOP_MARGIN + top;

		background.fill(1, 1 + top, size.width - 2, height - 1, ' '); // size.height - 2, ' ');

		for (int i = 0; i < textLines.size(); i++) {
			text.write(2, i + textY, textLines.get(i));
			if ((i + textY) >= (height + top)) { // this.size.height - 4) {
				text.write(3, i + textY + 2, "...");
				break;
			}
		}
	}

	private void add(ArrayList<StringEx> list, String string) {
		StringEx str = new StringEx(string);
		StringEx[] lines = str.wordWrap(size.width - 2);
		for (StringEx line : lines) {
			list.add(line);
		}
	}
}
