package roguelike.actors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SerializationData implements Serializable {
	private static final long serialVersionUID = 4344155508471296020L;

	private Map<String, Object> savedData = new HashMap<String, Object>();

	public SerializationData() {
	}

	public Actor restoreActor() {
		Actor actor = null;
		boolean isPlayer = (boolean) getData("isPlayer");
		if (isPlayer) {
			actor = new Player();
		}

		if (actor != null) {
			actor.onDeserialize(this);
			return actor;
		}
		return null;
	}

	public void setData(String key, Object data) {
		savedData.put(key, data);
	}

	public Object getData(String key) {
		return savedData.getOrDefault(key, null);
	}
}
