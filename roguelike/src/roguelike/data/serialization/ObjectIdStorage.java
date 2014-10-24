package roguelike.data.serialization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ObjectIdStorage<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<UUID, T> objects;

	public ObjectIdStorage() {
		objects = new HashMap<UUID, T>();
	}

	public boolean addIfNotExists(UUID key, T item) {
		if (objects.containsKey(key)) {
			return false;
		}
		objects.put(key, item);
		return true;
	}

	public T get(UUID key) {
		return objects.getOrDefault(key, null);
	}

	public T remove(UUID actorId) {
		return objects.remove(actorId);
	}
}
