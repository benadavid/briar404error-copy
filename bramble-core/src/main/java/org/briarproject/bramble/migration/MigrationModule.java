package org.briarproject.bramble.migration;

import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.crypto.CryptoComponent;
import org.briarproject.bramble.api.lifecycle.LifecycleManager;
import org.briarproject.bramble.api.migration.MigrationManager;
import org.briarproject.bramble.api.sync.ValidationManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.briarproject.bramble.api.migration.MigrationManager.CLIENT_ID;

@Module
public class MigrationModule {

	public static class EagerSingletons {
		@Inject
		MigrationManager migrationManager;
		@Inject
		MigrationValidator migrationValidator;
	}

	@Provides
	@Singleton
	MigrationManager provideMigrationManager(
			MigrationManagerImpl migrationManager,
			LifecycleManager lifecycleManager, ContactManager contactManager,
			ValidationManager validationManager) {
		lifecycleManager.registerClient(migrationManager);
		contactManager.registerAddContactHook(migrationManager);
		contactManager.registerRemoveContactHook(migrationManager);
		validationManager.registerIncomingMessageHook(CLIENT_ID,
				migrationManager);
		return migrationManager;
	}

	@Provides
	@Singleton
	MigrationValidator provideMigrationValidator(MessageParser messageParser,
			ClientHelper clientHelper, CryptoComponent crypto,
			ValidationManager validationManager) {
		MigrationValidator migrationValidator =
				new MigrationValidator(messageParser, clientHelper, crypto);
		validationManager.registerMessageValidator(CLIENT_ID,
				migrationValidator);
		return migrationValidator;
	}

	@Provides
	CertificateEncoder provideCertificateEncoder(
			CertificateEncoderImpl certificateEncoder) {
		return certificateEncoder;
	}

	@Provides
	CertificateFactory provideCertificateFactory(
			CertificateFactoryImpl certificateFactory) {
		return certificateFactory;
	}

	@Provides
	CertificateParser provideCertificateParser(
			CertificateParserImpl certificateParser) {
		return certificateParser;
	}

	@Provides
	MessageEncoder provideMessageEncoder(MessageEncoderImpl messageEncoder) {
		return messageEncoder;
	}

	@Provides
	MessageParser provideMessageParser(MessageParserImpl messageParser) {
		return messageParser;
	}

	@Provides
	ProtocolEngine provideProtocolEngine(ProtocolEngineImpl protocolEngine) {
		return protocolEngine;
	}
}
