package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;

@NotNullByDefault
class UnindexedH2Database extends H2Database {

	UnindexedH2Database(DatabaseConfig config, Clock clock) {
		super(config, clock);
	}

	@Override
	protected void createIndexes(Connection txn) throws DbException {
		// Not today, thank you
	}
}
