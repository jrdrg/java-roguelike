package roguelike;

import roguelike.actors.Player;
import roguelike.data.DataFactory;
import roguelike.data.serialization.PlayerSerializer;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.MeleeWeapon;
import roguelike.items.RangedWeapon;
import roguelike.items.WeaponFactory;
import roguelike.maps.DungeonMapBuilder;
import roguelike.maps.MapArea;
import roguelike.maps.MapBuilder;
import squidpony.squidmath.RNG;

public class GameLoader {

	private static GameLoader gameLoader = new GameLoader();

	/**
	 * Singleton DataFactory
	 */
	final DataFactory dataFactory;

	private GameLoader() {
		dataFactory = DataFactory.instance();
	}

	public static GameLoader instance() {
		return gameLoader;
	}

	public Game newGame() {

		Game game = new Game();
		Player player = game.getPlayer();

		// player.setPosition(1, 1);

		// TODO: make a real map
		// MapArea currentMapArea = new MapArea(200, 200, new MapBuilder());
		MapArea currentMapArea = new MapArea(83, 43, new DungeonMapBuilder());
		currentMapArea.addActor(player);

		game.setCurrentMapArea(currentMapArea);
		return game;
	}

	public Game load() {

		Game game = Game.load();
		Player player = game.getPlayer();

		// TODO: load game and map from file

		// MapArea currentMapArea = new MapArea(200, 200, new MapBuilder());
		MapArea currentMapArea = new MapArea(83, 43, new DungeonMapBuilder());
		currentMapArea.addActor(player);

		game.setCurrentMapArea(currentMapArea);
		return game;
	}

	public Player createPlayer() {
		Player player = new Player();

		MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create("sword");
		MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create("spear");
		RangedWeapon bow = (RangedWeapon) WeaponFactory.create("short bow");

		player.inventory().add(spear);
		player.inventory().add(bow);

		ItemSlot.RIGHT_ARM.equipItem(player, sword);

		return player;
	}

	public Player loadPlayer() {
		return PlayerSerializer.deserialize();
	}

	public RNG getRandom() {
		return new RNG();
	}
}
