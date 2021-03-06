package org.briarproject.briar.android.ConversationActivity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.briarproject.briar.R;
import org.briarproject.briar.android.splash.SplashScreenActivity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
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
public class DeleteConvoEspressoTest {

	@Rule
	public ActivityTestRule<SplashScreenActivity> mActivityTestRule =
			new ActivityTestRule<>(SplashScreenActivity.class);

	@Test
	public void deleteConvoEspressoTest() {
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

		ViewInteraction appCompatButton41 = onView(
				allOf(withId(R.id.button), withText("Allow Connections"),
						childAtPosition(
								allOf(withId(R.id.dozeView),
										childAtPosition(
												withClassName(
														is("android.support.constraint.ConstraintLayout")),
												0)),
								2)));
		appCompatButton41.perform(scrollTo(), click());

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton51 = onView(
				allOf(withId(R.id.next), withText("Create Account"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.widget.ScrollView")),
										0),
								2)));
		appCompatButton51.perform(scrollTo(), click());

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

		ViewInteraction navigationMenuItemView = onView(
				childAtPosition(
						allOf(withId(R.id.design_navigation_view),
								childAtPosition(
										withId(R.id.navigation),
										0)),
						5));
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

		ViewInteraction recyclerView2 = onView(
				allOf(withId(R.id.recyclerView),
						childAtPosition(
								withClassName(
										is("android.widget.RelativeLayout")),
								0)));
		recyclerView2.perform(actionOnItemAtPosition(0, click()));

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		openActionBarOverflowOrOptionsMenu(
				getInstrumentation().getTargetContext());

		ViewInteraction textView = onView(
				allOf(withId(R.id.title), withText("Delete Conversation"),
						childAtPosition(
								childAtPosition(
										IsInstanceOf.<View>instanceOf(
												android.widget.LinearLayout.class),
										0),
								0),
						isDisplayed()));
		textView.check(matches(withText("Delete Conversation")));

		ViewInteraction linearLayout = onView(
				allOf(childAtPosition(
						childAtPosition(
								IsInstanceOf.<View>instanceOf(
										android.widget.FrameLayout.class),
								0),
						3),
						isDisplayed()));
		linearLayout.check(matches(isDisplayed()));

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatTextView = onView(
				allOf(withId(R.id.title), withText("Delete Conversation"),
						childAtPosition(
								childAtPosition(
										withClassName(
												is("android.support.v7.view.menu.ListMenuItemView")),
										0),
								0),
						isDisplayed()));
		appCompatTextView.perform(click());

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Added a sleep statement to match the app's execution delay.
		// The recommended way to handle such scenarios is to use Espresso idling resources:
		// https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		ViewInteraction appCompatButton7 = onView(
				allOf(withId(android.R.id.button2), withText("Delete"),
						childAtPosition(
								childAtPosition(
										withId(R.id.buttonPanel),
										0),
								2)));
		appCompatButton7.perform(scrollTo(), click());

		//checks if messages are deleted and displays default lack of conversation message
		ViewInteraction textView2 = onView(allOf(withId(R.id.emptyView),
				withText("This is the conversation view.  There seems to be a lack of conversation.  " +
						"Just tap the input field at the bottom to start a conversation."),
						childAtPosition(
								childAtPosition(
										withId(R.id.conversationView),
										0),
								0),
						isDisplayed()));
		textView2.check(matches(withText("This is the conversation view.  There seems to be a lack of conversation.  " +
				"Just tap the input field at the bottom to start a conversation.")));
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
