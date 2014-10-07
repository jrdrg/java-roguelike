package roguelike.ui;

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

	private TerminalBase terminal;

	public LookDisplay(TerminalBase terminal, int width, int height) {
		super(width, height);
		this.terminal = terminal;
	}

	public LookDisplay setTerminal(TerminalBase terminal)
	{
		this.size = new Rectangle(0, 0, terminal.size().width, terminal.size().height);
		this.terminal = terminal;
		return this;
	}

	public void draw(MapArea map, int x, int y) {
		draw(map, x, y, true, "Looking at");
	}

	public void draw(MapArea map, int x, int y, boolean drawActor, String caption) {

		this.drawBoxShape(terminal);
		terminal.write(2, 0, caption);
		drawInfo(map, x, y, drawActor);
	}

	public void erase() {
		terminal.fill(0, 0, size.width, size.height, ' ');
	}

	private void drawInfo(MapArea map, int x, int y, boolean drawActor) {
		SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

		// Terminal border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
		TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);

		ArrayList<StringEx> textList = new ArrayList<StringEx>();

		background.fill(1, 1, size.width - 2, size.height - 2, ' ');
		// border.fill(0, 0, size.width, 1, ' ');

		Actor actor = drawActor ? map.getActorAt(x, y) : null;
		if (actor != null) {
			add(textList, "`" + actor.color().getName() + "`" + actor.getDescription());
			add(textList, "");
			Weapon equipped = ItemSlot.RIGHT_ARM.getEquippedWeapon(actor);
			add(textList, " `Gray`Weapon");
			add(textList, "`White`" + equipped.getName() + " (" + equipped.defaultDamageType().name() + ")");
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
		int textY = 2;
		Inventory inventory = map.getItemsAt(x, y);
		add(textList, "");

		if (drawActor)
			add(textList, "On ground:");

		int itemSize = (this.size.height - 4) - textList.size();
		String[] itemDescriptions = inventory.getItemListAsText(itemSize - 2);

		for (String string : itemDescriptions)
			add(textList, " " + string);

		for (int i = 0; i < textList.size(); i++) {
			text.write(2, i + textY, textList.get(i));
			if ((i + textY) >= this.size.height - 4) {
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
