package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.client.ContactGroupFactory;
import org.briarproject.bramble.api.client.ProtocolStateException;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.db.DatabaseComponent;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Metadata;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.migration.Certificate;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;

import javax.inject.Inject;

import static org.briarproject.bramble.api.migration.MigrationManager.CLIENT_ID;
import static org.briarproject.bramble.migration.MigrationConstants.GROUP_KEY_CERT;
import static org.briarproject.bramble.migration.State.AWAIT_BOTH;
import static org.briarproject.bramble.migration.State.AWAIT_CERT;
import static org.briarproject.bramble.migration.State.AWAIT_READY;
import static org.briarproject.bramble.migration.State.ERROR;
import static org.briarproject.bramble.migration.State.FINISHED;
import static org.briarproject.bramble.migration.State.START;

@NotNullByDefault
class ProtocolEngineImpl implements ProtocolEngine {

	private final DatabaseComponent db;
	private final ClientHelper clientHelper;
	private final ContactGroupFactory contactGroupFactory;
	private final MessageEncoder messageEncoder;
	private final CertificateParser certificateParser;
	private final CertificateEncoder certificateEncoder;

	@Inject
	ProtocolEngineImpl(DatabaseComponent db, ClientHelper clientHelper,
			ContactGroupFactory contactGroupFactory,
			MessageEncoder messageEncoder, CertificateParser certificateParser,
			CertificateEncoder certificateEncoder) {
		this.db = db;
		this.clientHelper = clientHelper;
		this.contactGroupFactory = contactGroupFactory;
		this.messageEncoder = messageEncoder;
		this.certificateParser = certificateParser;
		this.certificateEncoder = certificateEncoder;
	}

	@Override
	public Session onReadyAction(Transaction txn, Session s)
			throws DbException {
		if (s.getState() != START) throw new ProtocolStateException();
		// Send a READY message
		sendReadyMessage(txn, s.getGroupId());
		// Move to the AWAIT_BOTH state
		return new Session(s.getGroupId(), AWAIT_BOTH);
	}

	@Override
	public Session onReadyMessage(Transaction txn, Session s, ReadyMessage m)
			throws DbException, FormatException {
		switch (s.getState()) {
			case AWAIT_BOTH:
				return onRemoteReadyFirst(txn, s);
			case AWAIT_READY:
				return onRemoteReadySecond(s);
			case START:
			case AWAIT_CERT:
			case FINISHED:
			case ERROR:
				return abort(s);
			default:
				throw new AssertionError();
		}
	}

	private Session onRemoteReadyFirst(Transaction txn, Session s)
			throws DbException, FormatException {
		// Send a CERT message
		sendCertMessage(txn, s.getGroupId());
		// Move to the AWAIT_CERT state
		return new Session(s.getGroupId(), AWAIT_CERT);
	}

	private Session onRemoteReadySecond(Session s) {
		// Move to the FINISHED state
		return new Session(s.getGroupId(), AWAIT_CERT);
	}

	@Override
	public Session onCertMessage(Transaction txn, Session s, CertMessage m)
			throws DbException, FormatException {
		switch (s.getState()) {
			case AWAIT_BOTH:
				return onRemoteCertFirst(txn, s, m);
			case AWAIT_CERT:
				return onRemoteCertSecond(txn, s, m);
			case START:
			case AWAIT_READY:
			case FINISHED:
			case ERROR:
				return abort(s);
			default:
				throw new AssertionError();
		}
	}

	private Session onRemoteCertFirst(Transaction txn, Session s, CertMessage m)
			throws DbException, FormatException {
		// Send a CERT message
		sendCertMessage(txn, s.getGroupId());
		// Store the remote cert in the group metadata
		storeRemoteCert(txn, s.getGroupId(), m.getCertificate());
		// Move to the AWAIT_READY state
		return new Session(s.getGroupId(), AWAIT_READY);
	}

	private Session onRemoteCertSecond(Transaction txn, Session s,
			CertMessage m) throws DbException, FormatException {
		// Store the remote cert in the group metadata
		storeRemoteCert(txn, s.getGroupId(), m.getCertificate());
		// Move to the FINISHED state
		return new Session(s.getGroupId(), FINISHED);
	}

	private Session abort(Session s) {
		// If the session has already been aborted, do nothing
		if (s.getState() == ERROR) return s;
		// Move to the ERROR state
		return new Session(s.getGroupId(), ERROR);
	}

	private void sendReadyMessage(Transaction txn, GroupId g)
			throws DbException {
		Message m = messageEncoder.encodeReadyMessage(g);
		db.addLocalMessage(txn, m, new Metadata(), true);
	}

	private void sendCertMessage(Transaction txn, GroupId g)
			throws DbException {
		Message m = messageEncoder.encodeCertMessage(g, getLocalCert(txn));
		db.addLocalMessage(txn, m, new Metadata(), true);
	}

	private Certificate getLocalCert(Transaction txn) throws DbException {
		Group localGroup = contactGroupFactory.createLocalGroup(CLIENT_ID);
		try {
			BdfDictionary meta = clientHelper
					.getGroupMetadataAsDictionary(txn, localGroup.getId());
			return certificateParser.parse(meta.getDictionary(GROUP_KEY_CERT));
		} catch (FormatException e) {
			throw new DbException(e);
		}
	}

	private void storeRemoteCert(Transaction txn, GroupId g, Certificate cert)
			throws DbException {
		BdfDictionary meta = new BdfDictionary();
		meta.put(GROUP_KEY_CERT, certificateEncoder.encode(cert));
		try {
			clientHelper.mergeGroupMetadata(txn, g, meta);
		} catch (FormatException e) {
			throw new AssertionError(e);
		}
	}
}
