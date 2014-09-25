package roguelike.ui.animations;

import roguelike.actors.Actor;
import roguelike.ui.windows.TerminalBase;
import squidpony.squidgrid.los.BresenhamLOS;
import squidpony.squidgrid.los.LOSSolver;

public class RangedAttackAnimation extends Animation {

	private Actor target;
	private String damage;
	private LOSSolver los = new BresenhamLOS();

	public RangedAttackAnimation(Actor initiator, Actor target, String damage) {
		this.target = target;
		this.damage = damage;
		this.totalFrames = 15;

		int startx = initiator.getPosition().x;
		int starty = initiator.getPosition().y;
		int targetx = target.getPosition().x;
		int targety = target.getPosition().y;

		// los.isReachable(resistanceMap, startx, starty, targetx, targety)

	}

	@Override
	public boolean isBlocking() {
		return true;
	}

	@Override
	public void onNextFrame(TerminalBase terminal) {

	}

}
