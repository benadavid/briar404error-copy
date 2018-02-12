package org.briarproject.briar.android.navdrawer;

import android.content.Intent;

import org.briarproject.bramble.test.TestUtils;
import org.briarproject.briar.android.TestBriarApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by Gibran on 2018-02-11.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class NavDrawerActivityTest {

	private TestNavDrawerActivity navDrawerActivity;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Intent intent = new Intent();
		intent.putExtra("briar.GROUP_ID", TestUtils.getRandomId());
		navDrawerActivity = Robolectric.buildActivity(TestNavDrawerActivity.class,
				intent).create().start().resume().get();
	}

	@Test
	public void stuff(){
		System.out.println("Meow");
	}
}