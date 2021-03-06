package roguelike.ui.animations;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.util.DirectionIntercardinal;

public class AttackAnimation extends Animation {

	private Actor target;
	private String damage;

	private char attackChar;
	private Coordinate attackCharPoint;

	public AttackAnimation(Actor attacker, Actor target, String damage) {
		this.target = target;
		this.damage = damage;
		this.totalFrames = 15;

		Coordinate attackerPos = attacker.getPosition();
		Coordinate targetPos = target.getPosition();

		DirectionIntercardinal dir = DirectionIntercardinal.getDirection(targetPos.x - attackerPos.x, targetPos.y - attackerPos.y);
		switch (dir) {
		case UP:
		case DOWN:
			attackChar = '|';
			break;
		case LEFT:
		case RIGHT:
			attackChar = '-';
			break;
		case UP_LEFT:
		case DOWN_RIGHT:
			attackChar = '\\';
			break;
		case UP_RIGHT:
		case DOWN_LEFT:
			attackChar = '/';
			break;
		default:
			attackChar = '*';
			break;
		}
		if (Math.floor(attackerPos.distance(targetPos)) <= 1) {
			attackCharPoint = targetPos;
		}
		else {
			attackCharPoint = attackerPos.createOffsetPosition(dir);
		}
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public void onNextFrame(TerminalBase terminal) {
		Point offsetPos = getOffsetPosition(terminal, target);
		Point attackCharPos = getOffsetPosition(terminal, attackCharPoint);
		int x = offsetPos.x;
		int y = offsetPos.y;

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

		TerminalBase effect = terminal.withColor(foregroundColor, backgroundColor);
		TerminalBase dmg = terminal.withColor(foregroundColor);

		effect.fill(offsetPos.x, offsetPos.y, 1, 1);

		// float pctFinished = (currentFrame / (float) totalFrames);
		// if (pctFinished < 0.5) {
		// effect.put(offsetPos.x, offsetPos.y, '\\');
		// }
		// if (pctFinished >= 0.5 && pctFinished < 0.8) {
		// effect.put(offsetPos.x, offsetPos.y, '/');
		// }

		effect.put(attackCharPos.x, attackCharPos.y, attackChar);

		dmg.write(x, y + yOffset, damage.toString());
	}
}
