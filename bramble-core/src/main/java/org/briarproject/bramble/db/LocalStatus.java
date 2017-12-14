package org.briarproject.bramble.db;

import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.sync.ValidationManager.State;

/**
 * Represents the status of a message in the database.
 */
class LocalStatus {

	private final MessageId messageId;
	private final State state;
	private final boolean shared, deleted;

	LocalStatus(MessageId messageId, State state,
			boolean shared, boolean deleted) {
		this.messageId = messageId;
		this.state = state;
		this.shared = shared;
		this.deleted = deleted;
	}

	public MessageId getMessageId() {
		return messageId;
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
}
