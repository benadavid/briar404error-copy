package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.db.Metadata;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.InvalidMessageException;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageContext;
import org.briarproject.bramble.api.sync.ValidationManager.MessageValidator;

import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.briarproject.bramble.api.migration.Certificate.LABEL;

@NotNullByDefault
class MigrationValidator implements MessageValidator {

	private final MessageParser messageParser;
	private final ClientHelper clientHelper;
	private final CryptoComponent crypto;

	MigrationValidator(MessageParser messageParser, ClientHelper clientHelper,
			CryptoComponent crypto) {
		this.messageParser = messageParser;
		this.clientHelper = clientHelper;
		this.crypto = crypto;
	}

	@Override
	public MessageContext validateMessage(Message m, Group g)
			throws InvalidMessageException {
		try {
			MigrationMessage mm = messageParser.parse(m);
			switch (mm.getType()) {
				case READY:
					break;
				case CERT:
					validateCertificate(((CertMessage) mm).getCertificate());
					break;
				default:
					throw new AssertionError();
			}
			return new MessageContext(new Metadata(), Collections.emptyList());
		} catch (FormatException e) {
			throw new InvalidMessageException(e);
		}
	}

	private void validateCertificate(Certificate cert) throws FormatException {
		byte[] oldPublicKey = cert.getOldPublicKey().getEncoded();
		byte[] newPublicKey = cert.getNewPublicKey().getEncoded();
		BdfList signed = BdfList.of(cert.getName(), oldPublicKey, newPublicKey);
		byte[] signedBytes = clientHelper.toByteArray(signed);
		try {
			boolean oldValid = crypto.verify(LABEL, signedBytes, oldPublicKey,
					cert.getOldSignature());
			if (!oldValid) throw new FormatException();
			boolean newValid = crypto.verifyEd(LABEL, signedBytes, newPublicKey,
					cert.getNewSignature());
			if (!newValid) throw new FormatException();
		} catch (GeneralSecurityException e) {
			throw new FormatException();
		}
	}
}
