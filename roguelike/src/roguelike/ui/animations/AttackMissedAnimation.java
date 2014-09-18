package roguelike.ui.animations;

import java.awt.Point;

import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class AttackMissedAnimation extends Animation {

	private Actor target;
	private String description = "Missed!";

	public AttackMissedAnimation(Actor target) {
		this.target = target;
		this.totalFrames = 15;
	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public void onNextFrame(TerminalBase terminal) {
		Point offsetPos = getOffsetPosition(terminal, target);
		int x = offsetPos.x;
		int y = offsetPos.y;

		y = Math.max(0, y - (currentFrame / 4));

		SColor foregroundColor;
		int yOffset = 0;
		if (Player.isPlayer(target)) {
			foregroundColor = SColor.GREEN;
			yOffset = (currentFrame / 4) * 2;
		} else {
			foregroundColor = SColor.APRICOT;
		}
		foregroundColor = SColorFactory.blend(foregroundColor, SColor.DARK_BROWN, currentFrame / (float) totalFrames);

		TerminalBase dmg = terminal.withColor(foregroundColor);

		dmg.write(x, y + yOffset, description);
	}

}
