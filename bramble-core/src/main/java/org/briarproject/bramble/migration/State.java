package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.Immutable;

@Immutable
@NotNullByDefault
enum State {

	START(0), AWAIT_BOTH(1), AWAIT_READY(2), AWAIT_CERT(3), FINISHED(4),
	ERROR(5);

	private final int value;

	State(int value) {
		this.value = value;
	}

	int getValue() {
		return value;
	}

	static State fromValue(int value) throws FormatException {
		for (State s : values()) if (s.value == value) return s;
		throw new FormatException();
	}
}
