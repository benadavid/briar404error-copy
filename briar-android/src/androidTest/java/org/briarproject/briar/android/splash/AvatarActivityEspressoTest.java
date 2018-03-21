package org.briarproject.briar.android.splash;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.briarproject.briar.R;
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
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

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

		ViewInteraction appCompatButton = onView(
				allOf(withId(R.id.btn_skip), withText("SKIP"),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										0),
								4),
						isDisplayed()));
		appCompatButton.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction textInputEditText = onView(
				allOf(withId(R.id.nickname_entry),
						childAtPosition(
								childAtPosition(
										withId(R.id.nickname_entry_wrapper),
										0),
								0)));
		textInputEditText
				.perform(scrollTo(), replaceText("David"), closeSoftKeyboard());

		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.next), withText("Next"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								1)));
		appCompatButton2.perform(scrollTo(), click());

		ViewInteraction textInputEditText2 = onView(
				allOf(withId(R.id.password_entry),
						childAtPosition(
								childAtPosition(
										withId(R.id.password_entry_wrapper),
										0),
								0)));
		textInputEditText2.perform(scrollTo(), replaceText("david32"),
				closeSoftKeyboard());

		ViewInteraction textInputEditText3 = onView(
				allOf(withId(R.id.password_confirm),
						childAtPosition(
								childAtPosition(
										withId(R.id.password_confirm_wrapper),
										0),
								0)));
		textInputEditText3.perform(scrollTo(), replaceText("david32"),
				closeSoftKeyboard());

		ViewInteraction appCompatButton3 = onView(
				allOf(withId(R.id.next), withText("Next"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								3)));
		appCompatButton3.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatImageButton = onView(
				allOf(withContentDescription("Open the navigation drawer"),
						childAtPosition(
								allOf(withId(R.id.toolbar),
										childAtPosition(
												withClassName(
														is("android.support.design.widget.AppBarLayout")),
												0)),
								1),
						isDisplayed()));
		appCompatImageButton.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton4 = onView(
				allOf(withId(R.id.showcase_button), withText("Close"),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										1),
								0),
						isDisplayed()));
		appCompatButton4.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton5 = onView(
				allOf(withId(R.id.showcase_button), withText("Close"),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										1),
								0),
						isDisplayed()));
		appCompatButton5.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton6 = onView(
				allOf(withId(R.id.showcase_button), withText("Close"),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										1),
								0),
						isDisplayed()));
		appCompatButton6.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatImageButton2 = onView(
				allOf(withId(R.id.imageButton1),
						childAtPosition(
								allOf(withId(R.id.navigation),
										childAtPosition(
												withClassName(
														is("android.widget.LinearLayout")),
												0)),
								1)));
		appCompatImageButton2.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatImageButton3 = onView(
				allOf(withId(R.id.AvatarButton),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.support.constraint.ConstraintLayout")),
										0),
								2),
						isDisplayed()));
		appCompatImageButton3.perform(click());

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
