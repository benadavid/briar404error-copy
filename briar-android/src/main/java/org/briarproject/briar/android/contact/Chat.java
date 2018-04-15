package org.briarproject.briar.android.contact;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.thoughtcrime.securesms.components.util.ListenableFutureTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static java.util.logging.Level.WARNING;
import static org.briarproject.briar.android.navdrawer.NavDrawerActivity.nickname1;

import org.briarproject.bramble.api.db.DatabaseConfig;
import android.location.LocationManager;


public class Chat extends BriarActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    FirebaseDatabase database;
    DatabaseReference databaseRef1, databaseRef2;
    public static final String CONTACT_ID = "briar.CONTACT_ID";
    public static String friend_name;

    LocationManager mLocationManager;

    private volatile ContactId contactId;
    @Nullable
    private volatile String contactName;
    @Inject
    volatile ContactManager contactManager;
    @Inject
    protected DatabaseConfig databaseConfig;

    private final ListenableFutureTask<String> contactNameTask =
            new ListenableFutureTask<>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    Contact c = contactManager.getContact(contactId);
                    contactName = c.getAuthor().getName();
                    friend_name=contactName;
                    return c.getAuthor().getName();
                }
            });
    private final AtomicBoolean contactNameTaskStarted =
            new AtomicBoolean(false);

    @Override
    public void injectActivity(ActivityComponent component) {
        component.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Intent i = getIntent();
        int id = i.getIntExtra(CONTACT_ID, -1);
//		if (id == -1) throw new IllegalStateException();
        contactId = new ContactId(id);

        //get friend's author name
        runOnDbThread(() -> {
            try {
                long now = System.currentTimeMillis();
                Contact contact = contactManager.getContact(contactId);
                contactName = contact.getAuthor().getName();

                friend_name=contactName;

            } catch (DbException e) {

            }
        });

        //accessing the firebase database
        database=FirebaseDatabase.getInstance();
        //creates a database references
        databaseRef1=database.getReference().child(nickname1+"_"+friend_name);
        databaseRef2=database.getReference().child(friend_name+"_"+nickname1);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", nickname1);
                    databaseRef1.push().setValue(map);
                    databaseRef2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        databaseRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(message.equals(databaseConfig.getLocationWord())) {
                    Location currentLocation = getLastBestLocation();
                    String currentLocationMessage = "My current location is: /n" +
                            "Latitude " + currentLocation.getLatitude() + "/n" +
                            "Longitude " + currentLocation.getLongitude() + "/n" +
                            "at time " + currentLocation.getTime() + "/n" +
                            "using location provider " + currentLocation.getProvider();

                    Map<String, String> locationMessageMap = new HashMap<String, String>();
                    map.put("message", currentLocationMessage);
                    map.put("user", nickname1);

                    databaseRef1.push().setValue(locationMessageMap);
                    databaseRef2.push().setValue(locationMessageMap);
                }

                if(userName.equals(nickname1)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    addMessageBox(friend_name + ":-\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    private Location getLastBestLocation() {
        @SuppressLint("MissingPermission") Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        @SuppressLint("MissingPermission") Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
}