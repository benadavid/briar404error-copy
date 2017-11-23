package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.client.ContactGroupFactory;
import org.briarproject.bramble.api.client.ProtocolStateException;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.data.BdfEntry;
import org.briarproject.bramble.api.db.DatabaseComponent;
import org.briarproject.bramble.api.db.Metadata;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.test.BrambleMockTestCase;
import org.jmock.Expectations;
import org.junit.Test;

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_SIGNATURE_LENGTH;
import static org.briarproject.bramble.api.migration.MigrationManager.CLIENT_ID;
import static org.briarproject.bramble.api.sync.SyncConstants.MAX_GROUP_DESCRIPTOR_LENGTH;
import static org.briarproject.bramble.api.sync.SyncConstants.MAX_MESSAGE_LENGTH;
import static org.briarproject.bramble.migration.MigrationConstants.GROUP_KEY_CERT;
import static org.briarproject.bramble.migration.State.AWAIT_BOTH;
import static org.briarproject.bramble.migration.State.AWAIT_CERT;
import static org.briarproject.bramble.migration.State.AWAIT_READY;
import static org.briarproject.bramble.migration.State.ERROR;
import static org.briarproject.bramble.migration.State.FINISHED;
import static org.briarproject.bramble.migration.State.START;
import static org.briarproject.bramble.test.TestUtils.getRandomBytes;
import static org.briarproject.bramble.test.TestUtils.getRandomId;
import static org.briarproject.bramble.util.StringUtils.getRandomString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ProtocolEngineImplTest extends BrambleMockTestCase {

	private final DatabaseComponent db = context.mock(DatabaseComponent.class);
	private final ClientHelper clientHelper = context.mock(ClientHelper.class);
	private final ContactGroupFactory contactGroupFactory =
			context.mock(ContactGroupFactory.class);
	private final MessageEncoder messageEncoder =
			context.mock(MessageEncoder.class);
	private final CertificateParser certificateParser =
			context.mock(CertificateParser.class);
	private final CertificateEncoder certificateEncoder =
			context.mock(CertificateEncoder.class);
	private final PublicKey oldPublicKey = context.mock(PublicKey.class, "old");
	private final PublicKey newPublicKey = context.mock(PublicKey.class, "new");

	private final GroupId groupId = new GroupId(getRandomId());
	private final MessageId messageId = new MessageId(getRandomId());
	private final long timestamp = System.currentTimeMillis();
	private final byte[] raw = getRandomBytes(MAX_MESSAGE_LENGTH);
	private final Message message =
			new Message(messageId, groupId, timestamp, raw);
	private final ReadyMessage readyMessage = new ReadyMessage(groupId);
	private final Group localGroup = new Group(new GroupId(getRandomId()),
			CLIENT_ID, getRandomBytes(MAX_GROUP_DESCRIPTOR_LENGTH));
	private final String name = getRandomString(MAX_AUTHOR_NAME_LENGTH);
	private final byte[] oldSignature = getRandomBytes(MAX_SIGNATURE_LENGTH);
	private final byte[] newSignature = getRandomBytes(MAX_SIGNATURE_LENGTH);
	private final Certificate cert = new Certificate(name, oldPublicKey,
			newPublicKey, oldSignature, newSignature);
	private final CertMessage certMessage = new CertMessage(groupId, cert);

	private final ProtocolEngineImpl engine = new ProtocolEngineImpl(db,
			clientHelper, contactGroupFactory, messageEncoder,
			certificateParser, certificateEncoder);

	@Test
	public void testReadyActionThrowsExceptionInWrongState() throws Exception {
		for (State s : State.values()) {
			if (s == START) continue;
			try {
				Transaction txn = new Transaction(null, false);
				engine.onReadyAction(txn, new Session(groupId, s));
				fail();
			} catch (ProtocolStateException expected) {
				// Expected
			}
		}
	}

	@Test
	public void testReadyActionSendsReadyMessage() throws Exception {
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, START);

		context.checking(new Expectations() {{
			oneOf(messageEncoder).encodeReadyMessage(groupId);
			will(returnValue(message));
			oneOf(db).addLocalMessage(txn, message, new Metadata(), true);
		}});

		session = engine.onReadyAction(txn, session);

		assertEquals(groupId, session.getGroupId());
		assertEquals(AWAIT_BOTH, session.getState());
	}

	@Test
	public void testReadyMessageAbortsInWrongState() throws Exception {
		State[] wrong = {START, AWAIT_CERT, FINISHED, ERROR};
		for (State s : wrong) testReadyMessageAbortsInState(s);
	}

	private void testReadyMessageAbortsInState(State s) throws Exception {
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, s);
		session = engine.onReadyMessage(txn, session, readyMessage);
		assertEquals(groupId, session.getGroupId());
		assertEquals(ERROR, session.getState());
	}

	@Test
	public void testReadyMessageSendsCertInStateAwaitBoth() throws Exception {
		BdfDictionary localCertDict = new BdfDictionary();
		BdfDictionary localGroupMeta =
				BdfDictionary.of(new BdfEntry(GROUP_KEY_CERT, localCertDict));
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, AWAIT_BOTH);

		context.checking(new Expectations() {{
			// Load the local certificate
			oneOf(contactGroupFactory).createLocalGroup(CLIENT_ID);
			will(returnValue(localGroup));
			oneOf(clientHelper).getGroupMetadataAsDictionary(txn,
					localGroup.getId());
			will(returnValue(localGroupMeta));
			// Parse the local certificate
			oneOf(certificateParser).parse(localCertDict);
			will(returnValue(cert));
			// Send a cert message
			oneOf(messageEncoder).encodeCertMessage(groupId, cert);
			will(returnValue(message));
			oneOf(db).addLocalMessage(txn, message, new Metadata(), true);
		}});

		session = engine.onReadyMessage(txn, session, readyMessage);

		assertEquals(groupId, session.getGroupId());
		assertEquals(AWAIT_CERT, session.getState());
	}

	@Test
	public void testReadyMessageSetsStateFinishedInStateAwaitReady()
			throws Exception {
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, AWAIT_READY);
		session = engine.onReadyMessage(txn, session, readyMessage);
		assertEquals(groupId, session.getGroupId());
		assertEquals(FINISHED, session.getState());
	}

	@Test
	public void testCertMessageAbortsInWrongState() throws Exception {
		State[] wrong = {START, AWAIT_READY, FINISHED, ERROR};
		for (State s : wrong) testCertMessageAbortsInState(s);
	}

	private void testCertMessageAbortsInState(State s) throws Exception {
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, s);
		session = engine.onCertMessage(txn, session, certMessage);
		assertEquals(groupId, session.getGroupId());
		assertEquals(ERROR, session.getState());
	}

	@Test
	public void testCertMessageSendsCertAndStoresRemoteCertInStateAwaitBoth()
			throws Exception {
		BdfDictionary localCertDict = new BdfDictionary();
		BdfDictionary localGroupMeta =
				BdfDictionary.of(new BdfEntry(GROUP_KEY_CERT, localCertDict));
		BdfDictionary remoteCertDict = new BdfDictionary();
		BdfDictionary contactGroupMeta =
				BdfDictionary.of(new BdfEntry(GROUP_KEY_CERT, remoteCertDict));
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, AWAIT_BOTH);

		context.checking(new Expectations() {{
			// Load the local certificate
			oneOf(contactGroupFactory).createLocalGroup(CLIENT_ID);
			will(returnValue(localGroup));
			oneOf(clientHelper).getGroupMetadataAsDictionary(txn,
					localGroup.getId());
			will(returnValue(localGroupMeta));
			// Parse the local certificate
			oneOf(certificateParser).parse(localCertDict);
			will(returnValue(cert));
			// Send a cert message
			oneOf(messageEncoder).encodeCertMessage(groupId, cert);
			will(returnValue(message));
			oneOf(db).addLocalMessage(txn, message, new Metadata(), true);
			// Encode the remote certificate
			oneOf(certificateEncoder).encode(cert);
			will(returnValue(remoteCertDict));
			// Store the remote certificate
			oneOf(clientHelper).mergeGroupMetadata(txn, groupId,
					contactGroupMeta);
		}});

		session = engine.onCertMessage(txn, session, certMessage);

		assertEquals(groupId, session.getGroupId());
		assertEquals(AWAIT_READY, session.getState());
	}

	@Test
	public void testCertMessageStoresRemoteCertInStateAwaitCert()
			throws Exception {
		BdfDictionary remoteCertDict = new BdfDictionary();
		BdfDictionary contactGroupMeta =
				BdfDictionary.of(new BdfEntry(GROUP_KEY_CERT, remoteCertDict));
		Transaction txn = new Transaction(null, false);
		Session session = new Session(groupId, AWAIT_CERT);

		context.checking(new Expectations() {{
			// Encode the remote certificate
			oneOf(certificateEncoder).encode(cert);
			will(returnValue(remoteCertDict));
			// Store the remote certificate
			oneOf(clientHelper).mergeGroupMetadata(txn, groupId,
					contactGroupMeta);
		}});

		session = engine.onCertMessage(txn, session, certMessage);

		assertEquals(groupId, session.getGroupId());
		assertEquals(FINISHED, session.getState());
	}
}
