package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Metadata;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.system.Clock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.briarproject.bramble.api.sync.ValidationManager.State.DELIVERED;

@NotNullByDefault
class UnorderedH2Database extends H2Database {

	UnorderedH2Database(DatabaseConfig config, Clock clock) {
		super(config, clock);
	}

	@Override
	public Map<MessageId, Metadata> getMessageMetadata(Connection txn,
			GroupId g) throws DbException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT m.messageId, metaKey, value"
					+ " FROM messages AS m"
					+ " JOIN messageMetadata AS md"
					+ " ON m.messageId = md.messageId"
					+ " WHERE state = ? AND groupId = ?";
			ps = txn.prepareStatement(sql);
			ps.setInt(1, DELIVERED.getValue());
			ps.setBytes(2, g.getBytes());
			rs = ps.executeQuery();
			Map<MessageId, Metadata> all = new HashMap<>();
			while (rs.next()) {
				MessageId messageId = new MessageId(rs.getBytes(1));
				Metadata metadata = all.get(messageId);
				if (metadata == null) {
					metadata = new Metadata();
					all.put(messageId, metadata);
				}
				metadata.put(rs.getString(2), rs.getBytes(3));
			}
			rs.close();
			ps.close();
			return all;
		} catch (SQLException e) {
			tryToClose(rs);
			tryToClose(ps);
			throw new DbException(e);
		}
	}
}
