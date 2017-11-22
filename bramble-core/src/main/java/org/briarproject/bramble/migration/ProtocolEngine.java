package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

@NotNullByDefault
interface ProtocolEngine {

	Session onReadyAction(Transaction txn, Session s) throws DbException;

	Session onReadyMessage(Transaction txn, Session s, ReadyMessage m)
			throws DbException, FormatException;

	Session onCertMessage(Transaction txn, Session s, CertMessage m)
			throws DbException, FormatException;
}
