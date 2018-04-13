package org.briarproject.briar.android.contact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import org.briarproject.briar.android.activity.BriarActivity;

import org.briarproject.briar.R;

public class ConversationPinnedMessages extends AppCompatActivity {

	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation_pinned_messages);

	}

}
