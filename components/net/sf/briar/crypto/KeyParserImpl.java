package net.sf.briar.crypto;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import net.sf.briar.api.crypto.KeyParser;

public class KeyParserImpl implements KeyParser {

	private final KeyFactory keyFactory;

	KeyParserImpl(String algorithm) throws NoSuchAlgorithmException {
		keyFactory = KeyFactory.getInstance(algorithm);
	}

	public PublicKey parsePublicKey(byte[] encodedKey)
	throws InvalidKeySpecException {
		EncodedKeySpec e = new X509EncodedKeySpec(encodedKey);
		return keyFactory.generatePublic(e);
	}

}
