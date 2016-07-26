package org.briarproject.android.blogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.briarproject.R;
import org.briarproject.android.ActivityComponent;
import org.briarproject.android.BriarActivity;
import org.briarproject.android.blogs.RssFeedAdapter.RssFeedListener;
import org.briarproject.android.util.BriarRecyclerView;
import org.briarproject.api.db.DbException;
import org.briarproject.api.feed.Feed;
import org.briarproject.api.feed.FeedManager;
import org.briarproject.api.sync.GroupId;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.v4.app.ActivityOptionsCompat.makeCustomAnimation;
import static java.util.logging.Level.WARNING;

public class RssFeedManageActivity extends BriarActivity
		implements RssFeedListener {

	private static final Logger LOG =
			Logger.getLogger(RssFeedManageActivity.class.getName());

	private BriarRecyclerView list;
	private RssFeedAdapter adapter;

	// Fields that are accessed from background threads must be volatile
	private volatile GroupId groupId = null;
	@Inject
	@SuppressWarnings("WeakerAccess")
	volatile FeedManager feedManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// GroupId from Intent
		Intent i = getIntent();
		byte[] b = i.getByteArrayExtra(GROUP_ID);
		if (b == null) throw new IllegalStateException("No Group in intent.");
		groupId = new GroupId(b);

		setContentView(R.layout.activity_rss_feed_manage);

		adapter = new RssFeedAdapter(this, this);

		list = (BriarRecyclerView) findViewById(R.id.feedList);
		list.setLayoutManager(new LinearLayoutManager(this));
		list.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadFeeds();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.rss_feed_manage_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_rss_feeds_import:
				Intent i =
						new Intent(this, RssFeedImportActivity.class);
				i.putExtra(GROUP_ID, groupId.getBytes());
				ActivityOptionsCompat options =
						makeCustomAnimation(this, android.R.anim.slide_in_left,
								android.R.anim.slide_out_right);
				ActivityCompat.startActivity(this, i, options.toBundle());
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public void onDeleteClick(final Feed feed) {
		runOnDbThread(new Runnable() {
			@Override
			public void run() {
				try {
					feedManager.removeFeed(feed.getUrl());
					onFeedDeleted(feed);
				} catch (DbException e) {
					if (LOG.isLoggable(WARNING))
						LOG.log(WARNING, e.toString(), e);
					onDeleteError();
				}
			}
		});
	}

	private void loadFeeds() {
		runOnDbThread(new Runnable() {
			@Override
			public void run() {
				try {
					addFeeds(feedManager.getFeeds());
				} catch (DbException e) {
					if (LOG.isLoggable(WARNING))
						LOG.log(WARNING, e.toString(), e);
					list.setEmptyText(R.string.blogs_rss_feeds_manage_error);
					list.showData();
				}
			}
		});
	}

	private void addFeeds(final List<Feed> feeds) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (feeds.size() == 0) list.showData();
				else adapter.addAll(feeds);
			}
		});
	}

	private void onFeedDeleted(final Feed feed) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.remove(feed);
			}
		});
	}

	private void onDeleteError() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Snackbar.make(list,
						R.string.blogs_rss_feeds_manage_delete_error,
						LENGTH_LONG).show();
			}
		});
	}
}

