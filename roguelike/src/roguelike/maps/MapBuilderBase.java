package roguelike.maps;

import roguelike.Game;
import squidpony.squidmath.RNG;

public abstract class MapBuilderBase {
	protected RNG random = Game.current().random();
	protected TileBuilder tb = new TileBuilder();

	public abstract void buildMap(Tile[][] map);
}
