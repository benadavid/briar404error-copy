package org.briarproject.bramble.api.migration;

import org.briarproject.bramble.api.crypto.PublicKey;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.Immutable;

@Immutable
@NotNullByDefault
public class Certificate {

	/**
	 * Label for signing migration certificates.
	 */
	public static final String LABEL =
			"org.briarproject.bramble.migration.CERTIFICATE";

	private final String name;
	private final PublicKey oldPublicKey, newPublicKey;
	private final byte[] oldSignature, newSignature;

	public Certificate(String name, PublicKey oldPublicKey,
			PublicKey newPublicKey, byte[] oldSignature, byte[] newSignature) {
		this.name = name;
		this.oldPublicKey = oldPublicKey;
		this.newPublicKey = newPublicKey;
		this.oldSignature = oldSignature;
		this.newSignature = newSignature;
	}

	public String getName() {
		return name;
	}

	public PublicKey getOldPublicKey() {
		return oldPublicKey;
	}

	public PublicKey getNewPublicKey() {
		return newPublicKey;
	}

	public byte[] getOldSignature() {
		return oldSignature;
	}

	public byte[] getNewSignature() {
		return newSignature;
	}
}
