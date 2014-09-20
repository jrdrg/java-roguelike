package roguelike.ui;

import roguelike.Cursor;
import roguelike.maps.MapArea;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.Coordinate;
import squidpony.squidcolor.SColor;
import squidpony.squidgrid.fov.EliasFOV;
import squidpony.squidgrid.fov.FOVSolver;
import squidpony.squidgrid.util.RadiusStrategy;

public class AttackCursor extends Cursor {

	private SColor background;
	private FOVSolver fov = new EliasFOV();

	public AttackCursor(Coordinate initialPosition, MapArea mapArea, int maxRadius, RadiusStrategy radiusStrategy) {
		super(initialPosition, mapArea, maxRadius);
		this.background = SColor.DARK_CORAL;

		this.radiusStrategy = radiusStrategy;
		drawFOV();
	}

	@Override
	protected void onDraw(TerminalBase terminal, int sx, int sy) {
		// TODO: maybe some effects here, draw a line or something
		super.onDraw(terminal, sx, sy);
	}

	private void drawFOV() {
		// boolean[][] walls = new boolean[width][height];
		//
		// // walls = ArrayUtils.getSubArray(currentMap.getWalls(), screenArea);
		// lighting = ArrayUtils.getSubArray(currentMap.getLightValues(), screenArea);
		//
		// float lightForce = game.getPlayer().getVisionRadius();
		// float[][] incomingLight = fov.calculateFOV(
		// lighting,
		// player.x - screenArea.x,
		// player.y - screenArea.y,
		// 1f,
		// 1 / lightForce,
		// radiusStrategy);
		//
		// for (int x = screenArea.x; x < screenArea.getMaxX(); x++) {
		// for (int y = screenArea.y; y < screenArea.getMaxY(); y++) {
		// int cX = x - screenArea.x;
		// int cY = y - screenArea.y;
		//
		// Tile tile = currentMap.getTileAt(x, y);
		// tile.setVisible(fov.isLit(cX, cY));
		//
		// if (incomingLight[cX][cY] > 0) {
		// float bright = 1 - incomingLight[cX][cY];
		// tile.setLightedColorValue(SColorFactory.fromPallet("light", bright));
		// // clean[x][y] = false;
		// } else if (!tile.getLightedColorValue().equals(SColor.BLACK)) {
		// tile.setLightedColorValue(SColor.BLACK);
		// // clean[x][y] = false;
		// }
		//
		// }
		// }
	}
}
