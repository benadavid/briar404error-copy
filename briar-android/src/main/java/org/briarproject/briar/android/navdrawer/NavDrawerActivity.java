package org.briarproject.briar.android.navdrawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.api.plugin.BluetoothConstants;
import org.briarproject.bramble.api.plugin.LanTcpConstants;
import org.briarproject.bramble.api.plugin.TorConstants;
import org.briarproject.bramble.api.plugin.TransportId;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.userprofile.UserProfileActivity;
import org.briarproject.briar.android.blog.FeedFragment;
import org.briarproject.briar.android.contact.ContactListFragment;
import org.briarproject.briar.android.controller.handler.UiResultHandler;
import org.briarproject.briar.android.forum.ForumListFragment;
import org.briarproject.briar.android.fragment.BaseFragment;
import org.briarproject.briar.android.fragment.BaseFragment.BaseFragmentListener;
import org.briarproject.briar.android.fragment.SignOutFragment;
import org.briarproject.briar.android.navdrawer.NavDrawerController.ExpiryWarning;
import org.briarproject.briar.android.privategroup.list.GroupListFragment;
import org.briarproject.briar.android.settings.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import static android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE;
import static android.support.v4.view.GravityCompat.START;
import static android.support.v4.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.briarproject.briar.android.BriarService.EXTRA_STARTUP_FAILED;
import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_PASSWORD;
import static org.briarproject.briar.android.navdrawer.NavDrawerController.ExpiryWarning.NO;
import static org.briarproject.briar.android.navdrawer.NavDrawerController.ExpiryWarning.UPDATE;
import static org.briarproject.briar.android.util.UiUtils.getDaysUntilExpiry;

