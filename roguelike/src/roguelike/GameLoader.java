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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import roguelike.actors.Player;
import roguelike.items.Equipment.ItemSlot;
import roguelike.items.MeleeWeapon;
import roguelike.items.Projectile;
import roguelike.items.RangedWeapon;
import roguelike.items.WeaponFactory;
import roguelike.items.WeaponType;
import roguelike.maps.DungeonMapBuilder;
import roguelike.maps.MapArea;
import squidpony.squidmath.RNG;

public class GameLoader {
    private static final Logger LOG = LogManager.getLogger(GameLoader.class);

    private GameLoader() {
    }

    public static Game newGame() {

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

        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.warn(e.toString());
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

        }
        catch (Exception e) {
            e.printStackTrace();
            LOG.warn(e.toString());
            return null;
        }
        return game;
    }

    public static Player createPlayer() {
        Player player = new Player();

        MeleeWeapon sword = (MeleeWeapon) WeaponFactory.create(WeaponType.SHORT_SWORD);
        MeleeWeapon spear = (MeleeWeapon) WeaponFactory.create(WeaponType.SPEAR);
        RangedWeapon bow = (RangedWeapon) WeaponFactory.create(WeaponType.SHORT_BOW);
        Projectile arrow = (Projectile) WeaponFactory.create(WeaponType.ARROW);

        player.inventory().add(spear);
        player.inventory().add(bow);
        player.inventory().add(arrow);

        ItemSlot.RIGHT_HAND.equipItem(player, sword);

        return player;
    }

    public static RNG getRandom() {
        return new RNG();
    }
}
