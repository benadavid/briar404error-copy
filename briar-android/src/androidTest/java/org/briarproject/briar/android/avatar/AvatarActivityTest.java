package org.briarproject.briar.android.avatar;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.briarproject.briar.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by David on 2018-03-15.
 */
@RunWith(AndroidJUnit4.class)
public class AvatarActivityTest {

	@Rule
	public ActivityTestRule<AvatarActivity> activityTestRule= new ActivityTestRule<AvatarActivity>(AvatarActivity.class);

	private AvatarActivity activity=null;

	@Before
	public void setUp() throws Exception {
		activity=activityTestRule.getActivity();
	}

	@Test
	public void testLaunch(){
		View view= activity.findViewById(R.id.button2);

		assertNotNull(view);
	}


	@After
	public void tearDown() throws Exception {
		activity=null;
	}

}