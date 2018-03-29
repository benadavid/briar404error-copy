package org.briarproject.briar.android.notifications;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by David on 2018-03-28.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

	@Override
	public void onTokenRefresh() {
		// Get updated InstanceID token.
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.d("firebase", "Refreshed token: " + refreshedToken);

// We will Send this refreshedToken to our app server, so app
		// server can save it
		// and can later use it for sending notification to app.

// sendRegistrationToServer(refreshedToken);
	}
}
