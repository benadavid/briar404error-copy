package org.briarproject.bramble.api.sync;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

@NotNullByDefault
public interface MessageFactory {

	Message createMessage(GroupId g, long timestamp, byte[] body, boolean bold, boolean italic);

	Message createMessage(MessageId m, byte[] raw, boolean bold, boolean italic);
}
