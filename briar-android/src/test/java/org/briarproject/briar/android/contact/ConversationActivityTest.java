package org.briarproject.briar.android.contact;

import android.content.Intent;
import android.view.MenuItem;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.crypto.SecretKey;
import org.briarproject.bramble.api.data.BdfDictionary;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.db.Transaction;
import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.identity.AuthorId;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.bramble.api.lifecycle.IoExecutor;
import org.briarproject.bramble.api.plugin.TransportId;
import org.briarproject.bramble.api.properties.TransportProperties;
import org.briarproject.bramble.api.sync.Group;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.Message;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.test.TestUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.view.TextInputView;
import org.briarproject.briar.api.forum.Forum;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;
import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.test.TestUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.api.android.AndroidNotificationManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.briarproject.bramble.contact.ContactManagerImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Map;

import static java.util.logging.Level.INFO;
import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Gibran on 2018-02-11.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ConversationActivityTest {

	private TestConversationActivity conversationActivity;
	private TestConversationActivity spyConversationActivity;
	private MenuItem panicMenuItem;
	private TextInputView textInputView;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Intent intent = new Intent();
		intent.putExtra("briar.GROUP_ID", TestUtils.getRandomId());
		conversationActivity = Robolectric.buildActivity(TestConversationActivity.class,
				intent).create().start().resume().get();
		spyConversationActivity = Mockito.spy(conversationActivity);
		panicMenuItem = new RoboMenuItem(R.id.action_panic);
		textInputView = conversationActivity.findViewById(R.id.text_input_container);
	}

	@Test
	public void testPanicSent() {
		//Send panic alert
		conversationActivity.onOptionsItemSelected(panicMenuItem);
		//Ensure panic is sent
		Assert.assertEquals("PANIC", conversationActivity.lastAction);
	}

	@Test
	public void testPanicReceived() {
		//Send panic alert
//		MenuItem panicMenuItem = new RoboMenuItem(panicMenuItem);
		//Ensure panic is received
	}

	@Test
	public void testDownload(){
		// enter a sample URL into the text entry field and send the message
		String url = "https://www.google.com";
		textInputView.setText(url);
		conversationActivity.onSendClick(url);

		// assert that the text entry field is now empty, i.e. the message was sent
		assertEquals("", textInputView.getText().toString().trim());
	}

  @Test
	public void testSetContactMutedOrUnMuted() throws DbException {
		spyConversationActivity.setContactMutedOrUnMuted();

		Mockito.verify(spyConversationActivity).setContactMutedOrUnMuted();
	}

	@Test
	public void testContactUnMute() {
		AndroidNotificationManager mockNotificationManager = Mockito.mock(AndroidNotificationManager.class);
		conversationActivity.setNotificationManager(mockNotificationManager);
		conversationActivity.isMuted = false;

		conversationActivity.muteOrUnMuteContact();

		Mockito.verify(mockNotificationManager).unblockContactNotification(any(ContactId.class));
	}

	@Test
	public void testContactMute() {
		AndroidNotificationManager mockNotificationManager = Mockito.mock(AndroidNotificationManager.class);
		conversationActivity.setNotificationManager(mockNotificationManager);
		conversationActivity.isMuted = true;

		conversationActivity.muteOrUnMuteContact();

		Mockito.verify(mockNotificationManager).blockContactNotification(any(ContactId.class));
	}
}