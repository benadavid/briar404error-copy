package org.briarproject.bramble.api.contact.event;

import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.Immutable;

/**
 * Created by Gibran on 2018-03-04.
 * An event that is broadcast when a contact is marked muted or un-muted.
 */
@Immutable
@NotNullByDefault
public class ContactMutedEvent extends Event {

	private final ContactId contactId;
	private final boolean muted;

	public ContactMutedEvent(ContactId contactId, boolean muted) {
		this.contactId = contactId;
		this.muted = muted;
	}

	public ContactId getContactId() {
		return contactId;
	}

	public boolean isMuted() {
		return muted;
	}
}
