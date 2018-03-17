package org.briarproject.bramble.api.sync;

import org.briarproject.bramble.api.UniqueId;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Type-safe wrapper for a byte array that uniquely identifies a
 * {@link Message}.
 */
@ThreadSafe
@NotNullByDefault
public class MessageId extends UniqueId {

	/**
	 * Label for hashing messages to calculate their identifiers.
	 */
	public static final String LABEL = "org.briarproject.bramble/MESSAGE_ID";
	private boolean bold;
	private boolean italic;

	public MessageId(byte[] id, boolean bold, boolean italic) {
		super(id);
		this.bold = bold;
		this.italic = italic;
	}

	public MessageId(byte[] id) { super(id); }


	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof MessageId && super.equals(o);
	}
}
