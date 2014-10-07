package roguelike.maps;

import roguelike.Game;
import roguelike.actors.Actor;

public class Stairs extends Tile {

	private static final long serialVersionUID = -6367110457761317507L;

	private MapBuilderBase mapBuilder;

	public Stairs(MapBuilderBase mapBuilder) {
		this.mapBuilder = mapBuilder;
	}

	@Override
	public char getSymbol() {
		if (getActor() != null && visible) {
			return getActor().symbol();
		}

		return '>';
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