public class NavDrawerActivity extends BriarActivity implements
		BaseFragmentListener, TransportStateListener,
		OnNavigationItemSelectedListener {
	/** Class name for log messages. */
	private static final String LOG_TAG = NavDrawerActivity.class.getSimpleName();

	public static final String INTENT_CONTACTS = "intent_contacts";
	public static final String INTENT_GROUPS = "intent_groups";
	public static final String INTENT_FORUMS = "intent_forums";
	public static final String INTENT_BLOGS = "intent_blogs";

	private static final Logger LOG =
			Logger.getLogger(NavDrawerActivity.class.getName());

	private ActionBarDrawerToggle drawerToggle;

	// walkthrough variables
	private ShowcaseView showcaseView;
	private Target target1, target2;
	private int showcaseCounter;

	@Inject
	NavDrawerController controller;

	private DrawerLayout drawerLayout;
	private NavigationView navigation;

	ImageView imageView;
	Button button;
	private static final int IMAGE_UPLOAD_REQUEST=42;
	Uri imageUri;
	private ImageButton imgButton;
	String path;
	public final static String APP_PATH_SD_CARD="/DesiredSubfolderName/";
	public final static String APP_THUMBNAIL_PATH_SD_CARD="thumbnails";
    private static LocalAuthor author;
    public static String nickname1;

	private List<Transport> transports;
	private BaseAdapter transportsAdapter;
	//Firebase storage object
	private StorageReference mStorageRef;

    @Inject
    volatile IdentityManager identityManager;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		exitIfStartupFailed(intent);
		// TODO don't create new instances if they are on the stack (#606)
		if (intent.getBooleanExtra(INTENT_GROUPS, false)) {
			startFragment(GroupListFragment.newInstance(), R.id.nav_btn_groups);
		} else if (intent.getBooleanExtra(INTENT_FORUMS, false)) {
			startFragment(ForumListFragment.newInstance(), R.id.nav_btn_forums);
		} else if (intent.getBooleanExtra(INTENT_CONTACTS, false)) {
			startFragment(ContactListFragment.newInstance(),
					R.id.nav_btn_contacts);
		} else if (intent.getBooleanExtra(INTENT_BLOGS, false)) {
			startFragment(FeedFragment.newInstance(), R.id.nav_btn_blogs);
		}
		setIntent(null);
	}

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		exitIfStartupFailed(getIntent());
		setContentView(R.layout.activity_nav_drawer);


		Toolbar toolbar = findViewById(R.id.toolbar);
		drawerLayout = findViewById(R.id.drawer_layout);
		navigation = (NavigationView)findViewById(R.id.navigation);
		GridView transportsView = findViewById(R.id.transportsView);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
				R.string.nav_drawer_open_description,
				R.string.nav_drawer_close_description){

			// triggers walkthrough when navigation drawer is first opened
			public void onDrawerOpened(View drawerView){
				super.onDrawerOpened(drawerView);

				// highlights areas of the nav drawer featured in walkthrough
				target1 = new ViewTarget(R.id.navigation, NavDrawerActivity.this);
				target2 = new ViewTarget(R.id.connectivity, NavDrawerActivity.this);

				showcaseView = new ShowcaseView.Builder(NavDrawerActivity.this)
						.setTarget(Target.NONE)
						.setContentTitle("Walkthrough")
						.setContentText("Welcome to Briar, let's walk you through some of its features")
						.setStyle(R.style.ShowcaseTheme)
						.singleShot(77)
						.setOnClickListener((View v) -> {
								switch(showcaseCounter) {
									case 0:
										showcaseView.setShowcase(target1, true);
										showcaseView.setContentTitle("Features");
										showcaseView.setContentText("You can view your contacts, create private groups," +
												" and browse forums and blogs");
										break;
									case 1:
										showcaseView.setShowcase(target2, true);
										showcaseView.setContentTitle("Connectivity");
										showcaseView.setContentText("Choose to send messages securely via Tor, Bluetooth or Wi-Fi");
										break;
									case 2:
										showcaseView.hide();
										break;
								}
								showcaseCounter++;
						})
						.build();
			}
		};
		drawerLayout.addDrawerListener(drawerToggle);
		navigation.setNavigationItemSelectedListener(this);

		initializeTransports(getLayoutInflater());
		transportsView.setAdapter(transportsAdapter);

		if (state == null) {
			startFragment(ContactListFragment.newInstance(),
					R.id.nav_btn_contacts);
		}
		if (getIntent() != null) {
			onNewIntent(getIntent());
		}

        //retrieve local user nickname
        runOnDbThread(() -> {

            // Load the local pseudonym
            try {
                author = identityManager.getLocalAuthor();
                nickname1 = author.getName();
                ;
            } catch (DbException e) {
                return;
            }

        });

		//Firebase storage object initalized
		mStorageRef = FirebaseStorage.getInstance().getReference();

		//avatar button
		imgButton = (ImageButton) navigation.findViewById(R.id.imageButton1);
		          imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
	            startActivity(new Intent(NavDrawerActivity.this, UserProfileActivity.class));
            	/*
            	Intent intent = new Intent(Intent.ACTION_PICK);
	            intent.setType("image/*");
	            startActivityForResult(intent, IMAGE_UPLOAD_REQUEST);*/
            }
         });

		//******** load pic from internal memory and format it into a circle
		        if(fileExistance("desiredFilename.png")) {
			        // Make the image into a circle
			        // In saveImageToInternalStorage() we named the picture desiredFilename
			        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), getThumbnail("desiredFilename.png"));
			        roundedBitmapDrawable.setCircular(true);
			        imgButton.setImageDrawable(roundedBitmapDrawable);
			        }
		//********* end of load pic
	}

	//************
	//Save picture to internal memory
	//************
	public boolean saveImageToInternalStorage(Bitmap image) {

		try {
			// Use the compress method on the Bitmap object to write image to
			// the OutputStream
			//user may name the picture file in any way he wishes. Default is "desiredFilename"
			FileOutputStream fos = openFileOutput("desiredFilename.png", Context.MODE_PRIVATE);

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


	@Override
	@SuppressLint("NewApi")
	public void onStart() {
		super.onStart();
		updateTransports();
		controller.showExpiryWarning(new UiResultHandler<ExpiryWarning>(this) {
			@Override
			public void onResultUi(ExpiryWarning expiry) {
				if (expiry != NO) showExpiryWarning(expiry);
			}
		});

		//******** load pic from internal memory and format it into a circle
		if(fileExistance("desiredFilename.png")) {
			// Make the image into a circle
			// In saveImageToInternalStorage() we named the picture desiredFilename
			RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), getThumbnail("desiredFilename.png"));
			roundedBitmapDrawable.setCircular(true);
			imgButton.setImageDrawable(roundedBitmapDrawable);
		}
		//********* end of load pic
	}

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);
		if (request == REQUEST_PASSWORD && result == RESULT_OK) {
			controller.shouldAskForDozeWhitelisting(this,
					new UiResultHandler<Boolean>(this) {
						@Override
						public void onResultUi(Boolean ask) {
							if (ask) {
								showDozeDialog(
										getString(R.string.setup_doze_intro));
							}
						}
					});
		}

		//avatar
		if(result == RESULT_CANCELED) return;

		/*
		if (request == IMAGE_UPLOAD_REQUEST) {
			ParcelFileDescriptor fd;
			try {
				fd = getContentResolver().openFileDescriptor(data.getData(), "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}

			// Get the image file location
			Bitmap bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());

			//save to internal storage
			saveImageToInternalStorage(bmp);

			// Make the image into a circle
			RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bmp);
			roundedBitmapDrawable.setCircular(true);
			imgButton.setImageDrawable(roundedBitmapDrawable);
		}*/
	}

	private void exitIfStartupFailed(Intent intent) {
		if (intent.getBooleanExtra(EXTRA_STARTUP_FAILED, false)) {
			finish();
			LOG.info("Exiting");
			System.exit(0);
		}
	}

	private void loadFragment(int fragmentId) {
		// TODO re-use fragments from the manager when possible (#606)
		switch (fragmentId) {
			case R.id.nav_btn_contacts:
				startFragment(ContactListFragment.newInstance());
				break;
			case R.id.nav_btn_groups:
				startFragment(GroupListFragment.newInstance());
				break;
			case R.id.nav_btn_forums:
				startFragment(ForumListFragment.newInstance());
				break;
			case R.id.nav_btn_blogs:
				startFragment(FeedFragment.newInstance());
				break;
			case R.id.nav_btn_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			case R.id.nav_btn_signout:
				signOut();
				break;
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		drawerLayout.closeDrawer(START);
		clearBackStack();
		loadFragment(item.getItemId());
		// Don't display the Settings item as checked
		return item.getItemId() != R.id.nav_btn_settings;
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(START)) {
			drawerLayout.closeDrawer(START);
		} else if (getSupportFragmentManager().getBackStackEntryCount() == 0 &&
				getSupportFragmentManager()
						.findFragmentByTag(ContactListFragment.TAG) != null) {
			Intent i = new Intent(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);
		} else if (getSupportFragmentManager().getBackStackEntryCount() == 0 &&
				getSupportFragmentManager()
						.findFragmentByTag(ContactListFragment.TAG) == null) {
			/*
			 * This makes sure that the first fragment (ContactListFragment) the
			 * user sees is the same as the last fragment the user sees before
			 * exiting. This models the typical Google navigation behaviour such
			 * as in Gmail/Inbox.
			 */
			startFragment(ContactListFragment.newInstance(),
					R.id.nav_btn_contacts);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	private void signOut() {
		drawerLayout.setDrawerLockMode(LOCK_MODE_LOCKED_CLOSED);
		startFragment(new SignOutFragment());
		signOut(false);
	}

	private void startFragment(BaseFragment fragment, int itemId) {
		navigation.setCheckedItem(itemId);
		startFragment(fragment);
	}

	private void startFragment(BaseFragment fragment) {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
			startFragment(fragment, false);
		else startFragment(fragment, true);
	}

	private void startFragment(BaseFragment fragment,
			boolean isAddedToBackStack) {
		FragmentTransaction trans =
				getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.fade_in,
								R.anim.fade_out, R.anim.fade_in,
								R.anim.fade_out)
						.replace(R.id.fragmentContainer, fragment,
								fragment.getUniqueTag());
		if (isAddedToBackStack) {
			trans.addToBackStack(fragment.getUniqueTag());
		}
		trans.commit();
	}

	private void clearBackStack() {
		getSupportFragmentManager().popBackStackImmediate(null,
				POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void handleDbException(DbException e) {
		// Do nothing for now
	}

	@SuppressWarnings("ConstantConditions")
	private void showExpiryWarning(ExpiryWarning expiry) {
		int daysUntilExpiry = getDaysUntilExpiry();
		if (daysUntilExpiry < 0) signOut();

		// show expiry warning text
		ViewGroup expiryWarning = findViewById(R.id.expiryWarning);
		TextView expiryWarningText =
				expiryWarning.findViewById(R.id.expiryWarningText);
		// make close button functional
		ImageView expiryWarningClose =
				expiryWarning.findViewById(R.id.expiryWarningClose);

		// show a different snackbar in green if this is an update
		if (expiry == UPDATE) {
			expiryWarning.setBackgroundColor(
					ContextCompat.getColor(this, R.color.briar_green_light));
			expiryWarningText.setText(
					getString(R.string.expiry_update, daysUntilExpiry));
			expiryWarningText.setTextColor(
					ContextCompat.getColor(this, android.R.color.black));
			expiryWarningClose.setColorFilter(
					ContextCompat.getColor(this, android.R.color.black));
		} else {
			expiryWarningText.setText(getResources()
					.getQuantityString(R.plurals.expiry_warning,
							daysUntilExpiry, daysUntilExpiry));
		}

		expiryWarningClose.setOnClickListener(v -> {
			controller.expiryWarningDismissed();
			expiryWarning.setVisibility(GONE);
		});

		expiryWarning.setVisibility(VISIBLE);
	}

	private void initializeTransports(LayoutInflater inflater) {
		transports = new ArrayList<>(3);

		Transport tor = new Transport();
		tor.id = TorConstants.ID;
		tor.enabled = controller.isTransportRunning(tor.id);
		tor.iconId = R.drawable.transport_tor;
		tor.textId = R.string.transport_tor;
		transports.add(tor);

		Transport bt = new Transport();
		bt.id = BluetoothConstants.ID;
		bt.enabled = controller.isTransportRunning(bt.id);
		bt.iconId = R.drawable.transport_bt;
		bt.textId = R.string.transport_bt;
		transports.add(bt);

		Transport lan = new Transport();
		lan.id = LanTcpConstants.ID;
		lan.enabled = controller.isTransportRunning(lan.id);
		lan.iconId = R.drawable.transport_lan;
		lan.textId = R.string.transport_lan;
		transports.add(lan);

		transportsAdapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return transports.size();
			}

			@Override
			public Transport getItem(int position) {
				return transports.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View view;
				if (convertView != null) {
					view = convertView;
				} else {
					view = inflater.inflate(R.layout.list_item_transport,
							parent, false);
				}

				Transport t = getItem(position);
				int c;
				if (t.enabled) {
					c = ContextCompat.getColor(NavDrawerActivity.this,
							R.color.briar_green_light);
				} else {
					c = ContextCompat.getColor(NavDrawerActivity.this,
							android.R.color.tertiary_text_light);
				}

				ImageView icon = view.findViewById(R.id.imageView);
				icon.setImageDrawable(ContextCompat
						.getDrawable(NavDrawerActivity.this, t.iconId));
				icon.setColorFilter(c);

				TextView text = view.findViewById(R.id.textView);
				text.setText(getString(t.textId));

				return view;
			}
		};
	}

	private void setTransport(TransportId id, boolean enabled) {
		runOnUiThreadUnlessDestroyed(() -> {
			if (transports == null || transportsAdapter == null) return;
			for (Transport t : transports) {
				if (t.id.equals(id)) {
					t.enabled = enabled;
					transportsAdapter.notifyDataSetChanged();
					break;
				}
			}
		});
	}

	private void updateTransports() {
		if (transports == null || transportsAdapter == null) return;
		for (Transport t : transports) {
			t.enabled = controller.isTransportRunning(t.id);
		}
		transportsAdapter.notifyDataSetChanged();
	}

	@Override
	public void stateUpdate(TransportId id, boolean enabled) {
		setTransport(id, enabled);
	}

	private static class Transport {

		private TransportId id;
		private boolean enabled;
		private int iconId;
		private int textId;
	}

	//******
    //check if the picture exists in the internal memory before trying to load the picture to prevent crash
	//******
	public boolean fileExistance(String fname){
		File file = getBaseContext().getFileStreamPath(fname);
	    return file.exists();
	}
	//end of file existance code
}
