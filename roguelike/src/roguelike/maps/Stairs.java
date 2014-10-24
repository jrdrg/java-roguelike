package roguelike.maps;

import roguelike.Game;
import roguelike.actors.Actor;

public class Stairs extends Tile {

	private static final long serialVersionUID = 1L;

	private MapBuilderBase mapBuilder;
	private boolean down;

	public Stairs(MapBuilderBase mapBuilder, boolean isDown) {
		this.mapBuilder = mapBuilder;
		this.down = isDown;
	}

	@Override
	public char getSymbol() {
		if (getActor() != null && visible) {
			return getActor().symbol();
		}

		return down ? '>' : '<';
	}

	public boolean isDown() {
		return down;
	}

	public void use() {
		MapArea oldMap = Game.current().getCurrentMapArea();

		MapArea newMap = new MapArea(Game.MAP_WIDTH, Game.MAP_HEIGHT, mapBuilder);

		// TODO: put stairs going back up wherever the player gets created

		Actor actor = getActor();
		oldMap.removeActor(actor);

		newMap.addActor(actor);

		Game.current().setCurrentMapArea(newMap);
	}
}
