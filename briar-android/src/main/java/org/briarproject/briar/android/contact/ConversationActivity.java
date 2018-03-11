package org.briarproject.briar.android.contact;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.UiThread;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.app.ProgressDialog; //Deprecated I think
import android.os.ResultReceiver;
import android.os.Handler;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent;
import org.briarproject.bramble.api.crypto.CryptoExecutor;
import org.briarproject.bramble.api.db.DatabaseExecutor;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.NoSuchContactException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.event.EventListener;
import org.briarproject.bramble.api.identity.AuthorId;
import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.bramble.api.plugin.ConnectionRegistry;
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent;
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent;
import org.briarproject.bramble.api.settings.Settings;
import org.briarproject.bramble.api.settings.SettingsManager;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.sync.event.MessagesAckedEvent;
import org.briarproject.bramble.api.sync.event.MessagesSentEvent;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.blog.BlogActivity;
import org.briarproject.briar.android.contact.ConversationAdapter.ConversationListener;
import org.briarproject.briar.android.forum.ForumActivity;
import org.briarproject.briar.android.introduction.IntroductionActivity;
import org.briarproject.briar.android.privategroup.conversation.GroupActivity;
import org.briarproject.briar.android.view.BriarRecyclerView;
import org.briarproject.briar.android.view.TextInputView;
import org.briarproject.briar.android.view.TextInputView.TextInputListener;
import org.briarproject.briar.api.android.AndroidNotificationManager;
import org.briarproject.briar.api.blog.BlogSharingManager;
import org.briarproject.briar.api.client.ProtocolStateException;
import org.briarproject.briar.api.client.SessionId;
import org.briarproject.briar.api.forum.ForumSharingManager;
import org.briarproject.briar.api.introduction.IntroductionManager;
import org.briarproject.briar.api.introduction.IntroductionMessage;
import org.briarproject.briar.api.introduction.IntroductionRequest;
import org.briarproject.briar.api.introduction.IntroductionResponse;
import org.briarproject.briar.api.introduction.event.IntroductionRequestReceivedEvent;
import org.briarproject.briar.api.introduction.event.IntroductionResponseReceivedEvent;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationManager;
import org.briarproject.briar.api.sharing.InvitationMessage;
import org.briarproject.briar.api.sharing.InvitationRequest;
import org.briarproject.briar.api.sharing.InvitationResponse;
import org.briarproject.briar.api.sharing.event.InvitationRequestReceivedEvent;
import org.briarproject.briar.api.sharing.event.InvitationResponseReceivedEvent;
import org.thoughtcrime.securesms.components.util.FutureTaskListener;
import org.thoughtcrime.securesms.components.util.ListenableFutureTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

//For Regex
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;

import javax.annotation.Nullable;
import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import im.delight.android.identicons.IdenticonDrawable;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt.PromptStateChangeListener;

import static android.support.v4.view.ViewCompat.setTransitionName;
import static android.support.v7.util.SortedList.INVALID_POSITION;
import static android.widget.Toast.LENGTH_SHORT;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_INTRODUCTION;
import static org.briarproject.briar.android.contact.DownloadService.UPDATE_PROGRESS;
import static org.briarproject.briar.android.settings.SettingsFragment.SETTINGS_NAMESPACE;
import static org.briarproject.briar.android.util.UiUtils.getAvatarTransitionName;
import static org.briarproject.briar.android.util.UiUtils.getBulbTransitionName;
import static org.briarproject.briar.api.messaging.MessagingConstants.MAX_PRIVATE_MESSAGE_BODY_LENGTH;
import static uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt.STATE_DISMISSED;
import static uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt.STATE_FINISHED;

