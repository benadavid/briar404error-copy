package org.briarproject.briar.android.avatar;

/**
 * Created by David on 2018-02-14.
 */

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.net.Uri;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.briarproject.briar.R;

import java.io.IOException;
import java.util.UUID;

public class AvatarActivity extends AppCompatActivity {
	private Button btnChoose, btnUpload;
	private ImageView imageView;

	private Uri filePath;

	private final int PICK_IMAGE_REQUEST = 71;
	FirebaseStorage storage;
	StorageReference storageReference;
	//Initialize Views
	private void uploadImage() {

		if(filePath != null)
		{
			final ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("Uploading...");
			progressDialog.show();

			StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
			ref.putFile(filePath)
					.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
							progressDialog.dismiss();
							Toast.makeText(AvatarActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							progressDialog.dismiss();
							Toast.makeText(AvatarActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					})
					.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
							double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
									.getTotalByteCount());
							progressDialog.setMessage("Uploaded "+(int)progress+"%");
						}
					});
		}
	}

	private void chooseImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
				&& data != null && data.getData() != null )
		{
			filePath = data.getData();
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
				imageView.setImageBitmap(bitmap);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avatar_main);
		btnChoose = (Button) findViewById(R.id.btnChoose);
		btnUpload = (Button) findViewById(R.id.btnUpload);
		imageView = (ImageView) findViewById(R.id.imgView);
		// Setting the listeners
		btnChoose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseImage();
			}
		});

		btnUpload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadImage();
			}
		});
	}
}

