package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;

public class DenormalisedH2DatabaseTest extends DatabaseTest<Connection> {

	public DenormalisedH2DatabaseTest() throws Exception {
		super();
	}

	@Override
	protected Database<Connection> createDatabase(DatabaseConfig config,
			Clock clock) {
		return new DenormalisedH2Database(config, clock);
	}
}
