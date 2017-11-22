package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

@NotNullByDefault
interface CertificateEncoder {

	BdfDictionary encode(Certificate cert);
}
