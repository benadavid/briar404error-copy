package org.briarproject.bramble.api.sync;

import static org.briarproject.bramble.api.sync.SyncConstants.MAX_MESSAGE_LENGTH;
import static org.briarproject.bramble.api.sync.SyncConstants.MESSAGE_HEADER_LENGTH;

public class Message {

	private final MessageId id;
	private final GroupId groupId;
	private final long timestamp;
	private final byte[] raw;
	private final boolean bold;
	private final boolean italic;

	public Message(MessageId ID, GroupId groupId, long timestamp, byte[] raw, boolean bold, boolean italic) {
		if (raw.length <= MESSAGE_HEADER_LENGTH)
			throw new IllegalArgumentException();
		if (raw.length > MAX_MESSAGE_LENGTH)
			throw new IllegalArgumentException();

		this.id = ID;
		this.groupId = groupId;
		this.timestamp = timestamp;
		this.raw = raw;
		this.bold = bold;
		this.italic = italic;
	}

	/**
	 * Returns the message's unique identifier.
	 */
	public MessageId getId() {
		return id;
	}

	/**
	 * Returns the ID of the {@link Group} to which the message belongs.
	 */
	public GroupId getGroupId() {
		return groupId;
	}

	/**
	 * Returns the message's timestamp in milliseconds since the Unix epoch.
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the length of the raw message in bytes.
	 */
	public int getLength() {
		return raw.length;
	}

	/**
	 * Returns the raw message.
	 */
	public byte[] getRaw() {
		return raw;
	}

	/**
	 * Returns the bold value of the message.
	 */
	public boolean getBold() {return bold;}

	/**
	 * Returns the italic value of the message.
	 */
	public boolean getItalic() {return italic;}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Message && id.equals(((Message) o).getId());
	}
}