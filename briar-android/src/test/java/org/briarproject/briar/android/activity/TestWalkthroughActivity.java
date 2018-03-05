package org.briarproject.briar.android.activity;

import android.os.Bundle;

import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.controller.BriarController;
import org.briarproject.briar.android.controller.BriarControllerImpl;
import org.mockito.Mockito;
import org.briarproject.briar.android.activity.ActivityModule;


import javax.annotation.Nullable;

/**
 * Created by Gibran on 2018-02-11.
 */
@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class TestWalkthroughActivity extends WalkthroughActivity {

	@Override
	public void onCreate(@Nullable Bundle state) {
		setTheme(R.style.BriarTheme_NoActionBar);
		super.onCreate(state);
	}
}
