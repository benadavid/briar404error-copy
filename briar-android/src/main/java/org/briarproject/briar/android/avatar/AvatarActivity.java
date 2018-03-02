package org.briarproject.briar.android.avatar;

/**
 * Created by David on 2018-02-14.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import javax.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.BitmapDrawableResource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DatabaseConfig;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.NoSuchContactException;
import org.briarproject.bramble.api.identity.AuthorId;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.navdrawer.NavDrawerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.inject.Inject;

import static java.util.logging.Level.WARNING;
import static org.briarproject.briar.android.navdrawer.NavDrawerActivity.APP_PATH_SD_CARD;
import static org.briarproject.briar.android.navdrawer.NavDrawerActivity.APP_THUMBNAIL_PATH_SD_CARD;

public class AvatarActivity extends BriarActivity {
	private static final int SELECT_PHOTO = 100;
	Uri selectedImage;
	FirebaseStorage storage;
	StorageReference storageRef,imageRef;
	ProgressDialog progressDialog;
	UploadTask uploadTask;
	ImageView imageView;
	private TextInputEditText authorNameInput;
	protected DatabaseConfig databaseConfig;
	private static LocalAuthor author;
	private static String nickname;
	private static final String LOG_TAG = AvatarActivity.class.getSimpleName();


	@Inject
	volatile IdentityManager identityManager;

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avatar_main);
		imageView = (ImageView) findViewById(R.id.imageView2);
		//accessing the firebase storage
		storage = FirebaseStorage.getInstance();
		//creates a storage reference
		storageRef = storage.getReference();

		//******** load pic from internal memory and format it into a circle
		if(fileExistance("desiredFilename.png")) {
			// In saveImageToInternalStorage() we named the picture desiredFilename
			RoundedBitmapDrawable bmp = RoundedBitmapDrawableFactory.create(getResources(), getThumbnail("desiredFilename.png"));

			imageView.setImageDrawable(bmp);
		}
		//********* end of load pic
	}
	public void selectImage(View view) {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		switch (requestCode) {
			case SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					Toast.makeText(AvatarActivity.this,"Image selected, click on upload button",Toast.LENGTH_SHORT).show();
					selectedImage = imageReturnedIntent.getData();

					ParcelFileDescriptor fd;
					try {
						fd = getContentResolver().openFileDescriptor(imageReturnedIntent.getData(), "r");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						return;
					}

					// Get the image file location
					Bitmap bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());

					//save to internal storage
					saveImageToInternalStorage(bmp);

					Drawable d = new BitmapDrawable(getResources(), bmp);

					imageView.setImageDrawable(d);
				}
		}
	}
	public void uploadImage(View view) throws DbException {
		//retrieve local user nickname
		runOnDbThread(() -> {

			// Load the local pseudonym
			try {
				author = identityManager.getLocalAuthor();
				nickname = author.getName();
				;
			} catch (DbException e) {
				return;
			}

		});
		//upload image in firebase under that nickname
		uploadImage2();
	}

	public static String getNickname() {
		return nickname;
	}

	public void uploadImage2(){
			//create reference to images folder and assing a name to the file that will be uploaded
			imageRef = storageRef.child(getNickname() + "/pic");
			//creating and showing progress dialog
			progressDialog = new ProgressDialog(this);
			progressDialog.setMax(100);
			progressDialog.setMessage("Uploading...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.show();
			progressDialog.setCancelable(false);
			//starting upload
			uploadTask = imageRef.putFile(selectedImage);
			// Observe state change events such as progress, pause, and resume
			uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
					double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
					//sets and increments value of progressbar
					progressDialog.incrementProgressBy((int) progress);
				}
			});

			// Register observers to listen for when the download is done or if it fails
			uploadTask.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					// Handle unsuccessful uploads
					Toast.makeText(AvatarActivity.this,"Error in uploading!",Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
				}
			}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					// taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
					Uri downloadUrl = taskSnapshot.getDownloadUrl();
					Toast.makeText(AvatarActivity.this,"Avatar Set",Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					//showing the uploaded image in ImageView using the download url
					Picasso.with(AvatarActivity.this).load(downloadUrl).into(imageView);
				}
			});
	}

	//************
	//Save picture to internal memory
	//************
	public boolean saveImageToInternalStorage(Bitmap image) {

		try {
			// Use the compress method on the Bitmap object to write image to
			// the OutputStream
			//user may name the picture file in any way he wishes. Default is "desiredFilename"
			FileOutputStream
					fos = openFileOutput("desiredFilename.png", Context.MODE_PRIVATE);

			// Writing the bitmap to the output stream
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();

			return true;
		} catch (Exception e) {
			Log.e("saveToInternalStorage()", e.getMessage());
			return false;
		}
	}
	//************
	//end of save picture to internal memory
	//************

	//************
	//Load picture from internal memory
	//************
	public boolean isSdReadable() {

		boolean mExternalStorageAvailable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = true;
			Log.i("isSdReadable", "External storage card is readable.");
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			Log.i("isSdReadable", "External storage card is readable.");
			mExternalStorageAvailable = true;
		} else {
			// all we need to know is we can neither read nor write
			mExternalStorageAvailable = false;
		}

		return mExternalStorageAvailable;
	}

	public Bitmap getThumbnail(String filename) {

		String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
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
	//******
	// end of load picture from internal memory
	//******


	//******
	//check if the picture exists in the internal memory before trying to load the picture to prevent crash
	//******
	public boolean fileExistance(String fname){
		File file = getBaseContext().getFileStreamPath(fname);
		return file.exists();
	}
	//end of file existance code
}