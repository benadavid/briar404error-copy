package org.briarproject.briar.api.messaging;


/**
 * Created by Jean-Michel Laliberté on 4/4/2018.
 */

public class FirebasePrivateMessageHeader extends PrivateMessageHeader {

	protected String sender;
	protected String receiver;

	public FirebasePrivateMessageHeader(String sender, String receiver, long timestamp,
			boolean local, boolean read, boolean sent, boolean seen) {

		this.sender = sender;
		this.receiver = receiver;
		this.timestamp = timestamp;
		this.local = local;
		this.sent = sent;
		this.seen = seen;
		this.read = read;

	}

}
