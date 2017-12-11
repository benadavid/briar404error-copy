package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.system.Clock;
import org.junit.Ignore;

import java.io.File;
import java.sql.Connection;

import javax.annotation.Nullable;

@Ignore
public class H2SeparateBodiesDatabaseTraceTest extends DatabaseTraceTest {

	@Override
	Database<Connection> createDatabase(DatabaseConfig databaseConfig,
			Clock clock) {
		return new SeparateBodiesH2Database(databaseConfig, clock);
	}

	@Nullable
	@Override
	protected File getTraceFile() {
		return new File(testDir, "db.trace.db");
	}

	@Override
	protected String getTestName() {
		return getClass().getSimpleName();
	}
}
