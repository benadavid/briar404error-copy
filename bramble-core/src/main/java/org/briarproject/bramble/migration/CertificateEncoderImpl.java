package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.data.BdfEntry;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_NAME;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_NEW_PUBLIC_KEY;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_NEW_SIGNATURE;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_OLD_PUBLIC_KEY;
import static org.briarproject.bramble.migration.MigrationConstants.CERT_KEY_OLD_SIGNATURE;

@Immutable
@NotNullByDefault
class CertificateEncoderImpl implements CertificateEncoder {

	@Inject
	CertificateEncoderImpl() {
	}

	@Override
	public BdfDictionary encode(Certificate cert) {
		return BdfDictionary.of(
				new BdfEntry(CERT_KEY_NAME, cert.getName()),
				new BdfEntry(CERT_KEY_OLD_PUBLIC_KEY,
						cert.getOldPublicKey().getEncoded()),
				new BdfEntry(CERT_KEY_NEW_PUBLIC_KEY,
						cert.getNewPublicKey().getEncoded()),
				new BdfEntry(CERT_KEY_OLD_SIGNATURE, cert.getOldSignature()),
				new BdfEntry(CERT_KEY_NEW_SIGNATURE, cert.getNewSignature())
		);
	}
}
