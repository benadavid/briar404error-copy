package org.briarproject.bramble.sync;

import org.briarproject.bramble.api.sync.MessageId;

/**
 * Represents the status of a message in the database.
 */
public class SharingStatus {

	private final MessageId messageId;
	private final boolean shared, deleted;

	public SharingStatus(MessageId messageId, boolean shared, boolean deleted) {
		this.messageId = messageId;
		this.shared = shared;
		this.deleted = deleted;
	}

	public MessageId getMessageId() {
		return messageId;
	}

	public boolean isShared() {
		return shared;
	}

	public boolean isDeleted() {
		return deleted;
	}
}
