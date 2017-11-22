package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;

import javax.annotation.concurrent.Immutable;

import static org.briarproject.bramble.migration.MessageType.READY;

@Immutable
@NotNullByDefault
class ReadyMessage extends MigrationMessage {

	ReadyMessage(GroupId groupId) {
		super(READY, groupId);
	}
}
