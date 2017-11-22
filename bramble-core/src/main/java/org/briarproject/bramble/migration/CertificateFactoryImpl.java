package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.KeyPair;
import org.briarproject.bramble.api.crypto.KeyParser;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.api.migration.Certificate;

import java.security.GeneralSecurityException;

import javax.inject.Inject;

import static org.briarproject.bramble.api.migration.Certificate.LABEL;

class CertificateFactoryImpl implements CertificateFactory {

	private final CryptoComponent crypto;
	private final ClientHelper clientHelper;

	@Inject
	CertificateFactoryImpl(CryptoComponent crypto, ClientHelper clientHelper) {
		this.crypto = crypto;
		this.clientHelper = clientHelper;
	}

	@Override
	public Certificate createCertificate(LocalAuthor a) {
		KeyPair newKeyPair = crypto.generateEdKeyPair();
		BdfList listToSign = BdfList.of(
				a.getName(),
				a.getPublicKey(),
				newKeyPair.getPublic().getEncoded()
		);
		try {
			byte[] bytesToSign = clientHelper.toByteArray(listToSign);
			byte[] oldSignature = crypto.sign(LABEL, bytesToSign,
					a.getPrivateKey());
			byte[] newSignature = crypto.signEd(LABEL, bytesToSign,
					newKeyPair.getPrivate().getEncoded());
			KeyParser oldParser = crypto.getSignatureKeyParser();
			PublicKey oldPublicKey = oldParser.parsePublicKey(a.getPublicKey());
			return new Certificate(a.getName(), oldPublicKey,
					newKeyPair.getPublic(), oldSignature, newSignature);
		} catch (FormatException | GeneralSecurityException e) {
			throw new AssertionError(e);
		}
	}
}
