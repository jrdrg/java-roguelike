package roguelike;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class MessageLog implements Serializable {
	private static final long serialVersionUID = 1L;

	private LinkedList<MessageDisplayProperties> messages;
	private int maxSize = 100;

	public MessageLog() {
		this.messages = new LinkedList<MessageDisplayProperties>();
	}

	public void add(MessageDisplayProperties messageProps) {
		this.messages.addFirst(messageProps);

		if (messages.size() > maxSize)
			messages.removeLast();
	}

	public void add(String string) {
		if (string == null)
			return;

		add(new MessageDisplayProperties(string));
	}

	public int size() {
		return messages.size();
	}

	public int size(int maxLines) {
		return Math.min(maxLines, messages.size());
	}

	public List<MessageDisplayProperties> getAll() {
		return messages;
	}

	/**
	 * Returns messages in reverse order, so an index of 0 returns the most recent message
	 * 
	 * @param index
	 * @return
	 */
	public MessageDisplayProperties get(int index) {
		return messages.get(index);
	}
}
