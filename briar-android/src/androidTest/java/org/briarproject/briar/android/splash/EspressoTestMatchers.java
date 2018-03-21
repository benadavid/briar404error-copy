package org.briarproject.briar.android.splash;

import android.view.View;

import org.hamcrest.Matcher;

/**
 * Created by David on 2018-03-21.
 */

public class EspressoTestMatchers {

	public static Matcher<View> hasDrawable() {
		return new DrawableMatcher(DrawableMatcher.ANY);
	}
}
