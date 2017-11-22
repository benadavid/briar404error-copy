package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;

import javax.annotation.concurrent.Immutable;

@Immutable
@NotNullByDefault
abstract class MigrationMessage {

	private final MessageType type;
	private final GroupId groupId;

	MigrationMessage(MessageType type, GroupId groupId) {
		this.type = type;
		this.groupId = groupId;
	}

	MessageType getType() {
		return type;
	}

	GroupId getGroupId() {
		return groupId;
	}
}
