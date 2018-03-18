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
import org.briarproject.briar.api.forum.Forum;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Map;

import static java.util.logging.Level.INFO;


/**
 * Created by Gibran on 2018-02-11.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ConversationActivityTest {

	@Spy
	private TestConversationActivity conversationActivity;
	private MenuItem panicMenuItem;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Intent intent = new Intent();
		intent.putExtra("briar.GROUP_ID", TestUtils.getRandomId());
		conversationActivity = Robolectric.buildActivity(TestConversationActivity.class,
				intent).create().start().resume().get();
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
	public void testDownload() throws DbException, FormatException{


		byte[] bt = new byte[32];
		bt[0] = 0;
		bt[1] = 1;
		bt[2] = 0;
		bt[3] = 1;
		bt[4] = 0;
		bt[5] = 1;
		bt[6] = 0;
		bt[7] = 1;
		bt[8] = 0;
		bt[9] = 1;
		bt[10] = 0;
		bt[11] = 1;
		bt[12] = 0;
		bt[13] = 1;
		bt[14] = 0;
		bt[15] = 1;
		bt[16] = 0;
		bt[17] = 1;
		bt[18] = 0;
		bt[19] = 1;
		bt[20] = 0;
		bt[21] = 1;
		bt[22] = 0;
		bt[23] = 1;
		bt[24] = 0;
		bt[25] = 1;
		bt[26] = 0;
		bt[27] = 1;
		bt[28] = 0;
		bt[29] = 1;
		bt[30] = 0;
		bt[31] = 1;

		//ContactId cId = new ContactId(1);
		AuthorId aId = new AuthorId(bt);
		Author author = new Author(aId, "ALLO",null);
		LocalAuthor localAuthor = new LocalAuthor(aId,"ALLO",null,null,123455678);


		ContactId contactId = conversationActivity.contactManager.addContact(
				author,localAuthor.getId(),
				null,123455678,true,true,true);


		Contact contact = new Contact(contactId,author,localAuthor.getId(),true,true);


		Group group = conversationActivity.messagingManager.getContactGroup(contact);


		long timestamp = 1234567890;//clock.currentTimeMillis() - num * 60 * 1000;
		String body = "http://www.atcrs.ca";

		PrivateMessage message = conversationActivity.privateMessageFactory
				.createPrivateMessage(group.getId(), timestamp, body);


		conversationActivity.messagingManager.addLocalMessage(message);
		Collection<PrivateMessageHeader> pm = conversationActivity.messagingManager.getMessageHeaders(contactId);

		//Some logic might not be good here, bit we need basically to do an
		// event to say there is a new message and if it contains a string, it
		//should pass by the download function and triggers the download panel
		PrivateMessageReceivedEvent event = new PrivateMessageReceivedEvent(pm.iterator().next(),null,null);



		conversationActivity.eventOccurred(event);

	}


}