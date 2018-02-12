package org.briarproject.briar.android.activity;

import org.briarproject.briar.R;
import org.briarproject.briar.android.login.SetupActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WalkthroughActivity extends AppCompatActivity {

	private IntroManager introManager;
	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;
	private LinearLayout dotsLayout;
	private TextView[] dots;
	private int[] layouts;
	private Button skip;
	private Button next;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		introManager = new IntroManager(this);

		// Check if opening for first time, if not open setup screen
		if (!introManager.check())
		{
			launchSetup();
			finish();
		}

		// Make notification bar transparent
		if (Build.VERSION.SDK_INT >= 21)
		{
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}

		setContentView(R.layout.activity_walkthrough_start);

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
		skip = (Button) findViewById(R.id.btn_skip);
		next = (Button) findViewById(R.id.btn_next);


		// add walkthrough screens to layouts array
		layouts = new int[]{R.layout.activity_walkthrough1, R.layout.activity_walkthrough2,};

		// add bottom dots
		addBottomDots(0);

		// make notification bar transparent
		changeStatusBarColor();

		viewPagerAdapter = new ViewPagerAdapter();
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

		//skip button to go to setup screen
		skip.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				launchSetup();
			}
		});

		//next button
		next.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int current = getItem(+1);

				// if not at last screen, move to next screen
				if (current < layouts.length)
				{
					viewPager.setCurrentItem(current);
				}
				// if last page setup screen will be launched
				else
					{
					launchSetup();
				}
			}
		});
	}

	private void addBottomDots(int currentPage)
	{
		dots = new TextView[layouts.length];

		int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
		int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

		dotsLayout.removeAllViews();

		for (int i = 0; i < dots.length; i++)
		{
			dots[i] = new TextView(this);
			dots[i].setText(Html.fromHtml("&#8226;"));
			dots[i].setTextSize(35);
			dots[i].setTextColor(colorsInactive[currentPage]);
			dotsLayout.addView(dots[i]);
		}

		if (dots.length > 0)
		{
			dots[currentPage].setTextColor(colorsActive[currentPage]);
		}
	}

	private int getItem(int i)
	{
		return viewPager.getCurrentItem() + i;
	}

	//go to next activity which is setUp
	private void launchSetup()
	{
		introManager.setFirst(false);
		startActivity(new Intent(WalkthroughActivity.this, SetupActivity.class));
		finish();
	}

	//  viewpager change listener
	ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			addBottomDots(position);

			// change the next button text if its last page
			if (position == layouts.length - 1)
			{
				next.setText(getString(R.string.start));
				skip.setVisibility(View.GONE);
			}
			else
				{
				next.setText(getString(R.string.next));
				skip.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	//Making notification bar transparent
	protected void changeStatusBarColor()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}

	public class ViewPagerAdapter extends PagerAdapter
	{
		private LayoutInflater layoutInflater;

		@Override
		public Object instantiateItem(ViewGroup container, int position)
		{
			layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(layouts[position], container, false);
			container.addView(view);
			return view;
		}

		@Override
		public int getCount()
		{
			return layouts.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj)
		{
			return view == obj;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object)
		{
			View view = (View) object;
			container.removeView(view);
		}
	}
}