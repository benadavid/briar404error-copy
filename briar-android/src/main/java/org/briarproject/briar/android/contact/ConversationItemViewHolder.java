package org.briarproject.briar.android.contact;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.util.UiUtils;
import org.briarproject.briar.android.view.TextInputView;

@UiThread
@NotNullByDefault
class ConversationItemViewHolder extends ViewHolder {

	protected final ViewGroup layout;
	private final TextView text;
	private final TextView time;
	private boolean b;
	private boolean i;

	ConversationItemViewHolder(View v) {
		super(v);
		layout = v.findViewById(R.id.layout);
		text = v.findViewById(R.id.text);
		time = v.findViewById(R.id.time);
	}


	@CallSuper
	void bind(ConversationItem item) {
		if (item.getBody() == null) {
			text.setText("\u2026");
		} else {
			text.setText((StringUtils.trim(item.getBody())));

			b = TextInputView.getBold();
			i = TextInputView.getItalic();

			System.out.println("Static Bold: " + b);
			System.out.println("Static Italic: " + i);
			if(b == false && i == false) {
				text.setTypeface(text.getTypeface(), Typeface.NORMAL);
			}
			else if(b == true && i == false) {
				text.setTypeface(text.getTypeface(), Typeface.BOLD);
			}
			else if(b == false && i == true) {
				text.setTypeface(text.getTypeface(), Typeface.ITALIC);
			}
			else if(b == true && i == true) {
				text.setTypeface(text.getTypeface(), Typeface.BOLD_ITALIC);
			}
		}

		long timestamp = item.getTime();
		time.setText(UiUtils.formatDate(time.getContext(), timestamp));
	}

}
