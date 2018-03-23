package org.briarproject.briar.android.splash;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.briarproject.briar.R;
import org.briarproject.briar.android.navdrawer.NavDrawerActivity;
import org.briarproject.briar.android.userprofile.AvatarActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.briarproject.briar.android.splash.EspressoTestMatchers.hasDrawable;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AvatarActivityEspressoTest {

	@Rule
	public ActivityTestRule<AvatarActivity> mActivityTestRule =
			new ActivityTestRule<>(AvatarActivity.class);

	@Test
	public void avatarActivityEspressoTest() {

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton7 = onView(
				allOf(withId(R.id.button2), withText("Select Image"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("org.briarproject.briar.android.widget.TapSafeFrameLayout")),
										0),
								0),
						isDisplayed()));
		appCompatButton7.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton8 = onView(
				allOf(withId(R.id.button3), withText("Set as Avatar"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("org.briarproject.briar.android.widget.TapSafeFrameLayout")),
										0),
								1),
						isDisplayed()));
		appCompatButton8.perform(click());

		ViewInteraction imageView = onView(
				allOf(withId(R.id.imageView2),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.FrameLayout.class),
										0),
								2),
						isDisplayed()));
		imageView.check(matches(isDisplayed()));


		//assert if imageview has the actual drawable inside
		//main testing part
		imageView.check(matches(hasDrawable()));

		pressBack();

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		pressBack();

	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText(
						"Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup &&
						parentMatcher.matches(parent)
						&&
						view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}
