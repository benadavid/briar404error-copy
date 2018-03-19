package org.briarproject.briar.android.contact;

import android.content.Intent;
import android.view.MenuItem;

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

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Intent intent = new Intent();
		intent.putExtra("briar.GROUP_ID", TestUtils.getRandomId());
		conversationActivity = Robolectric.buildActivity(TestConversationActivity.class,
				intent).create().start().resume().get();
		spyConversationActivity = Mockito.spy(conversationActivity);
		panicMenuItem = new RoboMenuItem(R.id.action_panic);
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