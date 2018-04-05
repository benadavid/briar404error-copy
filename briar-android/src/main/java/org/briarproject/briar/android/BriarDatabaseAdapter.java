package org.briarproject.briar.android;


import com.google.firebase.database.FirebaseDatabase;

import org.briarproject.briar.api.messaging.FirebasePrivateMessageHeader;

/**
 * Created by Ordinateur on 4/4/2018.
 */

public class BriarDatabaseAdapter extends BriarDatabase{

	FirebaseDatabase database;


	public BriarDatabaseAdapter(String pseudo){

		super(pseudo);

		//FirebasePrivateMessageHeader md = new FirebasePrivateMessageHeader();

		loadContacts();
	}

	public addContact(String contact){
		this.contacts[contact] = contact;
	}

	private void loadContacts(){

		//We fill the contact HashMap with the messages
	}

}
