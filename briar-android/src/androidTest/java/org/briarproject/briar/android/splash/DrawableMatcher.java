package org.briarproject.briar.android.splash;

import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by David on 2018-03-21.
 */

public class DrawableMatcher extends TypeSafeMatcher<View> {
	static final int EMPTY = -1;
	static final int ANY = -2;
	private final int expectedId;

	public DrawableMatcher(int resourceId) {
		super(View.class);
		this.expectedId = resourceId;
	}

	@Override
	protected boolean matchesSafely(View target) {
		boolean answer=false;
		if (!(target instanceof ImageView)){
			answer= false;
		}
		ImageView imageView = (ImageView) target;
		if (expectedId == EMPTY){
			answer=false;
			return imageView.getDrawable() == null;
		}
		if (expectedId == ANY){
			answer=true;
			return imageView.getDrawable() != null;
		}
		return answer;
	}

	@Override
	public void describeTo(Description description) {

	}
}
