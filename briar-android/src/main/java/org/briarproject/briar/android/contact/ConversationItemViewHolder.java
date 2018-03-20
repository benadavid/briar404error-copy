package org.briarproject.briar.android.contact;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.commonsware.cwac.anddown.AndDown;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.util.UiUtils;

import android.graphics.Color;

@UiThread
@NotNullByDefault
class ConversationItemViewHolder extends ViewHolder {

	protected final ViewGroup layout;
	private final TextView text;
	private final TextView time;

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
			//Get the colour value from the message
			String colour = item.getBody().substring((item.getBody().length() -3), (item.getBody().length()));

			AndDown converter = new AndDown();
			//Remove the last 3 characters (colour characters) from the message
			String HTMLText = converter.markdownToHtml(StringUtils.trim(item.getBody().substring(0, (item.getBody().length() -3))));
			CharSequence HTMLString = Html.fromHtml(HTMLText);
			text.setText(HTMLString);
			
			switch(colour){
				case "RED":
					text.setTextColor(Color.RED);
					break;
				case "YLW":
					text.setTextColor(Color.YELLOW);
					break;
				case "GRN":
					text.setTextColor(Color.GREEN);
					break;
				case "CYN":
					text.setTextColor(Color.CYAN);
					break;
				case "BLU":
					text.setTextColor(Color.BLUE);
					break;
				case "MGN":
					text.setTextColor(Color.MAGENTA);
					break;
				case "GRY":
					text.setTextColor(Color.GRAY);
					break;
				case "BLK":
					text.setTextColor(Color.BLACK);
					break;
				case "NCL":
					break; //no colour selected - default option is still listed as its own case since every message must have a colour value appended to it
			}

		}

		long timestamp = item.getTime();
		time.setText(UiUtils.formatDate(time.getContext(), timestamp));
	}

}
