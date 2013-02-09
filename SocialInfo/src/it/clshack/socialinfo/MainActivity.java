package it.clshack.socialinfo;

import com.google.ads.AdView;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;

	private static ProgressDialog pDialog;
	private static final String ADMOB_ID = "a1511261dc71efe";
	private static AdView adMobView;
	private static String html;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			mViewPager.setCurrentItem(2); // go to about
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.main_title).toUpperCase();
			case 1:
				return getString(R.string.about_title).toUpperCase();
			default:
				return getString(R.string.about_title).toUpperCase();
			}
		}
	}

	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			setRetainInstance(true);
			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 1: {
				return loadSearch(inflater.inflate(R.layout.activity_search,
						container, false));
			}
			case 2:
				return loadAbout(inflater.inflate(R.layout.activity_about,
						container, false));
			default:
				return loadAbout(inflater.inflate(R.layout.activity_about,
						container, false));
			}
		}

		public View loadSearch(View search) {
			((Button) search.findViewById(R.id.search_button))
					.setOnClickListener(OnclickListenerActivity);
			if (Util.isOnline(getActivity())) {
				adMobView = new AdView(getActivity(), AdSize.BANNER, ADMOB_ID);
				((LinearLayout) search.findViewById(R.id.ads))
						.addView(adMobView);
				adMobView.loadAd(new AdRequest());
			}
			return search;
		}

		private View loadAbout(View about) {
			if (Util.isOnline(getActivity())) {
				adMobView = new AdView(getActivity(), AdSize.BANNER, ADMOB_ID);
				((LinearLayout) about.findViewById(R.id.ads))
						.addView(adMobView);
				adMobView.loadAd(new AdRequest());
			}
			return about;
		}

		View.OnClickListener OnclickListenerActivity = new View.OnClickListener() {
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.search_button:
					InputMethodManager imm = (InputMethodManager) getActivity()
							.getSystemService(Activity.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,
							0);
					cerca();
					break;
				}
			}
		};

		public void cerca() {

			Util.hideSoftKeyboard(getActivity());
			html = "";

			((EditText) getActivity().findViewById(R.id.search_info))
					.setError(null);
			if (((EditText) getActivity().findViewById(R.id.search_info))
					.getText().length() == 0) {
				((EditText) getActivity().findViewById(R.id.search_info))
						.setError(getString(R.string.error_search_info));
				return;
			}
			if (!Util.isOnline(getActivity().getApplicationContext())) {
				((TextView) getActivity().findViewById(R.id.error_text))
						.setText(getString(R.string.error_search_online));
				return;
			}
			downloadData();
		}
		public void downloadData() {
			pDialog = ProgressDialog.show(getActivity(),
					getString(R.string.title), getString(R.string.search),
					true, true);
			pDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
			Thread splashThread = new Thread() {
				@Override
				public void run() {
					try {
						html = Util.getHtml(Util
								.postData(((EditText) getActivity()
										.findViewById(R.id.search_info))
										.getText().toString()));
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						handler.sendEmptyMessage(1005);
						pDialog.dismiss();
					}
				}
			};                           
			splashThread.start();
		}
		private Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1005:
					setResult();
					break;
				}
			}
		};

		public void setResult() {
			String name = Util.getName(html);
			String url_img = Util.getUrlImg(html.replace("\\", ""));

			String img_src = "";
			WebView myWebView = (WebView) getActivity().findViewById(R.id.img);

			if (url_img != null) {
				img_src = "<html><style>body{background:#FFF;margin: 0; padding: 0;widthth:150px;} img {margin-top:30px;border-radius:10px; display: block; margin-left: auto; margin-right: auto;} </style><body><img src='"
						+ url_img + "' alt='social info' /></body></html>";
			}
			myWebView.loadDataWithBaseURL(null, img_src, "text/html", "utf-8",
					null);
			if ( name != null && ! name.equalsIgnoreCase(""))
				((TextView) getActivity().findViewById(R.id.error_text))
						.setText(name);
			else
				((TextView) getActivity().findViewById(R.id.error_text))
				.setText(getString(R.string.noresult));
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
