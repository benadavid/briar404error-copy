package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.Message;

@NotNullByDefault
interface MessageParser {

	MigrationMessage parse(Message m) throws FormatException;
}