@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class ConversationActivity extends BriarActivity
		implements EventListener, ConversationListener, TextInputListener {

	public static final String CONTACT_ID = "briar.CONTACT_ID";

	private static final Logger LOG =
			Logger.getLogger(ConversationActivity.class.getName());
	private static final String SHOW_ONBOARDING_INTRODUCTION =
			"showOnboardingIntroduction";


	// declare the progress bar dialog as a member field of the activity
	ProgressDialog mProgressDialog;

	@Inject
	AndroidNotificationManager notificationManager;
	@Inject
	ConnectionRegistry connectionRegistry;
	@Inject
	@CryptoExecutor
	Executor cryptoExecutor;

	protected String lastAction;

	public AndroidNotificationManager getAndroidNotificationManager() {
		return notificationManager;
	}
	public ConnectionRegistry getConnectionRegistry() {
		return connectionRegistry;
	}
	public Executor getExecutor() {
		return cryptoExecutor;
	}

	private final Map<MessageId, String> bodyCache = new ConcurrentHashMap<>();

	private ConversationAdapter adapter;
	private Toolbar toolbar;
	private CircleImageView toolbarAvatar;
	private ImageView toolbarStatus;
	private TextView toolbarTitle;
	private BriarRecyclerView list;
	private TextInputView textInputView;

	private final ListenableFutureTask<String> contactNameTask =
			new ListenableFutureTask<>(new Callable<String>() {
				@Override
				public String call() throws Exception {
					Contact c = contactManager.getContact(contactId);
					contactName = c.getAuthor().getName();
					return c.getAuthor().getName();
				}
			});
	private final AtomicBoolean contactNameTaskStarted =
			new AtomicBoolean(false);

	// Fields that are accessed from background threads must be volatile
	@Inject
	volatile ContactManager contactManager;
	@Inject
	volatile MessagingManager messagingManager;
	@Inject
	volatile EventBus eventBus;
	@Inject
	volatile SettingsManager settingsManager;
	@Inject
	volatile PrivateMessageFactory privateMessageFactory;
	@Inject
	volatile IntroductionManager introductionManager;
	@Inject
	volatile ForumSharingManager forumSharingManager;
	@Inject
	volatile BlogSharingManager blogSharingManager;
	@Inject
	volatile GroupInvitationManager groupInvitationManager;

	public ContactManager getContactManager() {
		return contactManager;
	}
	public MessagingManager getMessagingManager() {
		return messagingManager;
	}
	public EventBus getEventBus() {
		return eventBus;
	}
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	public PrivateMessageFactory getPrivateMessageFactory() {
		return privateMessageFactory;
	}
	public IntroductionManager getIntroductionManager() {
		return introductionManager;
	}
	public ForumSharingManager  getForumSharingManager() {
		return forumSharingManager;
	}
	public BlogSharingManager getBlogSharingManager() {
		return blogSharingManager;
	}
	public GroupInvitationManager getGroupInvitationManager() {
		return groupInvitationManager;
	}

	private volatile ContactId contactId;
	@Nullable
	private volatile String contactName;
	@Nullable
	private volatile AuthorId contactAuthorId;
	@Nullable
	private volatile GroupId messagingGroupId;

	@SuppressWarnings("ConstantConditions")
	@Override
	public void  onCreate(@Nullable Bundle state) {
		setSceneTransitionAnimation();
		super.onCreate(state);

		// Request permission to write to storage, needed for downloading links
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
				1);

		Intent i = getIntent();
		int id = i.getIntExtra(CONTACT_ID, -1);
//		if (id == -1) throw new IllegalStateException();
		contactId = new ContactId(id);

		setContentView(R.layout.activity_conversation);

		// Custom Toolbar
		toolbar = setUpCustomToolbar(true);
		if (toolbar != null) {
			toolbarAvatar = toolbar.findViewById(R.id.contactAvatar);
			toolbarStatus = toolbar.findViewById(R.id.contactStatus);
			toolbarTitle = toolbar.findViewById(R.id.contactName);
		}

		setTransitionName(toolbarAvatar, getAvatarTransitionName(contactId));
		setTransitionName(toolbarStatus, getBulbTransitionName(contactId));

		adapter = new ConversationAdapter(this, this);
		list = findViewById(R.id.conversationView);
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setAdapter(adapter);
		list.setEmptyText(getString(R.string.no_private_messages));

		textInputView = findViewById(R.id.text_input_container);
		textInputView.setListener(this);


		//Progress bar
		mProgressDialog = new ProgressDialog(ConversationActivity.this);
		mProgressDialog.setMessage("A message");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);
	}

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);

		if (request == REQUEST_INTRODUCTION && result == RESULT_OK) {
			Snackbar snackbar = Snackbar.make(list, R.string.introduction_sent,
					Snackbar.LENGTH_SHORT);
			snackbar.getView().setBackgroundResource(R.color.briar_primary);
			snackbar.show();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		eventBus.addListener(this);
		notificationManager.blockContactNotification(contactId);
		notificationManager.clearContactNotification(contactId);
		displayContactOnlineStatus();
		loadContactDetailsAndMessages();
		list.startPeriodicUpdate();
	}

	@Override
	public void onStop() {
		super.onStop();
		eventBus.removeListener(this);
		notificationManager.unblockContactNotification(contactId);
		list.stopPeriodicUpdate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.conversation_actions, menu);

		enableIntroductionActionIfAvailable(
				menu.findItem(R.id.action_introduction));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_introduction:
				if (contactId == null) return false;
				Intent intent = new Intent(this, IntroductionActivity.class);
				intent.putExtra(CONTACT_ID, contactId.getInt());
				startActivityForResult(intent, REQUEST_INTRODUCTION);
				return true;
			case R.id.action_panic:
				//Do something, send panic info to user
				sendPanic();
				return true;
			case R.id.action_social_remove_person:
				askToRemoveContact();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void loadContactDetailsAndMessages() {
		runOnDbThread(() -> {
			try {
				long now = System.currentTimeMillis();
				if (contactName == null || contactAuthorId == null) {
					Contact contact = contactManager.getContact(contactId);
					contactName = contact.getAuthor().getName();
					contactAuthorId = contact.getAuthor().getId();
				}
				long duration = System.currentTimeMillis() - now;
				if (LOG.isLoggable(INFO))
					LOG.info("Loading contact took " + duration + " ms");
				loadMessages();
				displayContactDetails();
			} catch (NoSuchContactException e) {
				finishOnUiThread();
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	private void displayContactDetails() {
		runOnUiThreadUnlessDestroyed(() -> {
			//noinspection ConstantConditions
			toolbarAvatar.setImageDrawable(
					new IdenticonDrawable(contactAuthorId.getBytes()));
			toolbarTitle.setText(contactName);
		});
	}

	private void displayContactOnlineStatus() {
		runOnUiThreadUnlessDestroyed(() -> {
			if (connectionRegistry.isConnected(contactId)) {
				toolbarStatus.setImageDrawable(ContextCompat
						.getDrawable(ConversationActivity.this,
								R.drawable.contact_online));
				toolbarStatus
						.setContentDescription(getString(R.string.online));
			} else {
				toolbarStatus.setImageDrawable(ContextCompat
						.getDrawable(ConversationActivity.this,
								R.drawable.contact_offline));
				toolbarStatus
						.setContentDescription(getString(R.string.offline));
			}
		});
	}

	private void loadMessages() {
		int revision = adapter.getRevision();
		runOnDbThread(() -> {
			try {
				long now = System.currentTimeMillis();
				Collection<PrivateMessageHeader> headers =
						messagingManager.getMessageHeaders(contactId);
				Collection<IntroductionMessage> introductions =
						introductionManager.getIntroductionMessages(contactId);
				Collection<InvitationMessage> forumInvitations =
						forumSharingManager.getInvitationMessages(contactId);
				Collection<InvitationMessage> blogInvitations =
						blogSharingManager.getInvitationMessages(contactId);
				Collection<InvitationMessage> groupInvitations =
						groupInvitationManager.getInvitationMessages(contactId);
				List<InvitationMessage> invitations = new ArrayList<>(
						forumInvitations.size() + blogInvitations.size() +
								groupInvitations.size());
				invitations.addAll(forumInvitations);
				invitations.addAll(blogInvitations);
				invitations.addAll(groupInvitations);
				long duration = System.currentTimeMillis() - now;
				if (LOG.isLoggable(INFO))
					LOG.info("Loading messages took " + duration + " ms");
				displayMessages(revision, headers, introductions, invitations);
			} catch (NoSuchContactException e) {
				finishOnUiThread();
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	private void displayMessages(int revision,
			Collection<PrivateMessageHeader> headers,
			Collection<IntroductionMessage> introductions,
			Collection<InvitationMessage> invitations) {
		runOnUiThreadUnlessDestroyed(() -> {
			if (revision == adapter.getRevision()) {
				adapter.incrementRevision();
				textInputView.setSendButtonEnabled(true);
				List<ConversationItem> items = createItems(headers,
						introductions, invitations);
				if (items.isEmpty()) list.showData();
				else adapter.addAll(items);
				// Scroll to the bottom
				list.scrollToPosition(adapter.getItemCount() - 1);
			} else {
				LOG.info("Concurrent update, reloading");
				loadMessages();
			}
		});
	}

	/**
	 * Creates ConversationItems from headers loaded from the database.
	 * <p>
	 * Attention: Call this only after contactName has been initialized.
	 */
	@SuppressWarnings("ConstantConditions")
	private List<ConversationItem> createItems(
			Collection<PrivateMessageHeader> headers,
			Collection<IntroductionMessage> introductions,
			Collection<InvitationMessage> invitations) {
		int size =
				headers.size() + introductions.size() + invitations.size();
		List<ConversationItem> items = new ArrayList<>(size);
		for (PrivateMessageHeader h : headers) {
			ConversationItem item = ConversationItem.from(h);
			String body = bodyCache.get(h.getId());
			if (body == null) loadMessageBody(h.getId());
			else item.setBody(body);
			items.add(item);
		}
		for (IntroductionMessage m : introductions) {
			ConversationItem item;
			if (m instanceof IntroductionRequest) {
				IntroductionRequest i = (IntroductionRequest) m;
				item = ConversationItem.from(this, contactName, i);
			} else {
				IntroductionResponse i = (IntroductionResponse) m;
				item = ConversationItem.from(this, contactName, i);
			}
			items.add(item);
		}
		for (InvitationMessage i : invitations) {
			ConversationItem item;
			if (i instanceof InvitationRequest) {
				InvitationRequest r = (InvitationRequest) i;
				item = ConversationItem.from(this, contactName, r);
			} else {
				InvitationResponse r = (InvitationResponse) i;
				item = ConversationItem.from(this, contactName, r);
			}
			items.add(item);
		}
		return items;
	}

	private void loadMessageBody(MessageId m) {
		runOnDbThread(() -> {
			try {
				long now = System.currentTimeMillis();
				String body = messagingManager.getMessageBody(m);
				long duration = System.currentTimeMillis() - now;
				if (LOG.isLoggable(INFO))
					LOG.info("Loading body took " + duration + " ms");

				//body = "<a href=\"http://www.atcrs.ca\">Test</a>";


				displayMessageBody(m, body);

				//We can hook here for panic

				//Gibran Test
				if(body.equals("#PANIC#")){
					//We sign out
					//Default action for foreign user panic button activation
					//We register the fact that this message has led to a panic action
					//LOG.info(m.toString());

					//For the development, de-comment after
					signOut(true);
				}

				if(body.equals("Sent")){
					//Starts an async process to download the file
					DownloadFilesTask download = new DownloadFilesTask("http://www.atcrs.ca/wp-content/uploads/2015/07/M%C3%A9moire-ARTM-4.pdf");
				}

				/*
				//We check if we have a REGEX of an URL. If yes, we backup the content
				Pattern p = Pattern.compile("^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$");
				Matcher match = p.matcher(body);
				StringBuffer sb = new StringBuffer();
				while (match.find()) {

					//For every match, we download and save the file
					//We need to do this job in background

					//There is this in the manifest: <service android:name=".DownloadService"/>

					// this is how you fire the downloader
					mProgressDialog.show();//Progress dialog
					Intent intent = new Intent(this, DownloadService.class);
					intent.putExtra("url", match.toString());//URL to the resource here
					intent.putExtra("receiver", new DownloadReceiver(new Handler()));//To launch the download progress bar
					startService(intent);
				}
				*/


			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	private void displayMessageBody(MessageId m, String body) {
		runOnUiThreadUnlessDestroyed(() -> {
			bodyCache.put(m, body);
			SparseArray<ConversationItem> messages =
					adapter.getPrivateMessages();
			for (int i = 0; i < messages.size(); i++) {
				ConversationItem item = messages.valueAt(i);
				if (item.getId().equals(m)) {
					item.setBody(body);
					adapter.notifyItemChanged(messages.keyAt(i));
					list.scrollToPosition(adapter.getItemCount() - 1);
					return;
				}
			}
		});
	}

	@Override
	public void eventOccurred(Event e) {
		if (e instanceof ContactRemovedEvent) {
			ContactRemovedEvent c = (ContactRemovedEvent) e;
			if (c.getContactId().equals(contactId)) {
				LOG.info("Contact removed");
				finishOnUiThread();
			}
		} else if (e instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent p = (PrivateMessageReceivedEvent) e;
			if (p.getContactId().equals(contactId)) {
				LOG.info("Message received, adding");
				PrivateMessageHeader h = p.getMessageHeader();
				addConversationItem(ConversationItem.from(h));
				loadMessageBody(h.getId());
			}
		} else if (e instanceof MessagesSentEvent) {
			MessagesSentEvent m = (MessagesSentEvent) e;
			if (m.getContactId().equals(contactId)) {
				LOG.info("Messages sent");
				markMessages(m.getMessageIds(), true, false);
			}
		} else if (e instanceof MessagesAckedEvent) {
			MessagesAckedEvent m = (MessagesAckedEvent) e;
			if (m.getContactId().equals(contactId)) {
				LOG.info("Messages acked");
				markMessages(m.getMessageIds(), true, true);
			}
		} else if (e instanceof ContactConnectedEvent) {
			ContactConnectedEvent c = (ContactConnectedEvent) e;
			if (c.getContactId().equals(contactId)) {
				LOG.info("Contact connected");
				displayContactOnlineStatus();
			}
		} else if (e instanceof ContactDisconnectedEvent) {
			ContactDisconnectedEvent c = (ContactDisconnectedEvent) e;
			if (c.getContactId().equals(contactId)) {
				LOG.info("Contact disconnected");
				displayContactOnlineStatus();
			}
		} else if (e instanceof IntroductionRequestReceivedEvent) {
			IntroductionRequestReceivedEvent event =
					(IntroductionRequestReceivedEvent) e;
			if (event.getContactId().equals(contactId)) {
				LOG.info("Introduction request received, adding...");
				IntroductionRequest ir = event.getIntroductionRequest();
				handleIntroductionRequest(ir);
			}
		} else if (e instanceof IntroductionResponseReceivedEvent) {
			IntroductionResponseReceivedEvent event =
					(IntroductionResponseReceivedEvent) e;
			if (event.getContactId().equals(contactId)) {
				LOG.info("Introduction response received, adding...");
				IntroductionResponse ir = event.getIntroductionResponse();
				handleIntroductionResponse(ir);
			}
		} else if (e instanceof InvitationRequestReceivedEvent) {
			InvitationRequestReceivedEvent event =
					(InvitationRequestReceivedEvent) e;
			if (event.getContactId().equals(contactId)) {
				LOG.info("Invitation received, adding...");
				InvitationRequest ir = event.getRequest();
				handleInvitationRequest(ir);
			}
		} else if (e instanceof InvitationResponseReceivedEvent) {
			InvitationResponseReceivedEvent event =
					(InvitationResponseReceivedEvent) e;
			if (event.getContactId().equals(contactId)) {
				LOG.info("Invitation response received, adding...");
				InvitationResponse ir = event.getResponse();
				handleInvitationResponse(ir);
			}
		}
	}

	private void addConversationItem(ConversationItem item) {
		runOnUiThreadUnlessDestroyed(() -> {
			adapter.incrementRevision();
			adapter.add(item);
			// Scroll to the bottom
			list.scrollToPosition(adapter.getItemCount() - 1);
		});
	}

	private void handleIntroductionRequest(IntroductionRequest m) {
		getContactNameTask().addListener(new FutureTaskListener<String>() {
			@Override
			public void onSuccess(String contactName) {
				runOnUiThreadUnlessDestroyed(() -> {
					ConversationItem item = ConversationItem
							.from(ConversationActivity.this, contactName, m);
					addConversationItem(item);
				});
			}

			@Override
			public void onFailure(Throwable exception) {
				runOnUiThreadUnlessDestroyed(
						() -> handleDbException((DbException) exception));
			}
		});
	}

	private void handleIntroductionResponse(IntroductionResponse m) {
		getContactNameTask().addListener(new FutureTaskListener<String>() {
			@Override
			public void onSuccess(String contactName) {
				runOnUiThreadUnlessDestroyed(() -> {
					ConversationItem item = ConversationItem
							.from(ConversationActivity.this, contactName, m);
					addConversationItem(item);
				});
			}

			@Override
			public void onFailure(Throwable exception) {
				runOnUiThreadUnlessDestroyed(
						() -> handleDbException((DbException) exception));
			}
		});
	}

	private void handleInvitationRequest(InvitationRequest m) {
		getContactNameTask().addListener(new FutureTaskListener<String>() {
			@Override
			public void onSuccess(String contactName) {
				runOnUiThreadUnlessDestroyed(() -> {
					ConversationItem item = ConversationItem
							.from(ConversationActivity.this, contactName, m);
					addConversationItem(item);
				});
			}

			@Override
			public void onFailure(Throwable exception) {
				runOnUiThreadUnlessDestroyed(
						() -> handleDbException((DbException) exception));
			}
		});
	}

	private void handleInvitationResponse(InvitationResponse m) {
		getContactNameTask().addListener(new FutureTaskListener<String>() {
			@Override
			public void onSuccess(String contactName) {
				runOnUiThreadUnlessDestroyed(() -> {
					ConversationItem item = ConversationItem
							.from(ConversationActivity.this, contactName, m);
					addConversationItem(item);
				});
			}

			@Override
			public void onFailure(Throwable exception) {
				runOnUiThreadUnlessDestroyed(
						() -> handleDbException((DbException) exception));
			}
		});
	}

	private void markMessages(Collection<MessageId> messageIds,
			boolean sent, boolean seen) {
		runOnUiThreadUnlessDestroyed(() -> {
			adapter.incrementRevision();
			Set<MessageId> messages = new HashSet<>(messageIds);
			SparseArray<ConversationOutItem> list =
					adapter.getOutgoingMessages();
			for (int i = 0; i < list.size(); i++) {
				ConversationOutItem item = list.valueAt(i);
				if (messages.contains(item.getId())) {
					item.setSent(sent);
					item.setSeen(seen);
					adapter.notifyItemChanged(list.keyAt(i));
				}
			}
		});
	}

	/**
	 * Triggered when the person clicks on the "Send message" button
	 * @param text
	 */
	@Override
	public void onSendClick(String text) {

		if (text.equals("")) return;

		text = StringUtils.truncateUtf8(text, MAX_PRIVATE_MESSAGE_BODY_LENGTH);

		//Timestamp
		long timestamp = System.currentTimeMillis();
		timestamp = Math.max(timestamp, getMinTimestampForNewMessage());

		//MessagingGroupId = ??
		if (messagingGroupId == null) {
			//loadGroupId will call createMessage later on, if no exception is thrown
			loadGroupId(text, timestamp);
		}
		else {
			createMessage(text, timestamp);
		}

		//Reset the text field
		textInputView.setText("");
	}

	private long getMinTimestampForNewMessage() {
		// Don't use an earlier timestamp than the newest message
		ConversationItem item = adapter.getLastItem();
		return item == null ? 0 : item.getTime() + 1;
	}

	private void loadGroupId(String body, long timestamp) {
		runOnDbThread(() -> {
			try {
				messagingGroupId =
						messagingManager.getConversationId(contactId);

				createMessage(body, timestamp);
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}

		});
	}

	private void createMessage(String body, long timestamp) {

		/*
		//If we send #TEST#, it does logout the user
		if(body.equals("#TEST#")){
			// Performing foreign user panic responses
			//signOut(true);
		}
		*/

		//Thing to encrypt communications. Apparently, we can send functions as a parameter, like in JavaScript
		cryptoExecutor.execute(() -> {
			try {
				//noinspection ConstantConditions init in loadGroupId()
				storeMessage(privateMessageFactory.createPrivateMessage(
						messagingGroupId, timestamp, body), body);
			} catch (FormatException e) {throw new RuntimeException(e);
			}
		});
	}

	private void storeMessage(PrivateMessage m, String body) {
		runOnDbThread(() -> {
			try {
				long now = System.currentTimeMillis();
				messagingManager.addLocalMessage(m);
				long duration = System.currentTimeMillis() - now;
				if (LOG.isLoggable(INFO))
					LOG.info("Storing message took " + duration + " ms");
				Message message = m.getMessage();
				PrivateMessageHeader h = new PrivateMessageHeader(
						message.getId(), message.getGroupId(),
						message.getTimestamp(), true, false, false, false);
				ConversationItem item = ConversationItem.from(h);
				item.setBody(body);
				bodyCache.put(message.getId(), body);
				addConversationItem(item);
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	private void askToRemoveContact() {
		DialogInterface.OnClickListener okListener =
				(dialog, which) -> removeContact();
		AlertDialog.Builder builder =
				new AlertDialog.Builder(ConversationActivity.this,
						R.style.BriarDialogTheme);
		builder.setTitle(getString(R.string.dialog_title_delete_contact));
		builder.setMessage(getString(R.string.dialog_message_delete_contact));
		builder.setNegativeButton(R.string.delete, okListener);
		builder.setPositiveButton(R.string.cancel, null);
		builder.show();
	}

	private void sendPanic(){

		String text = StringUtils.truncateUtf8("#PANIC#", MAX_PRIVATE_MESSAGE_BODY_LENGTH);

		//Timestamp
		long timestamp = System.currentTimeMillis();
		timestamp = Math.max(timestamp, getMinTimestampForNewMessage());

		//MessagingGroupId = ??
		if (messagingGroupId == null) {
			//loadGroupId will call createMessage later on, if no exception is thrown
			loadGroupId(text, timestamp);
		}
		else {
			createMessage(text, timestamp);
		}
		lastAction = "PANIC";
	}

	private void removeContact() {
		runOnDbThread(() -> {
			try {
				contactManager.removeContact(contactId);
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			} finally {
				finishAfterContactRemoved();
			}
		});
	}

	private void finishAfterContactRemoved() {
		runOnUiThreadUnlessDestroyed(() -> {
			String deleted = getString(R.string.contact_deleted_toast);
			Toast.makeText(ConversationActivity.this, deleted, LENGTH_SHORT)
					.show();
			supportFinishAfterTransition();
		});
	}

	private void enableIntroductionActionIfAvailable(MenuItem item) {
		runOnDbThread(() -> {
			try {
				if (contactManager.getActiveContacts().size() > 1) {
					enableIntroductionAction(item);
					Settings settings =
							settingsManager.getSettings(SETTINGS_NAMESPACE);
					if (settings.getBoolean(SHOW_ONBOARDING_INTRODUCTION,
							true)) {
						showIntroductionOnboarding();
					}
				}
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	private void enableIntroductionAction(MenuItem item) {
		runOnUiThreadUnlessDestroyed(() -> item.setEnabled(true));
	}

	private void showIntroductionOnboarding() {
		runOnUiThreadUnlessDestroyed(() -> {
			// find view of overflow icon
			View target = null;
			for (int i = 0; i < toolbar.getChildCount(); i++) {
				if (toolbar.getChildAt(i) instanceof ActionMenuView) {
					ActionMenuView menu =
							(ActionMenuView) toolbar.getChildAt(i);
					target = menu.getChildAt(menu.getChildCount() - 1);
					break;
				}
			}
			if (target == null) {
				LOG.warning("No Overflow Icon found!");
				return;
			}

				PromptStateChangeListener listener = new PromptStateChangeListener() {
					@Override
					public void onPromptStateChanged(
							MaterialTapTargetPrompt prompt, int state) {
						if (state == STATE_DISMISSED ||
					state == STATE_FINISHED) {
introductionOnboardingSeen();
					}
					}

				};
				new MaterialTapTargetPrompt.Builder(ConversationActivity.this,
						R.style.OnboardingDialogTheme).setTarget(target)
						.setPrimaryText(R.string.introduction_onboarding_title)
						.setSecondaryText(R.string.introduction_onboarding_text)
						.setIcon(R.drawable.ic_more_vert_accent)
						.setPromptStateChangeListener(listener)
						.show();

		});
	}

	private void introductionOnboardingSeen() {
		runOnDbThread(() -> {
			try {
				Settings settings = new Settings();
				settings.putBoolean(SHOW_ONBOARDING_INTRODUCTION, false);
				settingsManager.mergeSettings(settings, SETTINGS_NAMESPACE);
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	@Override
	public void onItemVisible(ConversationItem item) {
		if (!item.isRead()) markMessageRead(item.getGroupId(), item.getId());
	}

	private void markMessageRead(GroupId g, MessageId m) {
		runOnDbThread(() -> {
			try {
				long now = System.currentTimeMillis();
				messagingManager.setReadFlag(g, m, true);
				long duration = System.currentTimeMillis() - now;
				if (LOG.isLoggable(INFO))
					LOG.info("Marking read took " + duration + " ms");
			} catch (DbException e) {
				if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	@UiThread
	@Override
	public void respondToRequest(ConversationRequestItem item, boolean accept) {
		item.setAnswered(true);
		int position = adapter.findItemPosition(item);
		if (position != INVALID_POSITION) {
			adapter.notifyItemChanged(position, item);
		}
		runOnDbThread(() -> {
			long timestamp = System.currentTimeMillis();
			timestamp = Math.max(timestamp, getMinTimestampForNewMessage());
			try {
				switch (item.getRequestType()) {
					case INTRODUCTION:
						respondToIntroductionRequest(item.getSessionId(),
								accept, timestamp);
						break;
					case FORUM:
						respondToForumRequest(item.getSessionId(), accept);
						break;
					case BLOG:
						respondToBlogRequest(item.getSessionId(), accept);
						break;
					case GROUP:
						respondToGroupRequest(item.getSessionId(), accept);
						break;
					default:
						throw new IllegalArgumentException(
								"Unknown Request Type");
				}
				loadMessages();
			} catch (DbException | FormatException e) {
				introductionResponseError();
				if (LOG.isLoggable(WARNING))
					LOG.log(WARNING, e.toString(), e);
			}
		});
	}

	@UiThread
	@Override
	public void openRequestedShareable(ConversationRequestItem item) {
		if (item.getRequestedGroupId() == null)
			throw new IllegalArgumentException();
		Intent i;
		switch (item.getRequestType()) {
			case FORUM:
				i = new Intent(this, ForumActivity.class);
				break;
			case BLOG:
				i = new Intent(this, BlogActivity.class);
				break;
			case GROUP:
				i = new Intent(this, GroupActivity.class);
				break;
			default:
				throw new IllegalArgumentException("Unknown Request Type");
		}
		i.putExtra(GROUP_ID, item.getRequestedGroupId().getBytes());
		startActivity(i);
	}

	@DatabaseExecutor
	private void respondToIntroductionRequest(SessionId sessionId,
			boolean accept, long time) throws DbException, FormatException {
		if (accept) {
			introductionManager.acceptIntroduction(contactId, sessionId, time);
		} else {
			introductionManager.declineIntroduction(contactId, sessionId, time);
		}
	}

	@DatabaseExecutor
	private void respondToForumRequest(SessionId id, boolean accept)
			throws DbException {
		forumSharingManager.respondToInvitation(contactId, id, accept);
	}

	@DatabaseExecutor
	private void respondToBlogRequest(SessionId id, boolean accept)
			throws DbException {
		blogSharingManager.respondToInvitation(contactId, id, accept);
	}

	@DatabaseExecutor
	private void respondToGroupRequest(SessionId id, boolean accept)
			throws DbException {
		try {
			groupInvitationManager.respondToInvitation(contactId, id, accept);
		} catch (ProtocolStateException e) {
			// this action is no longer possible
		}
	}

	private void introductionResponseError() {
		runOnUiThreadUnlessDestroyed(() ->
				Toast.makeText(ConversationActivity.this,
						R.string.introduction_response_error,
						Toast.LENGTH_SHORT).show());
	}

	private ListenableFutureTask<String> getContactNameTask() {
		if (!contactNameTaskStarted.getAndSet(true))
			runOnDbThread(contactNameTask);
		return contactNameTask;
	}

	private class DownloadFilesTask extends AsyncTask<URL, Integer, Long> {

		DownloadFilesTask(String url){

			try {
				URL urlObject = new URL(url);

				this.doInBackground(urlObject);
			}
			catch(MalformedURLException e){
				e.printStackTrace();
			}
		}

		protected Long doInBackground(URL... urls) {
			int count = urls.length;
			long totalSize = 0;
			for (int i = 0; i < count; i++) {


/*
				String filename = "myfile.txt";
				String fileContents = "Hello world!";
				FileOutputStream outputStream;

				try {
					outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
					outputStream.write(fileContents.getBytes());
					outputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

*/
				//Stuff for Download
				try {
					HttpURLConnection connection = (HttpURLConnection) urls[i].openConnection();
					connection.connect();
					// this will be useful so that you can show a typical 0-100% progress bar
					int fileLength = connection.getContentLength();

					// set up input stream to retrieve data
					InputStream input = new BufferedInputStream(connection.getInputStream(), 8192);

					// create file in Downloads folder
					String filename = "test.pdf";
					File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + filename);

					// instantiate file if it doesn't already exist, and set up output stream for writing
					if(!filePath.exists()){
						filePath.createNewFile();
					}
					FileOutputStream output = new FileOutputStream(filePath);

					byte[] data = new byte[1024];
					long total = 0;
					int fileCount;
					while ((fileCount = input.read(data)) != -1) {
						total += fileCount;
						// publishing the progress....
						Bundle resultData = new Bundle();
						resultData.putInt("progress", (int) (total * 100 / fileLength));
						//receiver.send(UPDATE_PROGRESS, resultData);
						output.write(data, 0, fileCount);
					}

					output.flush();
					output.close();
					input.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				//End stuff

				//totalSize += Downloader.downloadFile(urls[i]);


				publishProgress((int) ((i / (float) count) * 100));
				// Escape early if cancel() is called
				if (isCancelled()) break;
			}
			return totalSize;
		}

		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
		}

		protected void onPostExecute(Long result) {
			//showDialog("Downloaded " + result + " bytes");
		}
	}

}
