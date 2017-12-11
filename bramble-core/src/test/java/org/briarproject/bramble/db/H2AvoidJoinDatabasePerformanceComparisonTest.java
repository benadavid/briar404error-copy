package org.briarproject.bramble.db;

import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.system.Clock;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;

@Ignore
public class H2AvoidJoinDatabasePerformanceComparisonTest
		extends DatabasePerformanceComparisonTest {

	@Override
	Database<Connection> createDatabase(boolean conditionA,
			DatabaseConfig databaseConfig, Clock clock) {
		if (conditionA) return new H2Database(databaseConfig, clock);
		else return new AvoidJoinH2Database(databaseConfig, clock);
	}

	@Override
	protected String getTestName() {
		return getClass().getSimpleName();
	}

	@Ignore
	@Test
	public void testContainsContactByAuthorId() throws Exception {
	}

	@Ignore
	@Test
	public void testContainsContactByContactId() throws Exception {
	}

	@Ignore
	@Test
	public void testContainsGroup() throws Exception {
	}

	@Ignore
	@Test
	public void testContainsLocalAuthor() throws Exception {
	}

	@Ignore
	@Test
	public void testContainsMessage() throws Exception {
	}

	@Ignore
	@Test
	public void testContainsVisibleMessage() throws Exception {
	}

	@Ignore
	@Test
	public void testCountOfferedMessages() throws Exception {
	}

	@Ignore
	@Test
	public void testGetContact() throws Exception {
	}

	@Ignore
	@Test
	public void testGetContacts() throws Exception {
	}

	@Ignore
	@Test
	public void testGetContactsByRemoteAuthorId() throws Exception {
	}

	@Ignore
	@Test
	public void testGetContactsByLocalAuthorId() throws Exception {
	}

	@Ignore
	@Test
	public void testGetGroup() throws Exception {
	}

	@Ignore
	@Test
	public void testGetGroupMetadata() throws Exception {
	}

	@Ignore
	@Test
	public void testGetGroups() throws Exception {
	}

	@Ignore
	@Test
	public void testGetGroupVisibilityWithContactId() throws Exception {
	}

	@Ignore
	@Test
	public void testGetGroupVisibility() throws Exception {
	}

	@Ignore
	@Test
	public void testGetLocalAuthor() throws Exception {
	}

	@Ignore
	@Test
	public void testGetLocalAuthors() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageDependencies() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageDependents() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageIds() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageIdsWithMatchingQuery() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageIdsWithNonMatchingQuery() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageMetadataByMessageId() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageMetadataForValidator() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageState() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageStatusByGroupId() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessageStatusByMessageId() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessagesToAck() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessagesToOffer() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessagesToRequest() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessagesToSend() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessagesToShare() throws Exception {
	}

	@Ignore
	@Test
	public void testGetMessagesToValidate() throws Exception {
	}

	@Ignore
	@Test
	public void testGetPendingMessages() throws Exception {
	}

	@Ignore
	@Test
	public void testGetRawMessage() throws Exception {
	}

	@Ignore
	@Test
	public void testGetRequestedMessagesToSend() throws Exception {
	}
}
