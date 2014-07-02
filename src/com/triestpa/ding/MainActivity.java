package com.triestpa.ding;

import java.util.Locale;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

public class MainActivity extends ActionBarActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		ParseInstallation.getCurrentInstallation().saveInBackground();
		PushService.subscribe(this, "Patrick", MainActivity.class);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PictureFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			}
			return null;
		}
	}

	public static class PictureFragment extends Fragment {
		Spring mSpring;
		ImageView profPic;

		long lastDing = 0;
		long currentTime;

		private static final String ARG_SECTION_NUMBER = "section_number";

		public static PictureFragment newInstance(int sectionNumber) {
			PictureFragment fragment = new PictureFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PictureFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			int section = getArguments().getInt(ARG_SECTION_NUMBER);

			profPic = (ImageView) rootView.findViewById(R.id.picture);

			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB) {
				profPic.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							mSpring.setEndValue(.5);
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							currentTime = System.currentTimeMillis();
							if (lastDing == 0 || (lastDing + 60000) < currentTime) {
								ParsePush push = new ParsePush();
								push.setChannel("Patrick");
								push.setMessage("Ding!");
								push.sendInBackground();
								lastDing = currentTime;

							} else {
								Toast.makeText(getActivity(), "Wait a minute please",
										Toast.LENGTH_SHORT).show();
							}
							mSpring.setEndValue(1);

						}
						return true;
					}
				});

				springInit();
			}
			return rootView;
		}

		public void springInit() {
			SpringSystem springSystem = SpringSystem.create();
			mSpring = springSystem.createSpring();
			mSpring.setCurrentValue(1);
			mSpring.setSpringConfig(new SpringConfig(200, 6));
			mSpring.addListener(new SimpleSpringListener() {
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
				public void onSpringUpdate(Spring spring) {
					float value = (float) spring.getCurrentValue();
					float scale = .5f + (value * .5f);

					profPic.setScaleX(scale);
					profPic.setScaleY(scale);
				}
			});
		}
	}

}
