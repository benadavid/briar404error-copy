package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.KeyParser;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.system.Clock;
import org.briarproject.bramble.test.BrambleMockTestCase;
import org.jmock.Expectations;
import org.junit.Test;

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_PUBLIC_KEY_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_SIGNATURE_LENGTH;
import static org.briarproject.bramble.api.sync.SyncConstants.MAX_MESSAGE_LENGTH;
import static org.briarproject.bramble.migration.MessageType.CERT;
import static org.briarproject.bramble.migration.MessageType.READY;
import static org.briarproject.bramble.test.TestUtils.getRandomBytes;
import static org.briarproject.bramble.test.TestUtils.getRandomId;
import static org.briarproject.bramble.util.StringUtils.getRandomString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class MessageEncoderParserIntegrationTest extends BrambleMockTestCase {

	private final ClientHelper clientHelper = context.mock(ClientHelper.class);
	private final Clock clock = context.mock(Clock.class);
	private final CryptoComponent crypto = context.mock(CryptoComponent.class);
	private final KeyParser oldParser =
			context.mock(KeyParser.class, "oldParser");
	private final KeyParser newParser =
			context.mock(KeyParser.class, "newParser");
	private final PublicKey oldPublicKey =
			context.mock(PublicKey.class, "oldPublicKey");
	private final PublicKey newPublicKey =
			context.mock(PublicKey.class, "newPublicKey");

	private final GroupId groupId = new GroupId(getRandomId());
	private final MessageId messageId = new MessageId(getRandomId());
	private final long timestamp = System.currentTimeMillis();
	private final byte[] raw = getRandomBytes(MAX_MESSAGE_LENGTH);
	private final Message message =
			new Message(messageId, groupId, timestamp, raw);
	private final String name = getRandomString(MAX_AUTHOR_NAME_LENGTH);
	private final byte[] oldPublicKeyBytes =
			getRandomBytes(MAX_PUBLIC_KEY_LENGTH);
	private final byte[] newPublicKeyBytes =
			getRandomBytes(MAX_PUBLIC_KEY_LENGTH);
	private final byte[] oldSignature = getRandomBytes(MAX_SIGNATURE_LENGTH);
	private final byte[] newSignature = getRandomBytes(MAX_SIGNATURE_LENGTH);
	private final Certificate cert = new Certificate(name, oldPublicKey,
			newPublicKey, oldSignature, newSignature);

	@Test
	public void testEncodeAndParseReadyMessage() throws Exception {
		BdfList body = BdfList.of(READY.getValue());
		context.checking(new Expectations() {{
			// Construct parser
			oneOf(crypto).getSignatureKeyParser();
			will(returnValue(oldParser));
			oneOf(crypto).getEdKeyParser();
			will(returnValue(newParser));
			// Encode
			oneOf(clock).currentTimeMillis();
			will(returnValue(timestamp));
			oneOf(clientHelper).createMessage(groupId, timestamp, body);
			will(returnValue(message));
			// Parse
			oneOf(clientHelper).toList(message);
			will(returnValue(body));
		}});

		MessageEncoder encoder = new MessageEncoderImpl(clientHelper, clock);
		MessageParser parser = new MessageParserImpl(clientHelper, crypto);
		MigrationMessage result =
				parser.parse(encoder.encodeReadyMessage(groupId));

		assertTrue(result instanceof ReadyMessage);
		assertEquals(READY, result.getType());
		assertEquals(groupId, result.getGroupId());
	}

	@Test
	public void testEncodeAndParseCertMessage() throws Exception {
		BdfList body = BdfList.of(
				CERT.getValue(),
				name,
				oldPublicKeyBytes,
				newPublicKeyBytes,
				oldSignature,
				newSignature
		);
		context.checking(new Expectations() {{
			// Construct parser
			oneOf(crypto).getSignatureKeyParser();
			will(returnValue(oldParser));
			oneOf(crypto).getEdKeyParser();
			will(returnValue(newParser));
			// Encode
			oneOf(clock).currentTimeMillis();
			will(returnValue(timestamp));
			oneOf(oldPublicKey).getEncoded();
			will(returnValue(oldPublicKeyBytes));
			oneOf(newPublicKey).getEncoded();
			will(returnValue(newPublicKeyBytes));
			oneOf(clientHelper).createMessage(groupId, timestamp, body);
			will(returnValue(message));
			// Parse
			oneOf(clientHelper).toList(message);
			will(returnValue(body));
			oneOf(oldParser).parsePublicKey(oldPublicKeyBytes);
			will(returnValue(oldPublicKey));
			oneOf(newParser).parsePublicKey(newPublicKeyBytes);
			will(returnValue(newPublicKey));
		}});

		MessageEncoder encoder = new MessageEncoderImpl(clientHelper, clock);
		MessageParser parser = new MessageParserImpl(clientHelper, crypto);
		MigrationMessage result =
				parser.parse(encoder.encodeCertMessage(groupId, cert));

		assertTrue(result instanceof CertMessage);
		assertEquals(CERT, result.getType());
		assertEquals(groupId, result.getGroupId());
		Certificate resultCert = ((CertMessage) result).getCertificate();
		assertEquals(cert.getName(), resultCert.getName());
		assertEquals(cert.getOldPublicKey(), resultCert.getOldPublicKey());
		assertEquals(cert.getNewPublicKey(), resultCert.getNewPublicKey());
		assertArrayEquals(cert.getOldSignature(), resultCert.getOldSignature());
		assertArrayEquals(cert.getNewSignature(), resultCert.getNewSignature());
	}
}
