package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.KeyParser;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.Message;

import java.security.GeneralSecurityException;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_PUBLIC_KEY_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_SIGNATURE_LENGTH;
import static org.briarproject.bramble.util.ValidationUtils.checkLength;

@Immutable
@NotNullByDefault
class MessageParserImpl implements MessageParser {

	private final ClientHelper clientHelper;
	private final KeyParser oldKeyParser, newKeyParser;

	@Inject
	MessageParserImpl(ClientHelper clientHelper, CryptoComponent crypto) {
		this.clientHelper = clientHelper;
		oldKeyParser = crypto.getSignatureKeyParser();
		newKeyParser = crypto.getEdKeyParser();
	}

	@Override
	public MigrationMessage parse(Message m) throws FormatException {
		BdfList body = clientHelper.toList(m);
		if (body.isEmpty()) throw new FormatException();
		MessageType type = MessageType.fromValue(body.getLong(0).intValue());
		switch (type) {
			case READY:
				return parseReadyMessage(m, body);
			case CERT:
				return parseCertMessage(m, body);
			default:
				throw new AssertionError();
		}
	}

	private ReadyMessage parseReadyMessage(Message m, BdfList body)
			throws FormatException {
		if (body.size() != 1) throw new FormatException();
		return new ReadyMessage(m.getGroupId());
	}

	private CertMessage parseCertMessage(Message m, BdfList body)
			throws FormatException {
		if (body.size() != 6) throw new FormatException();
		String name = body.getString(1);
		checkLength(name, 1, MAX_AUTHOR_NAME_LENGTH);
		byte[] oldPublicKeyBytes = body.getRaw(2);
		checkLength(oldPublicKeyBytes, 1, MAX_PUBLIC_KEY_LENGTH);
		byte[] newPublicKeyBytes = body.getRaw(3);
		checkLength(newPublicKeyBytes, 1, MAX_PUBLIC_KEY_LENGTH);
		byte[] oldSignature = body.getRaw(4);
		checkLength(oldSignature, 1, MAX_SIGNATURE_LENGTH);
		byte[] newSignature = body.getRaw(5);
		checkLength(newSignature, 1, MAX_SIGNATURE_LENGTH);
		PublicKey oldPublicKey, newPublicKey;
		try {
			oldPublicKey = oldKeyParser.parsePublicKey(oldPublicKeyBytes);
			newPublicKey = newKeyParser.parsePublicKey(newPublicKeyBytes);

		} catch (GeneralSecurityException e) {
			throw new FormatException();
		}
		Certificate cert = new Certificate(name, oldPublicKey, newPublicKey,
				oldSignature, newSignature);
		return new CertMessage(m.getGroupId(), cert);
	}
}
