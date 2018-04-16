package org.briarproject.briar.android.WifiConversation;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import org.briarproject.briar.R;
import org.briarproject.briar.android.contact.WifiConversation;
import org.briarproject.briar.android.splash.SplashScreenActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class WifiConversationEspressoTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Test
	public void wifiChatEspressoTest() {
		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
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
			Thread.sleep(5000);
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
				.perform(scrollTo(), replaceText("user"), closeSoftKeyboard());

		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.next), withText("Next"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								1)));
		appCompatButton2.perform(scrollTo(), click());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction textInputEditText4 = onView(
				allOf(withId(R.id.password_entry),
						childAtPosition(
								childAtPosition(
										withId(R.id.password_entry_wrapper),
										0),
								0)));
		textInputEditText4.perform(scrollTo(), replaceText("password"),
				closeSoftKeyboard());

		ViewInteraction textInputEditText5 = onView(
				allOf(withId(R.id.password_confirm),
						childAtPosition(
								childAtPosition(
										withId(R.id.password_confirm_wrapper),
										0),
								0)));
		textInputEditText5.perform(scrollTo(), replaceText("password"),
				closeSoftKeyboard());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton3 = onView(
				allOf(withId(R.id.next), withText("Next"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								3)));
		appCompatButton3.perform(scrollTo(), click());

		ViewInteraction appCompatButton4 = onView(
				allOf(withId(R.id.button), withText("Allow Connections"),
						childAtPosition(
								allOf(withId(R.id.dozeView),
										childAtPosition(
												withClassName(
														is("android.support.constraint.ConstraintLayout")),
												0)),
								2)));
		appCompatButton4.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton5 = onView(
				allOf(withId(R.id.next), withText("Create Account"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								2)));
		appCompatButton5.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
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
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton7 = onView(
				allOf(withId(R.id.showcase_button), withText("Close"),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										1),
								0),
						isDisplayed()));
		appCompatButton7.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton8 = onView(
				allOf(withId(R.id.showcase_button), withText("Close"),
						childAtPosition(
								childAtPosition(
										withId(android.R.id.content),
										1),
								0),
						isDisplayed()));
		appCompatButton8.perform(click());

		ViewInteraction navigationMenuItemView = onView(
				childAtPosition(
						allOf(withId(R.id.design_navigation_view),
								childAtPosition(
										withId(R.id.navigation),
										0)),
						6));
		navigationMenuItemView.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.list),
						childAtPosition(
								withId(android.R.id.list_container),
								0)));
		recyclerView.perform(actionOnItemAtPosition(21, click()));

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatImageButton2 = onView(
				allOf(withContentDescription("Open the navigation drawer"),
						childAtPosition(
								allOf(withId(R.id.toolbar),
										childAtPosition(
												withClassName(
														is("android.support.design.widget.AppBarLayout")),
												0)),
								1),
						isDisplayed()));
		appCompatImageButton2.perform(click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction checkedTextView = onView(
				allOf(withId(R.id.design_menu_item_text),
						childAtPosition(
								childAtPosition(
										withId(R.id.design_navigation_view),
										2),
								0),
						isDisplayed()));
		checkedTextView.check(matches(isDisplayed()));

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction navigationMenuItemView2 = onView(
				childAtPosition(
						allOf(withId(R.id.design_navigation_view),
								childAtPosition(
										withId(R.id.navigation),
										0)),
						2));
		navigationMenuItemView2.perform(scrollTo(), click());

		//---->> Assert wifi chat is clicked

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction recyclerView2 = onView(
				allOf(withId(R.id.recyclerView),
						childAtPosition(
								withClassName(
										is("android.widget.RelativeLayout")),
								0)));
		recyclerView2.perform(actionOnItemAtPosition(0, click()));

		//---> Assert that WifiConversation gets called after Selecting a contact


        intended(hasComponent(WifiConversation.class.getName()));


		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction imageView = onView(
				allOf(withId(R.id.sendButton),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												LinearLayout.class),
										1),
								1),
						isDisplayed()));
		imageView.check(matches(isDisplayed()));

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatEditText = onView(
				allOf(withId(R.id.messageArea),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.LinearLayout")),
										1),
								0),
						isDisplayed()));
		appCompatEditText.perform(replaceText("hi"), closeSoftKeyboard());

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatImageView = onView(
				allOf(withId(R.id.sendButton),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.LinearLayout")),
										1),
								1),
						isDisplayed()));
		appCompatImageView.perform(click());

		//---> Assert send button

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction textView = onView(
				allOf(withText("You:-\nhi"),
						childAtPosition(
								allOf(withId(R.id.layout1),
										childAtPosition(
												withId(R.id.layout2),
												0)),
								0),
						isDisplayed()));
		textView.check(matches(withText("You:- hi")));

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
