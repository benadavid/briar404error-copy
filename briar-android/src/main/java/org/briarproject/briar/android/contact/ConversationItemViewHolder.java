package org.briarproject.briar.android.contact;

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

@UiThread
@NotNullByDefault
class ConversationItemViewHolder extends ViewHolder {

	protected final ViewGroup layout;
	private final TextView text;
	private final TextView time;
	private boolean bold;
	private boolean italic;

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

			bold = item.isBold();
			italic = item.isItalic();

			if(bold == false && italic == false) {
				text.setTypeface(text.getTypeface(), Typeface.NORMAL);
			}
			else if(bold == true && italic == false) {
				text.setTypeface(text.getTypeface(), Typeface.BOLD);
			}
			else if(bold == false && italic == true) {
				text.setTypeface(text.getTypeface(), Typeface.ITALIC);
			}
			else if(bold == true && italic == true) {
				text.setTypeface(text.getTypeface(), Typeface.BOLD_ITALIC);
			}
		}

		long timestamp = item.getTime();
		time.setText(UiUtils.formatDate(time.getContext(), timestamp));
	}

}
