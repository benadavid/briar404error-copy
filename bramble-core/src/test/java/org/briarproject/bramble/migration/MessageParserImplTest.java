package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.KeyParser;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.test.BrambleMockTestCase;
import org.jmock.Expectations;
import org.junit.Test;

import static org.briarproject.bramble.migration.MessageType.CERT;
import static org.briarproject.bramble.test.TestUtils.getRandomBytes;
import static org.briarproject.bramble.test.TestUtils.getRandomId;

public class MessageParserImplTest extends BrambleMockTestCase {

	private final ClientHelper clientHelper = context.mock(ClientHelper.class);
	private final CryptoComponent crypto = context.mock(CryptoComponent.class);
	private final KeyParser oldParser = context.mock(KeyParser.class, "old");
	private final KeyParser newParser = context.mock(KeyParser.class, "new");

	private final Message message = new Message(new MessageId(getRandomId()),
			new GroupId(getRandomId()), System.currentTimeMillis(),
			getRandomBytes(123));

	private final MessageParser messageParser;

	public MessageParserImplTest() {
		context.checking(new Expectations() {{
			oneOf(crypto).getSignatureKeyParser();
			will(returnValue(oldParser));
			oneOf(crypto).getEdKeyParser();
			will(returnValue(newParser));
		}});
		messageParser = new MessageParserImpl(clientHelper, crypto);
	}

	@Test(expected = FormatException.class)
	public void testRejectsEmptyMessage() throws Exception {
		BdfList body = new BdfList();
		context.checking(new Expectations() {{
			oneOf(clientHelper).toList(message);
			will(returnValue(body));
		}});
		messageParser.parse(message);
	}

	@Test(expected = FormatException.class)
	public void testRejectsUnknownMessageType() throws Exception {
		BdfList body = BdfList.of(CERT.getValue() + 1);
		context.checking(new Expectations() {{
			oneOf(clientHelper).toList(message);
			will(returnValue(body));
		}});
		messageParser.parse(message);
	}
}
