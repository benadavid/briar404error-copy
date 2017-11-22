package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.Immutable;

@Immutable
@NotNullByDefault
enum MessageType {

	READY(0), CERT(1);

	private final int value;

	MessageType(int value) {
		this.value = value;
	}

	int getValue() {
		return value;
	}

	static MessageType fromValue(int value) throws FormatException {
		for (MessageType t : values()) if (t.value == value) return t;
		throw new FormatException();
	}
}
