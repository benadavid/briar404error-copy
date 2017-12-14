package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;

public class HyperSqlDatabaseTest extends DatabaseTest<Connection> {

	public HyperSqlDatabaseTest() throws Exception {
		super();
	}

	@Override
	protected Database<Connection> createDatabase(DatabaseConfig config,
			Clock clock) {
		return new HyperSqlDatabase(config, clock);
	}
}
