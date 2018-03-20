package org.briarproject.briar.android.threaded;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.commonsware.cwac.anddown.AndDown;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.threaded.ThreadItemAdapter.ThreadItemListener;
import org.briarproject.briar.android.view.AuthorView;

@UiThread
@NotNullByDefault
public abstract class BaseThreadItemViewHolder<I extends ThreadItem>
		extends RecyclerView.ViewHolder {

	private final static int ANIMATION_DURATION = 5000;

	protected final TextView textView;
	private final ViewGroup layout;
	private final AuthorView author;
	private String colour;

	public BaseThreadItemViewHolder(View v) {
		super(v);

		layout = v.findViewById(R.id.layout);
		textView = v.findViewById(R.id.text);
		author = v.findViewById(R.id.author);
	}

	@CallSuper
	public void bind(I item, ThreadItemListener<I> listener) {
			//Get the colour value from the message
			colour = item.getText().substring((item.getText().length() -3), (item.getText().length()));

			switch (colour) {
				case "RED":
					textView.setTextColor(Color.RED);
					break;
				case "YLW":
					textView.setTextColor(Color.YELLOW);
					break;
				case "GRN":
					textView.setTextColor(Color.GREEN);
					break;
				case "CYN":
					textView.setTextColor(Color.CYAN);
					break;
				case "BLU":
					textView.setTextColor(Color.BLUE);
					break;
				case "MGN":
					textView.setTextColor(Color.MAGENTA);
					break;
				case "GRY":
					textView.setTextColor(Color.GRAY);
					break;
				case "BLK":
					textView.setTextColor(Color.BLACK);
					break;
				case "NCL":
						textView.setTextColor(Color.BLACK);
					break;
				default:
					break;
			}
			AndDown converter = new AndDown();
			//Remove the last 3 characters (colour characters) from the message
			String HTMLText = converter.markdownToHtml(item.getText().substring(0, (item.getText().length() -3)));
			CharSequence HTMLString = Html.fromHtml(HTMLText);
			textView.setText(HTMLString);

		author.setAuthor(item.getAuthor());
		author.setDate(item.getTimestamp());
		author.setAuthorStatus(item.getStatus());

		if (item.isHighlighted()) {
			layout.setActivated(true);
		} else if (!item.isRead()) {
			layout.setActivated(true);
			animateFadeOut();
			listener.onUnreadItemVisible(item);
		} else {
			layout.setActivated(false);
		}
	}

	private void animateFadeOut() {
		setIsRecyclable(false);
		ValueAnimator anim = new ValueAnimator();
		ColorDrawable viewColor = new ColorDrawable(ContextCompat
				.getColor(getContext(), R.color.forum_cell_highlight));
		anim.setIntValues(viewColor.getColor(), ContextCompat
				.getColor(getContext(), R.color.window_background));
		anim.setEvaluator(new ArgbEvaluator());
		anim.setInterpolator(new AccelerateInterpolator());
		anim.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				layout.setBackgroundResource(
						R.drawable.list_item_thread_background);
				layout.setActivated(false);
				setIsRecyclable(true);
			}
			@Override
			public void onAnimationCancel(Animator animation) {
			}
			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		anim.addUpdateListener(valueAnimator -> layout.setBackgroundColor(
				(Integer) valueAnimator.getAnimatedValue()));
		anim.setDuration(ANIMATION_DURATION);
		anim.start();
	}

	protected Context getContext() {
		return textView.getContext();
	}

}
