package roguelike.ui;

import java.awt.Rectangle;

import roguelike.Cursor;
import roguelike.CursorResult;
import roguelike.Game;
import roguelike.actors.Actor;
import roguelike.actors.Player;
import roguelike.maps.MapArea;
import roguelike.maps.Tile;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
import roguelike.util.CurrentItemTracker;
import roguelike.util.Log;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;
import squidpony.squidgrid.fov.FOVSolver;
import squidpony.squidgrid.fov.ShadowFOV;
import squidpony.squidgrid.util.BasicRadiusStrategy;
import squidpony.squidgrid.util.RadiusStrategy;

public class AttackCursor extends Cursor {

	private SColor background;
	private FOVSolver fov = new ShadowFOV();
	private Coordinate initialPosition;

	boolean fovDrawn = false;
	boolean determinedActors = false;
	Rectangle screenArea;
	float[][] incomingLight;

	private CurrentItemTracker<Actor> targets;
	private Actor startTarget = null;

	public AttackCursor(Coordinate initialPosition, MapArea mapArea, int maxRadius, RadiusStrategy radiusStrategy) {
		super(initialPosition, mapArea, maxRadius);
		this.radiusStrategy = BasicRadiusStrategy.CIRCLE;
		this.background = SColor.DARK_CORAL;
		this.initialPosition = initialPosition;

		this.radiusStrategy = radiusStrategy;
		this.targets = new CurrentItemTracker<Actor>();
	}

	@Override
	protected void onDraw(TerminalBase terminal, int sx, int sy) {
		// TODO: maybe some effects here, draw a line or something

		if (!fovDrawn) {
			determineFOVTiles(terminal);
			fovDrawn = true;
		}
		drawFOV(terminal);
		super.onDraw(terminal, sx, sy);
		DisplayManager.instance().setDirty(); // update
	}

	private void determineFOVTiles(TerminalBase terminal) {
		int width = maxRadius;
		int height = maxRadius;
		MapArea currentMap = Game.current().getCurrentMapArea();
		screenArea = currentMap.getVisibleAreaInTiles(terminal, initialPosition);

		float[][] lighting = new float[width][height];

		lighting = ArrayUtils.getSubArray(currentMap.getLightValues(), screenArea);

		incomingLight = fov.calculateFOV(
				lighting,
				initialPosition.x - screenArea.x,
				initialPosition.y - screenArea.y,
				maxRadius + 1);

	}

	private void drawFOV(TerminalBase terminal) {
		MapArea currentMap = Game.current().getCurrentMapArea();

		for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
			for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
				int cX = x - screenArea.x;
				int cY = y - screenArea.y;

				Tile t = currentMap.getTileAt(x, y);
				if (incomingLight[cX][cY] > 0 && t.isVisible()) {

					if (!t.isWall())
						terminal.withColor(SColor.TRANSPARENT, SColorFactory.dimmest(background)).fill(cX, cY, 1, 1);

					if (!determinedActors) {
						Actor a = t.getActor();
						if (a != null && !(a instanceof Player)) {
							targets.add(a);
							if (startTarget == null) {
								startTarget = a;
								position.setLocation(a.getPosition());
								Log.debug("set attack cursor position=" + position.x + "," + position.y);
							}
						}
					}
				}
			}
		}
		determinedActors = true;
	}

	@Override
	protected boolean onUpdatePosition(Coordinate position) {
		int x = position.x - screenArea.x;
		int y = position.y - screenArea.y;

		if (incomingLight[x][y] > 0)
			return true;

		return false;
	}

	@Override
	protected CursorResult onProcessCommand(InputCommand command) {
		switch (command) {
		case PREVIOUS_TARGET:
			// move to previous target
			targets.previous();
			break;

		case NEXT_TARGET:
			// move to next target
			targets.advance();
			break;

		default:
			return null;
		}

		Actor tgt = targets.getCurrent();
		if (tgt != null)
			position.setLocation(tgt.getPosition());

		return null;
	}
}
