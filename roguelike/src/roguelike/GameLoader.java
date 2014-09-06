package roguelike;

import roguelike.actors.Actor;
import roguelike.actors.NpcBuilder;
import roguelike.actors.Player;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.MeleeWeapon;
import roguelike.maps.MapArea;
import roguelike.maps.MapBuilder;
import squidpony.squidmath.RNG;

public class GameLoader {

	public GameLoader() {
	}

	public Game newGame() {
		Game game = new Game(this);
		Player player = game.getPlayer();

		// player.setPosition(1, 1);

		// TODO: make a real map
		MapArea currentMapArea = new MapArea(300, 300, new MapBuilder(game));
		currentMapArea.addActor(player);

		NpcBuilder npcBuilder = new NpcBuilder(game);

		for (int x = 0; x < 35; x++) {
			Actor npc = npcBuilder.buildNpc(currentMapArea);
			currentMapArea.addActor(npc);
		}

		game.setCurrentMapArea(currentMapArea);

		return game;
	}

	public Game load() {
		// TODO: load game from file instead
		return newGame();
	}

	public Player createPlayer(Game game) {
		Player player = new Player(game);

		MeleeWeapon sword = new MeleeWeapon("Sword", "A long sword", 25);
		MeleeWeapon spear = new MeleeWeapon("Spear", "A spear", 20);
		player.getInventory().add(spear);

		player.getEquipment().equipItem(ItemSlot.RIGHT_ARM, sword, player.getInventory());

		return player;
	}

	public Player loadPlayer(Game game) {
		// TODO: load player from file instead
		return createPlayer(game);
	}

	public RNG getRandom() {
		return new RNG();
	}
}
