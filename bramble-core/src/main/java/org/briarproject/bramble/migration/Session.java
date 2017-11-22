package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;

import javax.annotation.concurrent.Immutable;

@Immutable
@NotNullByDefault
class Session {

	private final GroupId groupId;
	private final State state;

	Session(GroupId groupId, State state) {
		this.groupId = groupId;
		this.state = state;
	}

	GroupId getGroupId() {
		return groupId;
	}

	State getState() {
		return state;
	}
}
