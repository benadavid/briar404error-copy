package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import static java.util.logging.Level.WARNING;

@NotNullByDefault
class CachingH2Database extends H2Database {

	private static final Logger LOG =
			Logger.getLogger(CachingH2Database.class.getName());

	private final Map<Connection, Map<String, PreparedStatement>>
			statementCache = new ConcurrentHashMap<>();

	CachingH2Database(DatabaseConfig config, Clock clock) {
		super(config, clock);
	}

	@Override
	protected Connection createConnection() throws SQLException {
		Connection txn = super.createConnection();
		statementCache.put(txn, new HashMap<>());
		return txn;
	}

	@Override
	protected void closeConnection(Connection txn) throws SQLException {
		Map<String, PreparedStatement> cached = statementCache.remove(txn);
		if (cached == null) throw new IllegalStateException();
		for (PreparedStatement ps : cached.values()) super.tryToClose(ps);
		txn.close();
	}

	@Override
	protected void close(PreparedStatement ps) throws SQLException {
		try {
			ps.clearParameters();
		} catch (SQLException e) {
			tryToClose(ps);
			throw e;
		}
	}

	@Override
	protected void tryToClose(@Nullable PreparedStatement ps) {
		if (ps != null) removeFromCache(ps);
		super.tryToClose(ps);
	}

	private void removeFromCache(PreparedStatement ps) {
		try {
			Connection txn = ps.getConnection();
			Map<String, PreparedStatement> cached = statementCache.get(txn);
			if (cached == null) throw new IllegalStateException();
			cached.values().remove(ps);
		} catch (SQLException e) {
			if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
		}
	}

	@Override
	protected PreparedStatement prepareStatement(Connection txn, String sql)
			throws SQLException {
		Map<String, PreparedStatement> cached = statementCache.get(txn);
		if (cached == null) throw new IllegalStateException();
		PreparedStatement ps = cached.get(sql);
		if (ps == null) {
			ps = super.prepareStatement(txn, sql);
			cached.put(sql, ps);
		}
		return ps;
	}
}
