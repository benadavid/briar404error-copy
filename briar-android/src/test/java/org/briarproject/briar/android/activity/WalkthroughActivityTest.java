package org.briarproject.briar.android.activity;

import android.content.Intent;
import android.graphics.Color;

import org.briarproject.bramble.test.TestUtils;
import org.briarproject.briar.android.TestBriarApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by Gibran on 2018-02-11.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class WalkthroughActivityTest {

	private TestWalkthroughActivity walkthroughActivity;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Intent intent = new Intent();
		intent.putExtra("briar.GROUP_ID", TestUtils.getRandomId());
		walkthroughActivity = Robolectric.buildActivity(TestWalkthroughActivity.class,
				intent).create().start().resume().get();
	}

	@Test
	public void testChangeStatusBarColor() {
		walkthroughActivity.changeStatusBarColor();
		Assert.assertEquals(Color.TRANSPARENT, walkthroughActivity.getWindow().getStatusBarColor());
	}
}