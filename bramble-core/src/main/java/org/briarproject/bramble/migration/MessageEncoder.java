package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;

@NotNullByDefault
interface MessageEncoder {

	Message encodeReadyMessage(GroupId g);

	Message encodeCertMessage(GroupId g, Certificate cert);
}
