package org.briarproject.briar.android.navdrawer;

import android.os.Bundle;

import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityModule;
import org.briarproject.briar.android.activity.BaseActivity;
import org.briarproject.briar.android.controller.BriarController;
import org.briarproject.briar.android.controller.BriarControllerImpl;
import org.briarproject.briar.android.threaded.ThreadItemAdapter;
import org.mockito.Mockito;

import javax.annotation.Nullable;

/**
 * Created by Gibran on 2018-02-11.
 */
@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class TestNavDrawerActivity extends NavDrawerActivity{

	@Override
	public void onCreate(@Nullable Bundle state) {
		setTheme(R.style.BriarTheme_NoActionBar);
		super.onCreate(state);
	}

	@Override
	protected ActivityModule getActivityModule() {
		return new ActivityModule(this) {

			@Override
			protected BriarController provideBriarController(
					BriarControllerImpl briarController) {
				BriarController c = Mockito.mock(BriarController.class);
				Mockito.when(c.hasEncryptionKey()).thenReturn(true);
				return c;
			}

		};
	}
}

