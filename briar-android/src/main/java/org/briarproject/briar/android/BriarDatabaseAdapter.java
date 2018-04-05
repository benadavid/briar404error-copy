package org.briarproject.briar.android;


import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.briarproject.briar.api.messaging.FirebasePrivateMessageHeader;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by Ordinateur on 4/4/2018.
 */

public class BriarDatabaseAdapter extends BriarDatabase{

	protected FirebaseDatabase database;
	protected DatabaseReference dbRef;

	public BriarDatabaseAdapter(String pseudo){

		super(pseudo);

		//FirebasePrivateMessageHeader md = new FirebasePrivateMessageHeader();

		database = FirebaseDatabase.getInstance();
		dbRef = database.getReference(pseudo);

		/* Read from the database */
		dbRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				// This method is called once with the initial value and again
				// whenever data at this location is updated.
				//HashMap<String, ArrayList<FirebasePrivateMessageHeader>>
				contacts = dataSnapshot.getValue(HashMap.class);
			}

			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
				Log.w(TAG, "Failed to read value.", error.toException());
			}
		});
	}

	public void addContact(String contact){

		if(!contacts.containsKey(contact)){
			//We create an empty message list
			ArrayList<FirebasePrivateMessageHeader> emptyMessageList
					= new ArrayList<>(5);

			this.contacts.put(contact, emptyMessageList);

			commitToDatabase();
		}
	}

	public boolean contactExists(String contact){
		if(contacts.containsKey(contact)){
			return true;
		}
		return false;
	}

	public ArrayList<FirebasePrivateMessageHeader> getConversation(String contact){

			return contacts.get(contact);
	}


	public void addMessage(String contact, FirebasePrivateMessageHeader message){

		if(!contactExists(contact)){
			addContact(contact);
		}

		ArrayList<FirebasePrivateMessageHeader> messages = this.contacts.get(contact);
		messages.add(message);

		//We update the contact with the new message list
		contacts.put(contact,messages);

		commitToDatabase();

		//We update the other user with the new message list
		DatabaseReference otherUser = database.getReference(contact);

		otherUser.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				//We change the data for the other user, i.e. the receiver
				HashMap<String, ArrayList<FirebasePrivateMessageHeader>>
						otherUserContacts = dataSnapshot.getValue(HashMap.class);

				//We get the conversation with the actual user
				ArrayList<FirebasePrivateMessageHeader> currentConversation =
						otherUserContacts.get(pseudo);

				//We invert the sender and receiver for the other user, but the
				// message is still the same

				//We invert the sender and the receiver for the receiver version of the message
				String sender = message.getReceiver();
				String receiver = message.getSender();

				FirebasePrivateMessageHeader otherUserMessage =

						new FirebasePrivateMessageHeader(sender,receiver,
								message.getTimestamp(),
						message.isLocal(),false,true,false);

				currentConversation.add(otherUserMessage);

				otherUser.setValue(otherUserContacts);

				//String value = dataSnapshot.getValue(String.class);
				//Log.d(TAG, "Value is: " + value);
			}

			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
				//Log.w(TAG, "Failed to read value.", error.toException());
			}
		});

	}

	private void commitToDatabase(){
		dbRef.setValue(contacts);
	}

}
