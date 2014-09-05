package roguelike.ui.animations;

import java.awt.Point;
import java.awt.Rectangle;

import roguelike.Game;
import roguelike.actors.Actor;
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
		this.totalFrames = 10;
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public boolean nextFrame(Terminal terminal) {
		currentFrame++;

		SColor backgroundColor = SColorFactory.blend(SColor.RED, SColor.BLACK, currentFrame / (float) totalFrames);
		Terminal effect = terminal.withColor(SColor.TRANSPARENT, backgroundColor);
		Terminal dmg = terminal.withColor(SColor.YELLOW);
		Point offsetPos = getOffsetPosition(terminal);
		int x = offsetPos.x;
		int y = offsetPos.y;
		int width = 1 + ((currentFrame / 4) * 2);
		int height = 1 + ((currentFrame / 4) * 2);

		x = Math.max(0, x - (currentFrame / 4));
		y = Math.max(0, y - (currentFrame / 4));

		effect.fill(x, y, width, height);
		dmg.write(x, y, damage.toString());

		System.out.println("AttackAnimation: frame " + currentFrame + ", x=" + x + ", y=" + y);

		boolean finished = currentFrame >= totalFrames;
		if (finished) {
			// clear animation effect?
		}

		return finished;
	}

	private Point getOffsetPosition(Terminal terminal) {
		Game g = target.getGame();
		Rectangle termSize = terminal.size();
		Coordinate playerPos = g.getPlayer().getPosition();
		Point upperLeft = g.getCurrentMapArea().getUpperLeftScreenTile(termSize.width, termSize.height, playerPos);
		Coordinate targetPos = target.getPosition();

		Point ret = new Point(targetPos.x - upperLeft.x, targetPos.y - upperLeft.y);

		return ret;
	}
}
