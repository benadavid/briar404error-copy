package org.briarproject.briar.api.messaging;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.Message;

import javax.annotation.concurrent.Immutable;

@Immutable
@NotNullByDefault
public class PrivateMessage {

	private final Message message;

	public PrivateMessage(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public boolean isBold(PrivateMessage PM) { return PM.getMessage().getBold(); }

	public boolean isItalic(PrivateMessage PM) { return PM.getMessage().getItalic(); }

}
