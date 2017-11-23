package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.crypto.KeyParser;
import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.test.BrambleMockTestCase;
import org.jmock.Expectations;
import org.junit.Test;

import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_PUBLIC_KEY_LENGTH;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_SIGNATURE_LENGTH;
import static org.briarproject.bramble.test.TestUtils.getRandomBytes;
import static org.briarproject.bramble.util.StringUtils.getRandomString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CertificateEncoderParserIntegrationTest extends
		BrambleMockTestCase {

	private final CryptoComponent crypto = context.mock(CryptoComponent.class);
	private final KeyParser oldParser =
			context.mock(KeyParser.class, "oldParser");
	private final KeyParser newParser =
			context.mock(KeyParser.class, "newParser");
	private final PublicKey oldPublicKey =
			context.mock(PublicKey.class, "oldPublicKey");
	private final PublicKey newPublicKey =
			context.mock(PublicKey.class, "newPublicKey");

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
	public void testEncodingAndParsing() throws Exception {
		context.checking(new Expectations() {{
			// Construct parser
			oneOf(crypto).getSignatureKeyParser();
			will(returnValue(oldParser));
			oneOf(crypto).getEdKeyParser();
			will(returnValue(newParser));
			// Encode
			oneOf(oldPublicKey).getEncoded();
			will(returnValue(oldPublicKeyBytes));
			oneOf(newPublicKey).getEncoded();
			will(returnValue(newPublicKeyBytes));
			// Parse
			oneOf(oldParser).parsePublicKey(oldPublicKeyBytes);
			will(returnValue(oldPublicKey));
			oneOf(newParser).parsePublicKey(newPublicKeyBytes);
			will(returnValue(newPublicKey));
		}});

		CertificateEncoder encoder = new CertificateEncoderImpl();
		CertificateParser parser = new CertificateParserImpl(crypto);

		Certificate result = parser.parse(encoder.encode(cert));

		assertEquals(cert.getName(), result.getName());
		assertEquals(cert.getOldPublicKey(), result.getOldPublicKey());
		assertEquals(cert.getNewPublicKey(), result.getNewPublicKey());
		assertArrayEquals(cert.getOldSignature(), result.getOldSignature());
		assertArrayEquals(cert.getNewSignature(), result.getNewSignature());
	}
}
