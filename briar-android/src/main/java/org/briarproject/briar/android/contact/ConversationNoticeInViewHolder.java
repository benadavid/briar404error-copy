package org.briarproject.briar.android.contact;

import android.graphics.Color;
import android.support.annotation.UiThread;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.commonsware.cwac.anddown.AndDown;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@UiThread
@NotNullByDefault
class ConversationNoticeInViewHolder extends ConversationItemViewHolder {

	private final TextView msgText;
	private String colour;

	ConversationNoticeInViewHolder(View v) {
		super(v);
		msgText = v.findViewById(R.id.msgText);
	}

	@Override
	void bind(ConversationItem conversationItem) {
		super.bind(conversationItem);

		ConversationNoticeInItem item =
				(ConversationNoticeInItem) conversationItem;

		String message = item.getMsgText();
		if (StringUtils.isNullOrEmpty(message)) {
			msgText.setVisibility(GONE);
			layout.setBackgroundResource(R.drawable.notice_in);
		} else {
			msgText.setVisibility(VISIBLE);
			colour = message.substring((message.length() -6), (message.length()-3));

			switch (colour) {
				case "RED":
					msgText.setTextColor(Color.RED);
					break;
				case "YLW":
					msgText.setTextColor(Color.YELLOW);
					break;
				case "GRN":
					msgText.setTextColor(Color.GREEN);
					break;
				case "CYN":
					msgText.setTextColor(Color.CYAN);
					break;
				case "BLU":
					msgText.setTextColor(Color.BLUE);
					break;
				case "MGN":
					msgText.setTextColor(Color.MAGENTA);
					break;
				case "GRY":
					msgText.setTextColor(Color.GRAY);
					break;
				case "BLK":
					msgText.setTextColor(Color.BLACK);
					break;
				case "NCL":
					msgText.setTextColor(Color.BLACK);
					break;
				default:
					break;
			}

			AndDown converter = new AndDown();
			String HTMLText = converter.markdownToHtml(StringUtils.trim(message.substring(0, (message.length()-6))));
			CharSequence HTMLString = Html.fromHtml(HTMLText);
			msgText.setText(HTMLString);
			layout.setBackgroundResource(R.drawable.notice_out_bottom);
		}
	}

}
