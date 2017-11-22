package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.KeyParser;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import java.security.GeneralSecurityException;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_NAME;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_NEW_PUBLIC_KEY;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_NEW_SIGNATURE;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_OLD_PUBLIC_KEY;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_OLD_SIGNATURE;

@Immutable
@NotNullByDefault
class CertificateParserImpl implements CertificateParser {

	private final KeyParser oldParser, newParser;

	@Inject
	CertificateParserImpl(CryptoComponent crypto) {
		oldParser = crypto.getSignatureKeyParser();
		newParser = crypto.getEdKeyParser();
	}

	@Override
	public Certificate parse(BdfDictionary d) throws FormatException {
		String name = d.getString(CERT_KEY_NAME);
		byte[] oldPublicKeyBytes = d.getRaw(CERT_KEY_OLD_PUBLIC_KEY);
		byte[] newPublicKeyBytes = d.getRaw(CERT_KEY_NEW_PUBLIC_KEY);
		byte[] oldSignature = d.getRaw(CERT_KEY_OLD_SIGNATURE);
		byte[] newSignature = d.getRaw(CERT_KEY_NEW_SIGNATURE);
		try {
			PublicKey oldPublicKey =
					oldParser.parsePublicKey(oldPublicKeyBytes);
			PublicKey newPublicKey =
					newParser.parsePublicKey(newPublicKeyBytes);
			return new Certificate(name, oldPublicKey, newPublicKey,
					oldSignature, newSignature);
		} catch (GeneralSecurityException e) {
			throw new FormatException();
		}
	}
}
