package roguelike.ui.animations;

import java.awt.Point;
import java.util.Queue;

import roguelike.actors.Actor;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;
import squidpony.squidmath.Bresenham;

public class RangedAttackAnimation extends Animation {

	private Actor target;
	private Queue<Point> path;

	private int rangedFrames = 8;

	private AttackAnimation damageAnim;

	public RangedAttackAnimation(Actor attacker, Actor target, String damage) {
		this.target = target;

		path = Bresenham.line2D(attacker.getPosition(), target.getPosition());

		damageAnim = new AttackAnimation(attacker, target, damage);
		this.totalFrames = damageAnim.totalFrames + rangedFrames;
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public void onNextFrame(TerminalBase terminal) {
		Point offsetPos = getOffsetPosition(terminal, target);
		int x = offsetPos.x - target.getPosition().x;
		int y = offsetPos.y - target.getPosition().y;

		if (this.currentFrame < rangedFrames) {
			int numTiles = (int) Math.ceil(path.size() / (float) 8.0f);
			for (int i = 0; i < numTiles; i++) {

				Point p = path.poll();
				if (p != null) {
					// terminal.withColor(SColor.TRANSPARENT, SColor.RED).fill(p.x + x, p.y + y, 1, 1);
					terminal.withColor(SColor.LIGHT_GRAY).put(p.x + x, p.y + y, '`');
				}
			}
		}
		else {
			damageAnim.nextFrame(terminal);
		}
	}
}
