package org.briarproject.briar.android.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.identity.IdentityManager;
import org.briarproject.bramble.api.identity.LocalAuthor;
import org.briarproject.briar.R;
import org.thoughtcrime.securesms.components.KeyboardAwareLinearLayout;
import org.thoughtcrime.securesms.components.emoji.EmojiDrawer;
import org.thoughtcrime.securesms.components.emoji.EmojiDrawer.EmojiEventListener;
import org.thoughtcrime.securesms.components.emoji.EmojiEditText;
import org.thoughtcrime.securesms.components.emoji.EmojiToggle;

import javax.annotation.Nullable;
import javax.inject.Inject;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.KeyEvent.KEYCODE_BACK;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

@UiThread
public class TextInputView extends KeyboardAwareLinearLayout
		implements EmojiEventListener {

	protected final ViewHolder ui;
	protected TextInputListener listener;
	protected String colour = "NCL";
	public boolean pinned = false;

    public TextInputView(Context context) {
		this(context, null);
	}

	public TextInputView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TextInputView(Context context, @Nullable AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOrientation(VERTICAL);
		setLayoutTransition(new LayoutTransition());

		inflateLayout(context);
		ui = new ViewHolder();
		setUpViews(context, attrs);
	}

	protected void inflateLayout(Context context) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.text_input_view, this, true);
	}

	@CallSuper
	protected void setUpViews(Context context, @Nullable AttributeSet attrs) {
		// get attributes
		TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.TextInputView);
		String hint = attributes.getString(R.styleable.TextInputView_hint);
		attributes.recycle();

		if (hint != null) {
			ui.editText.setHint(hint);
		}

		ui.emojiToggle.attach(ui.emojiDrawer);
		ui.emojiToggle.setOnClickListener(v -> onEmojiToggleClicked());
		ui.editText.setOnClickListener(v -> showSoftKeyboard());
		ui.editText.setOnKeyListener((v, keyCode, event) -> {
			if (keyCode == KEYCODE_BACK && isEmojiDrawerOpen()) {
				hideEmojiDrawer();
				return true;
			}
			if (keyCode == KEYCODE_ENTER && event.isCtrlPressed()) {
				trySendMessage();
				return true;
			}
			return false;
		});
		ui.None.setOnClickListener(v -> NoColour());
		ui.Red.setOnClickListener(v -> RedText());
		ui.Yellow.setOnClickListener(v -> YellowText());
		ui.Green.setOnClickListener(v -> GreenText());
		ui.Cyan.setOnClickListener(v -> CyanText());
		ui.Blue.setOnClickListener(v -> BlueText());
		ui.Magenta.setOnClickListener(v -> MagentaText());
		ui.Grey.setOnClickListener(v -> GreyText());
		ui.Black.setOnClickListener(v -> BlackText());
		ui.Pinned.setOnClickListener(v -> TogglePin());
		ui.sendButton.setOnClickListener(v -> trySendMessage());
		ui.emojiDrawer.setEmojiEventListener(this);
	}

	private void TogglePin() {
		if(ui.Pinned.isChecked()){
			pinned = true;
		}
		if(!ui.Pinned.isChecked()){
			pinned = false;
		}
	}

	private void trySendMessage() {
		if (listener != null) {
			listener.onSendClick(getText());
		}
	}

	@Override
	public void setVisibility(int visibility) {
		if (visibility == GONE && isKeyboardOpen()) {

			onKeyboardClose();
		}
		super.setVisibility(visibility);
	}

	@Override
	public void onKeyEvent(KeyEvent keyEvent) {
		ui.editText.dispatchKeyEvent(keyEvent);
	}

	@Override
	public void onEmojiSelected(String emoji) {
		ui.editText.insertEmoji(emoji);
	}

	@Override
	public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
		return ui.editText.requestFocus(direction, previouslyFocusedRect);
	}

	private void onEmojiToggleClicked() {
		if (isEmojiDrawerOpen()) {
			showSoftKeyboard();
		} else {
			showEmojiDrawer();
		}
	}

	private void NoColour() { colour = "NCL"; }

	private void RedText() { colour = "RED"; }

	private void YellowText() { colour = "YLW"; }

	private void GreenText() { colour = "GRN"; }

	private void CyanText() { colour = "CYN"; }

	private void BlueText() { colour = "BLU"; }

	private void MagentaText() { colour = "MGN"; }

	private void GreyText() { colour = "GRY"; }

	private void BlackText() { colour = "BLK"; }

	public void setText(String text) {
		ui.editText.setText(text);
	}

	public String getText() {
		String text = ((ui.editText.getText()).append(colour)).toString();
		colour = "NCL";
		return text;
	}

	public String getBlogText() {
		return ui.editText.getText().toString();
	}

	public void setHint(@StringRes int res) {
		ui.editText.setHint(res);
	}

	public void setSendButtonEnabled(boolean enabled) {
		ui.sendButton.setEnabled(enabled);
	}

	public void addTextChangedListener(TextWatcher watcher) {
		ui.editText.addTextChangedListener(watcher);
	}

	public void setListener(TextInputListener listener) {
		this.listener = listener;
	}

	public void showSoftKeyboard() {
		if (isKeyboardOpen()) return;

		if (ui.emojiDrawer.isShowing()) {
			postOnKeyboardOpen(this::hideEmojiDrawer);
		}
		ui.editText.post(() -> {
			ui.editText.requestFocus();
			InputMethodManager imm = (InputMethodManager)
					getContext().getSystemService(INPUT_METHOD_SERVICE);
			imm.showSoftInput(ui.editText, SHOW_IMPLICIT);
		});
	}

	public void hideSoftKeyboard() {
		IBinder token = ui.editText.getWindowToken();
		Object o = getContext().getSystemService(INPUT_METHOD_SERVICE);
		((InputMethodManager) o).hideSoftInputFromWindow(token, 0);
	}

	public void showEmojiDrawer() {
		if (isKeyboardOpen()) {
			postOnKeyboardClose(() -> ui.emojiDrawer.show(getKeyboardHeight()));
			hideSoftKeyboard();
		} else {
			ui.emojiDrawer.show(getKeyboardHeight());
			ui.editText.requestFocus();
		}
	}

	public void hideEmojiDrawer() {
		ui.emojiDrawer.hide();
	}

	public boolean isEmojiDrawerOpen() {
		return ui.emojiDrawer.isShowing();
	}

	protected class ViewHolder {

		private final EmojiToggle emojiToggle;
		final EmojiEditText editText;
		final View sendButton;
		final EmojiDrawer emojiDrawer;
		final View None;
		final View Red;
		final View Yellow;
		final View Green;
		final View Cyan;
		final View Blue;
		final View Magenta;
		final View Grey;
		final View Black;
		final CheckBox Pinned;

		private ViewHolder() {
			emojiToggle = findViewById(R.id.emoji_toggle);
			editText = findViewById(R.id.input_text);
			emojiDrawer = findViewById(R.id.emoji_drawer);
			sendButton = findViewById(R.id.btn_send);
			None = findViewById(R.id.none);
			Red = findViewById(R.id.red);
			Yellow = findViewById(R.id.yellow);
			Green = findViewById(R.id.green);
			Cyan = findViewById(R.id.cyan);
			Blue = findViewById(R.id.blue);
			Magenta = findViewById(R.id.magenta);
			Grey = findViewById(R.id.grey);
			Black = findViewById(R.id.black);
			Pinned = findViewById(R.id.pin);
		}
	}

	public interface TextInputListener {
		void onSendClick(String text);
	}

}