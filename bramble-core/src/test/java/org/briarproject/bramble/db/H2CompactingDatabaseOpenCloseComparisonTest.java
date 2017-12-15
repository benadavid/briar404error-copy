package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class H2CompactingDatabaseOpenCloseComparisonTest
		extends DatabaseOpenCloseComparisonTest {

	@Override
	Database<Connection> createDatabase(boolean conditionA,
			DatabaseConfig databaseConfig, Clock clock) {
		if (conditionA) {
			return new H2Database(databaseConfig, clock);
		} else {
			return new H2Database(databaseConfig, clock) {
				@Override
				public void close() throws DbException {
					// H2 will close the database when the last connection
					// closes, so open a connection to perform compacting
					// before closing other connections
					try {
						Connection c = createConnection();
						assert c != null;
						super.closeAllConnections();
						Statement s = c.createStatement();
						s.execute("SHUTDOWN COMPACT");
						s.close();
						c.close();
					} catch (SQLException e) {
						throw new DbException(e);
					}
				}
			};
		}
	}

	@Override
	protected String getTestName() {
		return getClass().getSimpleName();
	}
}
