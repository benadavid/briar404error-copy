package org.briarproject.briar.android;

import org.briarproject.briar.api.messaging.FirebasePrivateMessageHeader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ordinateur on 4/4/2018.
 */

abstract class BriarDatabase {

	protected String pseudo;
	protected HashMap<String, ArrayList<FirebasePrivateMessageHeader>> contacts;

	public BriarDatabase(String pseudo){
		this.pseudo = pseudo;
	}

}
