package roguelike.ui.animations;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.ui.windows.Terminal;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class AttackAnimation extends Animation {

	private Actor target;
	private String damage;

	public AttackAnimation(Actor target, String damage) {
		this.target = target;
		this.damage = damage;
		this.totalFrames = 15;
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public void onNextFrame(Terminal terminal) {
		Point offsetPos = getOffsetPosition(terminal);
		int x = offsetPos.x;
		int y = offsetPos.y;
		int width = 1 + ((currentFrame / 4) * 2);
		int height = 1 + ((currentFrame / 4) * 2);

		x = Math.max(0, x - (currentFrame / 4));
		y = Math.max(0, y - (currentFrame / 4));

		SColor backgroundColor = SColorFactory.blend(SColor.RED, SColor.BLACK, currentFrame / (float) totalFrames);
		SColor foregroundColor;
		int yOffset = 0;
		if (Player.isPlayer(target)) {
			foregroundColor = SColor.RED;
			yOffset = (currentFrame / 4) * 2;
		} else {
			foregroundColor = SColor.YELLOW;
		}
		foregroundColor = SColorFactory.blend(foregroundColor, SColor.BENI_DYE, currentFrame / (float) totalFrames);

		Terminal effect = terminal.withColor(SColor.TRANSPARENT, backgroundColor);
		Terminal dmg = terminal.withColor(foregroundColor);

		effect.fill(x, y, width, height);
		dmg.write(x, y + yOffset, damage.toString());

		System.out.println("AttackAnimation: frame " + currentFrame + ", x=" + x + ", y=" + y);
	}

	private Point getOffsetPosition(Terminal terminal) {
		Game g = Game.current();
		Rectangle termSize = terminal.size();
		Coordinate playerPos = g.getPlayer().getPosition();
		Point upperLeft = g.getCurrentMapArea().getUpperLeftScreenTile(termSize.width, termSize.height, playerPos);
		Coordinate targetPos = target.getPosition();

		Point ret = new Point(targetPos.x - upperLeft.x, targetPos.y - upperLeft.y);

		return ret;
	}
}
