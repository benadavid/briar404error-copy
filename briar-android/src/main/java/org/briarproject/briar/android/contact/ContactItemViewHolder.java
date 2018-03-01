package org.briarproject.briar.android.contact;

import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.contact.BaseContactListAdapter.OnContactClickListener;

import javax.annotation.Nullable;

import im.delight.android.identicons.IdenticonDrawable;

@UiThread
@NotNullByDefault
public class ContactItemViewHolder<I extends ContactItem>
		extends RecyclerView.ViewHolder {

	protected final ViewGroup layout;
	protected final ImageView avatar;
	protected final TextView name;
	@Nullable
	protected final ImageView bulb;
	StorageReference storageRef,imageRef;
	FirebaseStorage storage;
	public static String nickname;

	public ContactItemViewHolder(View v) {
		super(v);

		layout = (ViewGroup) v;
		avatar = v.findViewById(R.id.avatarView);
		name = v.findViewById(R.id.nameView);
		// this can be null as not all layouts that use this ViewHolder have it
		bulb = v.findViewById(R.id.bulbView);
	}

	protected void bind(I item, @Nullable OnContactClickListener<I> listener) {
		//accessing the firebase storage
		storage = FirebaseStorage.getInstance();
		//creates a storage reference
		storageRef = storage.getReference();
		Author author = item.getContact().getAuthor();
		//set avatar or sets identicon if no image found in firebase storage
		Glide.with(avatar.getContext() /* context */)
				.using(new FirebaseImageLoader())
				.load(storageRef.child("/"+author.getName()+"/pic.jpg"))
				.error(new IdenticonDrawable(author.getId().getBytes()))
				.into(avatar);
		String contactName = author.getName();
		name.setText(contactName);

		if (bulb != null) {
			// online/offline
			if (item.isConnected()) {
				bulb.setImageResource(R.drawable.contact_connected);
			} else {
				bulb.setImageResource(R.drawable.contact_disconnected);
			}
		}

		layout.setOnClickListener(v -> {
			if (listener != null) listener.onItemClick(avatar, item);
		});
	}

}
