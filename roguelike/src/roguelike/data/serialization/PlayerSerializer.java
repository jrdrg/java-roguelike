package roguelike.data.serialization;

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
import roguelike.actors.SerializationData;
import roguelike.util.Log;

public class PlayerSerializer {

	public static void serialize(Player player) {
		try {
			SerializationData data = new SerializationData();

			OutputStream file = new FileOutputStream("player.ser");
			GZIPOutputStream gzip = new GZIPOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(gzip);

			player.onSerialize(data);

			output.writeObject(data);

			output.close();

		} catch (Exception e) {
			Log.warning(e.toString());
		}
	}

	public static Player deserialize() {
		try {
			InputStream file = new FileInputStream("player.ser");
			GZIPInputStream gzip = new GZIPInputStream(file);
			ObjectInput input = new ObjectInputStream(gzip);

			SerializationData playerData = (SerializationData) input.readObject();

			input.close();

			return (Player) playerData.restoreActor();

		} catch (Exception e) {
			Log.warning(e.toString());
			return null;
		}
	}
}
