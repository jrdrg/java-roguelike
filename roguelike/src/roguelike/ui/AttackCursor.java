package roguelike.ui;

import java.awt.Rectangle;

import roguelike.Cursor;
import roguelike.Game;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.ArrayUtils;
import roguelike.util.Coordinate;
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
	Rectangle screenArea;
	float[][] incomingLight;

	public AttackCursor(Coordinate initialPosition, MapArea mapArea, int maxRadius, RadiusStrategy radiusStrategy) {
		super(initialPosition, mapArea, maxRadius);
		this.radiusStrategy = BasicRadiusStrategy.CIRCLE;
		this.background = SColor.DARK_CORAL;
		this.initialPosition = initialPosition;

		this.radiusStrategy = radiusStrategy;
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

				if (incomingLight[cX][cY] > 0 && currentMap.getTileAt(x, y).isVisible()) {
					terminal.withColor(SColor.TRANSPARENT, SColorFactory.dimmest(background)).fill(cX, cY, 1, 1);
				}

			}
		}
	}

	@Override
	protected boolean onUpdatePosition(Coordinate position) {
		int x = position.x - screenArea.x;
		int y = position.y - screenArea.y;

		if (incomingLight[x][y] > 0)
			return true;

		return false;
	}
}
