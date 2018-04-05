package org.briarproject.briar.android.contact;

import javax.annotation.Nullable;

/**
 * Created by Ordinateur on 4/5/2018.
 */

public class FirebaseConversationItem extends ConversationItem {

	public FirebaseConversationItem(){

	}

	public FirebaseConversationItem(@Nullable String body, long time, boolean read){
		this.body = body;
		this.time = time;
		this.read = read;
	}

	@Override
	public boolean isIncoming() {
		return false;
	}

	@Override
	public int getLayout() {
		return 0;
	}
}
