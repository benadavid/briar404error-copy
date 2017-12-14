package org.briarproject.bramble.db;

import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.sync.ValidationManager.State;

/**
 * Represents the status of a message in the database.
 */
class LocalStatus {

	private final MessageId messageId;
	private final long timestamp;
	private final int length;
	private final State state;
	private final boolean shared, deleted;

	LocalStatus(MessageId messageId, long timestamp, int length, State state,
			boolean shared, boolean deleted) {
		this.messageId = messageId;
		this.timestamp = timestamp;
		this.length = length;
		this.state = state;
		this.shared = shared;
		this.deleted = deleted;
	}

	public MessageId getMessageId() {
		return messageId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getLength() {
		return length;
	}

	public State getState() {
		return state;
	}

	public boolean isShared() {
		return shared;
	}

	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public int hashCode() {
		return messageId.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof LocalStatus) {
			LocalStatus ls = (LocalStatus) o;
			return messageId.equals(ls.messageId)
					&& timestamp == ls.timestamp
					&& length == ls.length
					&& state.equals(ls.state)
					&& shared == ls.shared
					&& deleted == ls.deleted;
		}
		return false;
	}
}
