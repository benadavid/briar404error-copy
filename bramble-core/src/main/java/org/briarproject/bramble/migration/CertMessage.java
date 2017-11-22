package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;

import javax.annotation.concurrent.Immutable;

import static org.briarproject.bramble.migration.MessageType.CERT;

@Immutable
@NotNullByDefault
class CertMessage extends MigrationMessage {

	private final Certificate cert;

	CertMessage(GroupId groupId, Certificate cert) {
		super(CERT, groupId);
		this.cert = cert;
	}

	Certificate getCertificate() {
		return cert;
	}
}
