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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AvatarEspressoTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Test
	public void avatarEspressoTest() {
		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(60000);
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
			Thread.sleep(3476779);
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
				.perform(scrollTo(), replaceText("rajee"), closeSoftKeyboard());

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
		textInputEditText2.perform(scrollTo(), replaceText("pasword"),
				closeSoftKeyboard());

		ViewInteraction textInputEditText3 = onView(
				allOf(withId(R.id.password_confirm),
						childAtPosition(
								childAtPosition(
										withId(R.id.password_confirm_wrapper),
										0),
								0)));
		textInputEditText3.perform(scrollTo(), replaceText("pasword"),
				closeSoftKeyboard());

		ViewInteraction appCompatButton3 = onView(
				allOf(withId(R.id.next), withText("Create Account"),
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
			Thread.sleep(3533486);
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

		ViewInteraction imageButton = onView(
				allOf(withId(R.id.imageButton1),
						childAtPosition(
								allOf(withId(R.id.navigation),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.LinearLayout.class),
												0)),
								1),
						isDisplayed()));
		imageButton.check(matches(isDisplayed()));

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
			Thread.sleep(3544460);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction imageButton2 = onView(
				allOf(withId(R.id.AvatarButton),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.view.ViewGroup.class),
										0),
								0),
						isDisplayed()));
		imageButton2.check(matches(isDisplayed()));

		ViewInteraction imageView = onView(
				allOf(withId(R.id.imageView3),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.view.ViewGroup.class),
										0),
								1),
						isDisplayed()));
		imageView.check(matches(isDisplayed()));

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
			Thread.sleep(3547929);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction button = onView(
				allOf(withId(R.id.button2),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.FrameLayout.class),
										0),
								0),
						isDisplayed()));
		button.check(matches(isDisplayed()));

		ViewInteraction button2 = onView(
				allOf(withId(R.id.button3),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.FrameLayout.class),
										0),
								1),
						isDisplayed()));
		button2.check(matches(isDisplayed()));

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
			Thread.sleep(3560366);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction imageView2 = onView(
				allOf(withId(R.id.imageView2),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.FrameLayout.class),
										0),
								2),
						isDisplayed()));
		imageView2.check(matches(isDisplayed()));

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

		pressBack();

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3556277);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction imageButton3 = onView(
				allOf(withId(R.id.AvatarButton),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.view.ViewGroup.class),
										0),
								0),
						isDisplayed()));
		imageButton3.check(matches(isDisplayed()));

		pressBack();

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(3579197);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction imageButton4 = onView(
				allOf(withId(R.id.imageButton1),
						childAtPosition(
								allOf(withId(R.id.navigation),
										childAtPosition(
												IsInstanceOf.<View>instanceOf(
														android.widget.LinearLayout.class),
												0)),
								1),
						isDisplayed()));
		imageButton4.check(matches(isDisplayed()));

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
