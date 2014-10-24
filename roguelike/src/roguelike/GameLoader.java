package roguelike;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import roguelike.actors.Player;
import roguelike.actors.conditions.Poisoned;
import roguelike.actors.conditions.ReducedVision;
import roguelike.actors.conditions.Stunned;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.MeleeWeapon;
import roguelike.items.Projectile;
import roguelike.items.RangedWeapon;
import roguelike.items.WeaponFactory;
import roguelike.items.WeaponType;
import roguelike.maps.DungeonMapBuilder;
import roguelike.maps.MapArea;
import roguelike.util.Log;
import squidpony.squidmath.RNG;

public class GameLoader {

	private static GameLoader gameLoader = new GameLoader();

	private GameLoader() {
	}

	public static GameLoader instance() {
		return gameLoader;
	}

	public Game newGame() {

		Game game = new Game();
		Player player = game.getPlayer();
		MapArea currentMapArea = MapArea.build(Game.MAP_WIDTH, Game.MAP_HEIGHT, new DungeonMapBuilder());
		currentMapArea.addActor(player);

		game.setCurrentMapArea(currentMapArea);
		return game;
	}

	public static void save(Game game) {
		try {
			OutputStream file = new FileOutputStream("saves/game.ser");
			GZIPOutputStream gzip = new GZIPOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(gzip);

			output.writeObject(game);

			output.close();

		} catch (Exception e) {
			e.printStackTrace();
			Log.warning(e.toString());
		}
	}

	public static Game load() {
		Game game = new Game();
		try {
			InputStream file = new FileInputStream("saves/game.ser");
			GZIPInputStream gzip = new GZIPInputStream(file);
			ObjectInput input = new ObjectInputStream(gzip);

			game = (Game) input.readObject();

			input.close();

		} catch (Exception e) {
			e.printStackTrace();
			Log.warning(e.toString());
			return null;
		}
		return game;
	}

	public Player createPlayer() {
		Player player = new Player();

		MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create(WeaponType.SHORT_SWORD);
		MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create(WeaponType.SPEAR);
		RangedWeapon bow = (RangedWeapon) WeaponFactory.create(WeaponType.SHORT_BOW);
		Projectile arrow = (Projectile) WeaponFactory.create(WeaponType.ARROW);

		player.inventory().add(spear);
		player.inventory().add(bow);
		player.inventory().add(arrow);

		ItemSlot.RIGHT_HAND.equipItem(player, sword);

		player.addCondition(new Poisoned(10));
		player.addCondition(new Stunned(5));
		player.addCondition(new ReducedVision(114));

		return player;
	}

	public RNG getRandom() {
		return new RNG();
	}
}
