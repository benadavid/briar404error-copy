package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.api.migration.Certificate;

interface CertificateFactory {

	Certificate createCertificate(LocalAuthor a);
}
