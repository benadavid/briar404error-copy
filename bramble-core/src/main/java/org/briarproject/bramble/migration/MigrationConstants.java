package org.briarproject.bramble.migration;

interface MigrationConstants {

	/**
	 * Group metadata key for the migration certificate. For the local group
	 * this is the local cert, for contact groups it's the remote cert.
	 */
	String GROUP_KEY_CERT = "cert";

	/**
	 * Group metadata key for the contact's author ID.
	 */
	String GROUP_KEY_AUTHOR_ID = "authorId";

	/**
	 * Group metadata key for the session state.
	 */
	String GROUP_KEY_STATE = "state";

	/**
	 * Message metadata key for the message type.
	 */
	String MSG_KEY_MSG_TYPE = "type";

	/**
	 * Dictionary keys for storing certificates in metadata.
	 */
	String CERT_KEY_NAME = "name";
	String CERT_KEY_OLD_PUBLIC_KEY = "oldPub";
	String CERT_KEY_NEW_PUBLIC_KEY = "newPub";
	String CERT_KEY_OLD_SIGNATURE = "oldSig";
	String CERT_KEY_NEW_SIGNATURE = "newSig";

}
