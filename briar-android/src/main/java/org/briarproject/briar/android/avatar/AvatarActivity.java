package org.briarproject.briar.android.avatar;

/**
 * Created by David on 2018-02-14.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import javax.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
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

import java.io.File;

import javax.inject.Inject;

import static java.util.logging.Level.WARNING;

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
					Toast.makeText(AvatarActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
					progressDialog.dismiss();
					//showing the uploaded image in ImageView using the download url
					Picasso.with(AvatarActivity.this).load(downloadUrl).into(imageView);
				}
			});
	}
}