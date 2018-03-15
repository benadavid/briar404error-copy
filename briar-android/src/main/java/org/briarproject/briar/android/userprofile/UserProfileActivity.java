package org.briarproject.briar.android.userprofile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.briarproject.briar.R;

import java.io.File;
import java.io.FileInputStream;

public class UserProfileActivity extends AppCompatActivity {

	private StorageReference mStorageRef; //Firebase storage object
	private ImageButton avatarButton;
	private static final String LOG_TAG = UserProfileActivity.class.getSimpleName();
	public final static String APP_PATH_SD_CARD="/DesiredSubfolderName/";
	public final static String APP_THUMBNAIL_PATH_SD_CARD="thumbnails";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		//Firebase storage object initalized
		mStorageRef = FirebaseStorage.getInstance().getReference();

		avatarButton = findViewById(R.id.AvatarButton);
		avatarButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			startActivity(new Intent (UserProfileActivity.this,
					AvatarActivity.class));
			}
		});

		// load pic from internal memory and format it into a circle
		if(fileExistance("desiredFilename.png")) {
			// Make the image into a circle
			// In saveImageToInternalStorage() we named the picture desiredFilename
			RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory
					.create(getResources(), getThumbnail("desiredFilename.png"));
			roundedBitmapDrawable.setCircular(true);
			avatarButton.setImageDrawable(roundedBitmapDrawable);
		}
	}

	public void onStart() {
		super.onStart();

		//load pic from internal memory and format it into a circle
		if(fileExistance("desiredFilename.png")) {
			// Make the image into a circle
			// In saveImageToInternalStorage() we named the picture desiredFilename
			RoundedBitmapDrawable roundedBitmapDrawable =
					RoundedBitmapDrawableFactory.create(getResources(),
							getThumbnail("desiredFilename.png"));
			roundedBitmapDrawable.setCircular(true);
			avatarButton.setImageDrawable(roundedBitmapDrawable);
		}


	}

	//check if the picture exists in the internal memory before trying to load
	// the picture to prevent crash
	public boolean fileExistance(String fname){
		File file = getBaseContext().getFileStreamPath(fname);
		return file.exists();
	}

	public Bitmap getThumbnail(String filename) {

		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
		Bitmap thumbnail = null;


		// If no file on external storage, look in internal storage
		if (thumbnail == null) {
			try {
				File filePath = getFileStreamPath(filename);
				FileInputStream fi = new FileInputStream(filePath);
				thumbnail = BitmapFactory.decodeStream(fi);
			} catch (Exception ex) {
				Log.e(LOG_TAG + "getThumbnail() failed", ex.getMessage());
			}
		}
		return thumbnail;
	}

}
