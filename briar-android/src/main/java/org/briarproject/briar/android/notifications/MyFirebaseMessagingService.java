package org.briarproject.briar.android.notifications;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by David on 2018-03-28.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	public MyFirebaseMessagingService() {
	}
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);

		Log.d("firebase", "From: " + remoteMessage.getFrom());
		Log.d("firebase", "Notification Message Body: " + remoteMessage.getNotification().getBody());

	}
}