package org.briarproject.briar.android.contact;

import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;

import org.briarproject.bramble.api.crypto.CryptoExecutor;
import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.bramble.api.plugin.ConnectionRegistry;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityModule;
import org.briarproject.briar.android.activity.BaseActivity;
import org.briarproject.briar.android.controller.BriarController;
import org.briarproject.briar.android.controller.BriarControllerImpl;
import org.briarproject.briar.android.threaded.ThreadItemAdapter;
import org.briarproject.briar.api.android.AndroidNotificationManager;
import org.mockito.Mockito;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

/**
 * Created by Gibran on 2018-02-11.
 */
@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class TestConversationActivity extends ConversationActivity {

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
