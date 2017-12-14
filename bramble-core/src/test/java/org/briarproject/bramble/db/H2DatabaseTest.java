package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;

public class H2DatabaseTest extends DatabaseTest<Connection> {

	public H2DatabaseTest() throws Exception {
		super();
	}

	@Override
	protected Database<Connection> createDatabase(DatabaseConfig config,
			Clock clock) {
		return new H2Database(config, clock);
	}
}
