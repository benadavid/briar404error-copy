package org.briarproject.bramble.api.migration;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.ClientId;

import javax.annotation.Nullable;

@NotNullByDefault
public interface MigrationManager {

	/**
	 * The unique ID of the identity migration client.
	 */
	ClientId CLIENT_ID = new ClientId("org.briarproject.briar.migration");

	/**
	 * Returns true if we've exchanged migration certificates with the given
	 * contact.
	 */
	boolean hasMigrated(Transaction txn, Contact c) throws DbException;

	/**
	 * Returns the given contact's migration certificate, if any.
	 */
	@Nullable
	Certificate getCertificate(Transaction txn, Contact c) throws DbException;
}
