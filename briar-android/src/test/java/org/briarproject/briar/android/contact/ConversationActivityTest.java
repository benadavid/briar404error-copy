package org.briarproject.briar.android.contact;

import android.content.Intent;
import android.view.MenuItem;
import org.briarproject.bramble.test.TestUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
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
}