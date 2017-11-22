package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.system.Clock;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import static org.briarproject.bramble.migration.MessageType.CERT;
import static org.briarproject.bramble.migration.MessageType.READY;

@Immutable
@NotNullByDefault
class MessageEncoderImpl implements MessageEncoder {

	private final ClientHelper clientHelper;
	private final Clock clock;

	@Inject
	MessageEncoderImpl(ClientHelper clientHelper, Clock clock) {
		this.clientHelper = clientHelper;
		this.clock = clock;
	}

	@Override
	public Message encodeReadyMessage(GroupId g) {
		return createMessage(g, BdfList.of(READY.getValue()));
	}

	@Override
	public Message encodeCertMessage(GroupId g, Certificate cert) {
		BdfList body = BdfList.of(
				CERT.getValue(),
				cert.getName(),
				cert.getOldPublicKey().getEncoded(),
				cert.getNewPublicKey().getEncoded(),
				cert.getOldSignature(),
				cert.getNewSignature()
		);
		return createMessage(g, body);
	}

	private Message createMessage(GroupId g, BdfList body) {
		long timestamp = clock.currentTimeMillis();
		try {
			return clientHelper.createMessage(g, timestamp, body);
		} catch (FormatException e) {
			throw new AssertionError(e);
		}
	}
}
