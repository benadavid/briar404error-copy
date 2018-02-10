package org.briarproject.briar.android.activity;

import android.content.Context;
import android.content.SharedPreferences;

public class IntroManager {

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private Context context;

	public IntroManager(Context context)
	{
		this.context = context;
		pref = context.getSharedPreferences("first", 0);
		editor = pref.edit();
	}

	public void setFirst(boolean isFirstTime)
	{
		editor.putBoolean("check", isFirstTime);
		editor.commit();
	}

	public boolean check() {
		return pref.getBoolean("check", true);
	}
}
