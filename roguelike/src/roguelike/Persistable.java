package roguelike;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface Persistable<T> {

	public void save(ObjectOutput output) throws IOException;

	public T load(ObjectInput input) throws ClassNotFoundException, IOException;

}
