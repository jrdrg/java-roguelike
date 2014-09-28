package roguelike.maps;

public class Stairs extends Tile {

	private static final long serialVersionUID = -6367110457761317507L;

	private MapArea leadsTo;
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
}
