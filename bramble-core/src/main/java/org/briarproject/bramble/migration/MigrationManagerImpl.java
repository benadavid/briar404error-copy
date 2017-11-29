package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.UniqueId;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.client.ContactGroupFactory;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactManager.AddContactHook;
import org.briarproject.bramble.api.contact.ContactManager.RemoveContactHook;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.db.DatabaseComponent;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Metadata;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.identity.AuthorFactory;
import org.briarproject.bramble.api.identity.AuthorId;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.migration.MigrationManager;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.Client;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.InvalidMessageException;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.ValidationManager.IncomingMessageHook;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static org.briarproject.bramble.api.sync.Group.Visibility.SHARED;
import static org.briarproject.bramble.migration.MigrationConstants.GROUP_KEY_AUTHOR_ID;
import static org.briarproject.bramble.migration.MigrationConstants.GROUP_KEY_CERT;
import static org.briarproject.bramble.migration.MigrationConstants.GROUP_KEY_STATE;
import static org.briarproject.bramble.migration.State.START;

@NotNullByDefault
class MigrationManagerImpl implements MigrationManager, Client, AddContactHook,
		RemoveContactHook, IncomingMessageHook {

	private final DatabaseComponent db;
	private final ClientHelper clientHelper;
	private final IdentityManager identityManager;
	private final ContactGroupFactory contactGroupFactory;
	private final CertificateFactory certificateFactory;
	private final CertificateEncoder certificateEncoder;
	private final CertificateParser certificateParser;
	private final ProtocolEngine protocolEngine;
	private final MessageParser messageParser;
	private final AuthorFactory authorFactory;
	private final Group localGroup;

	@Inject
	MigrationManagerImpl(DatabaseComponent db, ClientHelper clientHelper,
			IdentityManager identityManager,
			ContactGroupFactory contactGroupFactory,
			CertificateFactory certificateFactory,
			CertificateEncoder certificateEncoder,
			CertificateParser certificateParser,
			ProtocolEngine protocolEngine, MessageParser messageParser,
			AuthorFactory authorFactory) {
		this.db = db;
		this.clientHelper = clientHelper;
		this.identityManager = identityManager;
		this.contactGroupFactory = contactGroupFactory;
		this.certificateFactory = certificateFactory;
		this.certificateEncoder = certificateEncoder;
		this.certificateParser = certificateParser;
		this.protocolEngine = protocolEngine;
		this.messageParser = messageParser;
		this.authorFactory = authorFactory;
		localGroup = contactGroupFactory.createLocalGroup(CLIENT_ID);
	}

	@Override
	public boolean hasMigrated(Transaction txn, Contact c) throws DbException {
		Group g = getContactGroup(c);
		try {
			BdfDictionary meta =
					clientHelper.getGroupMetadataAsDictionary(txn, g.getId());
			return meta.containsKey(GROUP_KEY_CERT);
		} catch (FormatException e) {
			throw new DbException(e);
		}
	}

	@Nullable
	@Override
	public Certificate getCertificate(Transaction txn, Contact c)
			throws DbException {
		Group g = getContactGroup(c);
		try {
			BdfDictionary meta =
					clientHelper.getGroupMetadataAsDictionary(txn, g.getId());
			if (!meta.containsKey(GROUP_KEY_CERT)) return null;
			return certificateParser.parse(meta.getDictionary(GROUP_KEY_CERT));
		} catch (FormatException e) {
			throw new DbException(e);
		}
	}

	@Override
	public void createLocalState(Transaction txn) throws DbException {
		// Return if we've already created our local state
		if (db.containsGroup(txn, localGroup.getId())) return;
		// Create a local group for storing our migration certificate
		db.addGroup(txn, localGroup);
		// Create our migration certificate
		createAndStoreLocalCert(txn);
		// Set things up for any pre-existing contacts
		for (Contact c : db.getContacts(txn)) addingContact(txn, c);
	}

	@Override
	public void addingContact(Transaction txn, Contact c) throws DbException {
		// Create a group and share it with the contact
		Group g = getContactGroup(c);
		db.addGroup(txn, g);
		db.setGroupVisibility(txn, c.getId(), g.getId(), SHARED);
		// Create and start a session
		Session s = new Session(g.getId(), START);
		s = protocolEngine.onReadyAction(txn, s);
		// Store the session state and the contact's author ID
		BdfDictionary meta = new BdfDictionary();
		meta.put(GROUP_KEY_STATE, s.getState().getValue());
		meta.put(GROUP_KEY_AUTHOR_ID, c.getAuthor().getId().getBytes());
		try {
			clientHelper.mergeGroupMetadata(txn, g.getId(), meta);
		} catch (FormatException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public void removingContact(Transaction txn, Contact c) throws DbException {
		db.removeGroup(txn, getContactGroup(c));
	}

	private Group getContactGroup(Contact c) {
		return contactGroupFactory.createContactGroup(CLIENT_ID, c);
	}

	private void createAndStoreLocalCert(Transaction txn) throws DbException {
		LocalAuthor a = identityManager.getLocalAuthor(txn);
		Certificate cert = certificateFactory.createCertificate(a);
		BdfDictionary meta = new BdfDictionary();
		meta.put(GROUP_KEY_CERT, certificateEncoder.encode(cert));
		try {
			clientHelper.mergeGroupMetadata(txn, localGroup.getId(), meta);
		} catch (FormatException e) {
			throw new AssertionError(e);
		}
	}

	@Override
	public boolean incomingMessage(Transaction txn, Message m, Metadata meta)
			throws DbException, InvalidMessageException {
		try {
			MigrationMessage mm = messageParser.parse(m);
			switch (mm.getType()) {
				case READY:
					incomingReadyMessage(txn, (ReadyMessage) mm);
					return false;
				case CERT:
					incomingCertMessage(txn, (CertMessage) mm);
					return false;
				default:
					throw new AssertionError();
			}
		} catch (FormatException e) {
			throw new InvalidMessageException(e);
		}
	}

	private void incomingReadyMessage(Transaction txn, ReadyMessage m)
			throws DbException, FormatException {
		// Update the session
		BdfDictionary meta =
				clientHelper.getGroupMetadataAsDictionary(txn, m.getGroupId());
		Session s = getSession(m.getGroupId(), meta);
		s = protocolEngine.onReadyMessage(txn, s, m);
		storeSession(txn, s);
	}

	private void incomingCertMessage(Transaction txn, CertMessage m)
			throws DbException, FormatException {
		// Check that the cert was created by the contact
		Certificate cert = m.getCertificate();
		Author certAuthor = authorFactory.createAuthor(cert.getName(),
				cert.getOldPublicKey().getEncoded());
		BdfDictionary meta =
				clientHelper.getGroupMetadataAsDictionary(txn, m.getGroupId());
		byte[] authorIdBytes = meta.getRaw(GROUP_KEY_AUTHOR_ID);
		if (authorIdBytes.length != UniqueId.LENGTH)
			throw new FormatException();
		if (!certAuthor.getId().equals(new AuthorId(authorIdBytes)))
			throw new FormatException();
		// Update the session
		Session s = getSession(m.getGroupId(), meta);
		s = protocolEngine.onCertMessage(txn, s, m);
		storeSession(txn, s);
	}

	private Session getSession(GroupId g, BdfDictionary meta)
			throws FormatException {
		State state = State.fromValue(meta.getLong(GROUP_KEY_STATE).intValue());
		return new Session(g, state);
	}

	private void storeSession(Transaction txn, Session s) throws DbException {
		BdfDictionary meta = new BdfDictionary();
		meta.put(GROUP_KEY_STATE, s.getState().getValue());
		try {
			clientHelper.mergeGroupMetadata(txn, s.getGroupId(), meta);
		} catch (FormatException e) {
			throw new AssertionError(e);
		}
	}
}
