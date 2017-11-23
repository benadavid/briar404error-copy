package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.InvalidMessageException;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageContext;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.test.BrambleMockTestCase;
import org.jmock.Expectations;
import org.junit.Test;

import java.security.GeneralSecurityException;

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_PUBLIC_KEY_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_SIGNATURE_LENGTH;
import static org.briarproject.bramble.api.migration.Certificate.LABEL;
import static org.briarproject.bramble.api.migration.MigrationManager.CLIENT_ID;
import static org.briarproject.bramble.api.sync.SyncConstants.MAX_GROUP_DESCRIPTOR_LENGTH;
import static org.briarproject.bramble.api.sync.SyncConstants.MAX_MESSAGE_LENGTH;
import static org.briarproject.bramble.test.TestUtils.getRandomBytes;
import static org.briarproject.bramble.test.TestUtils.getRandomId;
import static org.briarproject.bramble.util.StringUtils.getRandomString;
import static org.junit.Assert.assertTrue;

public class MigrationValidatorTest extends BrambleMockTestCase {

	private final MessageParser messageParser =
			context.mock(MessageParser.class);
	private final ClientHelper clientHelper = context.mock(ClientHelper.class);
	private final CryptoComponent crypto = context.mock(CryptoComponent.class);
	private final PublicKey oldPublicKey = context.mock(PublicKey.class, "old");
	private final PublicKey newPublicKey = context.mock(PublicKey.class, "new");

	private final MessageId messageId = new MessageId(getRandomId());
	private final GroupId groupId = new GroupId(getRandomId());
	private final long timestamp = System.currentTimeMillis();
	private final byte[] raw = getRandomBytes(MAX_MESSAGE_LENGTH);
	private final Message message =
			new Message(messageId, groupId, timestamp, raw);
	private final Group group = new Group(new GroupId(getRandomId()),
			CLIENT_ID, getRandomBytes(MAX_GROUP_DESCRIPTOR_LENGTH));
	private final ReadyMessage readyMessage = new ReadyMessage(groupId);
	private final String name = getRandomString(MAX_AUTHOR_NAME_LENGTH);
	private final byte[] oldPublicKeyBytes =
			getRandomBytes(MAX_PUBLIC_KEY_LENGTH);
	private final byte[] newPublicKeyBytes =
			getRandomBytes(MAX_PUBLIC_KEY_LENGTH);
	private final byte[] oldSignature = getRandomBytes(MAX_SIGNATURE_LENGTH);
	private final byte[] newSignature = getRandomBytes(MAX_SIGNATURE_LENGTH);
	private final Certificate cert = new Certificate(name, oldPublicKey,
			newPublicKey, oldSignature, newSignature);
	private final CertMessage certMessage = new CertMessage(groupId, cert);
	private BdfList signed =
			BdfList.of(name, oldPublicKeyBytes, newPublicKeyBytes);
	private byte[] signedBytes = getRandomBytes(123);

	private final MigrationValidator validator =
			new MigrationValidator(messageParser, clientHelper, crypto);

	@Test(expected = InvalidMessageException.class)
	public void testRejectsMessageIfParsingFails() throws Exception {
		context.checking(new Expectations() {{
			oneOf(messageParser).parse(message);
			will(throwException(new FormatException()));
		}});

		validator.validateMessage(message, group);
	}

	@Test
	public void testAcceptsReadyMessage() throws Exception {
		context.checking(new Expectations() {{
			oneOf(messageParser).parse(message);
			will(returnValue(readyMessage));
		}});

		MessageContext context = validator.validateMessage(message, group);

		assertTrue(context.getMetadata().isEmpty());
		assertTrue(context.getDependencies().isEmpty());
	}

	@Test(expected = InvalidMessageException.class)
	public void testRejectsCertMessageWhenOldSignatureThrowsException()
			throws Exception {
		expectSerialiseSignedData();
		context.checking(new Expectations() {{
			// Verifying the old signature throws an exception
			oneOf(crypto).verify(LABEL, signedBytes, oldPublicKeyBytes,
					oldSignature);
			will(throwException(new GeneralSecurityException()));
		}});

		validator.validateMessage(message, group);
	}

	@Test(expected = InvalidMessageException.class)
	public void testRejectsCertMessageWhenOldSignatureDoesNotVerify()
			throws Exception {
		expectSerialiseSignedData();
		context.checking(new Expectations() {{
			// Verifying the old signature fails
			oneOf(crypto).verify(LABEL, signedBytes, oldPublicKeyBytes,
					oldSignature);
			will(returnValue(false));
		}});

		validator.validateMessage(message, group);
	}

	@Test(expected = InvalidMessageException.class)
	public void testRejectsCertMessageWhenNewSignatureThrowsException()
			throws Exception {
		expectSerialiseSignedData();
		context.checking(new Expectations() {{
			// Verifying the old signature succeeds
			oneOf(crypto).verify(LABEL, signedBytes, oldPublicKeyBytes,
					oldSignature);
			will(returnValue(true));
			// Verifying the new signature throws an exception
			oneOf(crypto).verifyEd(LABEL, signedBytes, newPublicKeyBytes,
					newSignature);
			will(throwException(new GeneralSecurityException()));
		}});

		validator.validateMessage(message, group);
	}

	@Test(expected = InvalidMessageException.class)
	public void testRejectsCertMessageWhenNewSignatureDoesNotVerify()
			throws Exception {
		expectSerialiseSignedData();
		context.checking(new Expectations() {{
			// Verifying the old signature succeeds
			oneOf(crypto).verify(LABEL, signedBytes, oldPublicKeyBytes,
					oldSignature);
			will(returnValue(true));
			// Verifying the new signature fails
			oneOf(crypto).verifyEd(LABEL, signedBytes, newPublicKeyBytes,
					newSignature);
			will(returnValue(false));
		}});

		validator.validateMessage(message, group);
	}

	@Test
	public void testAcceptsCertMessage() throws Exception {
		expectSerialiseSignedData();
		context.checking(new Expectations() {{
			// Verifying both signatures succeeds
			oneOf(crypto).verify(LABEL, signedBytes, oldPublicKeyBytes,
					oldSignature);
			will(returnValue(true));
			oneOf(crypto).verifyEd(LABEL, signedBytes, newPublicKeyBytes,
					newSignature);
			will(returnValue(true));
		}});

		MessageContext context = validator.validateMessage(message, group);

		assertTrue(context.getMetadata().isEmpty());
		assertTrue(context.getDependencies().isEmpty());
	}

	private void expectSerialiseSignedData() throws Exception {
		context.checking(new Expectations() {{
			// Serialise the signed data
			oneOf(messageParser).parse(message);
			will(returnValue(certMessage));
			oneOf(oldPublicKey).getEncoded();
			will(returnValue(oldPublicKeyBytes));
			oneOf(newPublicKey).getEncoded();
			will(returnValue(newPublicKeyBytes));
			oneOf(clientHelper).toByteArray(signed);
			will(returnValue(signedBytes));
		}});
	}
}
