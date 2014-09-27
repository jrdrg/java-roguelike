package roguelike.ui;

import java.util.ArrayList;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Statistics;
import roguelike.items.Inventory;
import roguelike.items.Item;
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

	public void draw(MapArea map, int x, int y) {

		this.drawBoxShape(terminal);

		terminal.write(2, 0, "Looking at");

		drawInfo(map, x, y);
	}

	public void erase() {
		terminal.fill(0, 0, size.width, size.height, ' ');
	}

	private void drawInfo(MapArea map, int x, int y) {
		SColor menuBgColor = SColorFactory.asSColor(30, 30, 30);

		// Terminal border = terminal.withColor(SColor.WHITE, SColor.GRAPE_MOUSE);
		TerminalBase background = terminal.withColor(menuBgColor, menuBgColor);
		TerminalBase text = terminal.withColor(SColor.WHITE, menuBgColor);

		ArrayList<StringEx> textList = new ArrayList<StringEx>();

		background.fill(1, 1, size.width - 2, size.height - 2, ' ');
		// border.fill(0, 0, size.width, 1, ' ');

		Actor actor = map.getActorAt(x, y);
		if (actor != null) {
			add(textList, "`" + actor.getColor().getName() + "`" + actor.getDescription());
			add(textList, "");
			Weapon equipped = ItemSlot.RIGHT_ARM.getEquippedWeapon(actor);
			add(textList, " `Gray`Weapon: `White`" + equipped.getDescription() + " (" + equipped.defaultDamageType().name() + ")");

			Statistics stats = actor.statistics();
			add(textList, String.format(" `Bronze`MP:`White`%3d `Bronze`RP:`White`%3d `Bronze`Ref:`White`%3d `Bronze`Aim:`White`%3d `Bronze`Spd:`White`%3d",
					stats.baseMeleePool(0), stats.baseRangedPool(0), stats.reflexes(), stats.aiming(), actor.effectiveSpeed(map)));

			add(textList, String.format(" `Bronze`To:`White`%3d `Bronze`Co:`White`%3d `Bronze`Pe:`White`%3d " +
					"`Bronze`Qu:`White`%3d `Bronze`Wi:`White`%3d `Bronze`Pr:`White`%3d",
					stats.toughness.getTotalValue(), stats.conditioning.getTotalValue(), stats.perception.getTotalValue(),
					stats.agility.getTotalValue(), stats.willpower.getTotalValue(), stats.presence.getTotalValue()));

			add(textList, String.format(" `Red`H:`White`%3d", actor.getHealth().getCurrent()));
			add(textList, "");
			add(textList, String.format(" Can see player? `Red`%s", actor.canSee(Game.current().getPlayer(), map)));
		}
		int textY = 2;
		Inventory inventory = map.getItemsAt(x, y);
		add(textList, "");
		add(textList, "On ground:");
		if (inventory != null && inventory.any()) {
			for (Item i : inventory.allItems()) {
				add(textList, " " + i.getName());
			}
		}

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
