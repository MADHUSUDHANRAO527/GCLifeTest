package mobile.gclife.activity;

import java.util.ArrayList;
import java.util.List;

import mobile.gclife.PojoGson.FlatDetailsPojo;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import mobile.gclife.MaterialDesign.PagerSlidingTabStrip;
import com.gc.materialdesign.views.ButtonFloat;
import com.google.gson.Gson;

public class UserProfile extends ActionBarActivity {
	String hostname;
	PagerSlidingTabStrip tabs;
	ViewPager pager;
	MyPageAdapter adapter;
	android.support.v7.app.ActionBar actionBar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frds_detail_tabstrip);

		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		// getActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
		actionBar.setTitle("Profile");
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPageAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);

		// arrayUrl.add("discussions");

	}

	public class MyPageAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "Personal Details", "Flat Details" };

		public MyPageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			return UserDetailTabs.newInstance(position);
		}

	}

	public static class UserDetailTabs extends Fragment {
		private int position, actionBarHeight;
		private static final String ARG_POSITION = "position";
		TextView unameTxt, emailTxt, mobileNumTxt, avenueNameTxt,
				societyNameTxt, buidlingNameTxt, flatNumtxt,
				flatTypetxt, ownwerTypetxt, memTypeTxt,
				dateFrmTypetxt, liscnseEndsOntxt, emrNumTxt, occupationtxt,
				dobTxt;
		Typeface typefaceLight, typeMeduim;
		LinearLayout layUserDet, flatDetailsLay;
		SharedPreferences userPref;
		List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
		ListView listviewFlats;
		ButtonFloat addFlats, editProfile;
		UserDetailsPojo user;
		String memDet, userStatus;

		public static UserDetailTabs newInstance(int position) {
			UserDetailTabs f = new UserDetailTabs();
			Bundle b = new Bundle();
			b.putInt(ARG_POSITION, position);
			f.setArguments(b);
			return f;
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setHasOptionsMenu(true);
			position = getArguments().getInt(ARG_POSITION);
		}

		@Override
		public View onCreateView(final LayoutInflater infaltor,
				final ViewGroup container, Bundle savedInstanceState) {
			View v = null;
			Bundle b = getActivity().getIntent().getExtras();
			v = infaltor.inflate(R.layout.profile, container, false);
			userPref = getActivity().getSharedPreferences("USER", MODE_PRIVATE);

			emailTxt = (TextView) v.findViewById(R.id.emailTxt);

			unameTxt = (TextView) v.findViewById(R.id.unameTxt);
			mobileNumTxt = (TextView) v.findViewById(R.id.mobileNumTxt);
			listviewFlats = (ListView) v.findViewById(R.id.flatsList);
			addFlats = (ButtonFloat) v.findViewById(R.id.addFlats);
			editProfile = (ButtonFloat) v.findViewById(R.id.editProfile);
			layUserDet = (LinearLayout) v.findViewById(R.id.layUserDet);
			flatDetailsLay = (LinearLayout) v.findViewById(R.id.flatDetailsLay);
			emrNumTxt = (TextView) v.findViewById(R.id.emeryNumTxt);
			occupationtxt = (TextView) v.findViewById(R.id.occuTxt);
			dobTxt = (TextView) v.findViewById(R.id.dobTxt);

			editProfile.setVisibility(View.VISIBLE);

			typefaceLight = Typeface.createFromAsset(getActivity().getAssets(),
					"fonts/RobotoLight.ttf");
			typeMeduim = Typeface.createFromAsset(getActivity().getAssets(),
					"fonts/RobotoMedium.ttf");
			emailTxt.setTypeface(typefaceLight);
			mobileNumTxt.setTypeface(typefaceLight);
			unameTxt.setTypeface(typefaceLight);

			Intent intent = getActivity().getIntent();
			Bundle bundle = intent.getExtras();
			memDet = bundle.getString("EACH_USER_DET");

			System.out.println(memDet + " +++++++++++++++++++++++++++++++++");

			Gson gson = new Gson();
			if (memDet == null || memDet == "null" || memDet.equals(null)
					|| memDet == "" || memDet.equals("")) {

				String jsonUser = userPref.getString("USER_DATA", "NV");
				user = gson.fromJson(jsonUser, UserDetailsPojo.class);

			} else {
				editProfile.setVisibility(View.GONE);
				user = gson.fromJson(memDet, UserDetailsPojo.class);
				addFlats.setVisibility(View.INVISIBLE);

			}
			unameTxt.setText(user.getUsername());
			emailTxt.setText(user.getEmail());
			mobileNumTxt.setText(user.getMobile());

			emrNumTxt.setText(user.getEmeNum());
			occupationtxt.setText(user.getOccupation());
			dobTxt.setText(user.getDob());

			// list flat details
			flatsList = user.getGclife_registration_flatdetails();

			FlatModelAdapter dataTaskGrpAdapter = new FlatModelAdapter(
					getActivity(), R.layout.flat_list_row, flatsList);

			listviewFlats.setAdapter(dataTaskGrpAdapter);

			listviewFlats.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					FlatDetailsPojo flats = flatsList.get(position);
					userStatus = flats.getStatus();
					Intent flatDetail = new Intent(getActivity(),
							FlatDetail.class);
					Gson gsonn = new Gson();
					String jsonFlats = gsonn.toJson(flats);
					flatDetail.putExtra("DETAILS", jsonFlats);
					startActivity(flatDetail);
					getActivity().overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
				}
			});
			addFlats.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent flats = new Intent(getActivity(), AddFlats.class);
					startActivity(flats);
					getActivity().overridePendingTransition(R.anim.slide_right_in,
							R.anim.slide_out_right);
				}
			});
			editProfile.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent flats = new Intent(getActivity(), EditProfile.class);
					startActivity(flats);
					getActivity().overridePendingTransition(R.anim.slide_right_in,
							R.anim.slide_out_right);
				}
			});
			if (position == 0) {
				addFlats.setVisibility(View.INVISIBLE);
				flatDetailsLay.setVisibility(View.GONE);
				if (memDet == null || memDet == "null" || memDet.equals(null)
						|| memDet == "" || memDet.equals("")) {
					editProfile.setVisibility(View.VISIBLE);
				}
				else{
					editProfile.setVisibility(View.GONE);
				}
				layUserDet.setVisibility(View.VISIBLE);
			} else {
				flatDetailsLay.setVisibility(View.VISIBLE);
				editProfile.setVisibility(View.GONE);
				layUserDet.setVisibility(View.GONE);
				listviewFlats.setVisibility(View.VISIBLE);
				if (memDet == null || memDet == "null" || memDet.equals(null)
						|| memDet == "" || memDet.equals("")) {
					addFlats.setVisibility(View.VISIBLE);
				} else {
					addFlats.setVisibility(View.INVISIBLE);
				}
			}

			TypedValue tv = new TypedValue();

			if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize,
					tv, true)) {
				actionBarHeight = TypedValue.complexToDimensionPixelSize(
						tv.data, getResources().getDisplayMetrics());
			}
			return v;

		}

		private class FlatModelAdapter extends ArrayAdapter<FlatDetailsPojo> {

			ArrayList<FlatDetailsPojo> flatsList;
			LayoutInflater inflator;

			public FlatModelAdapter(Context context, int textViewResourceId,
					List<FlatDetailsPojo> flatsList) {
				super(context, textViewResourceId, flatsList);
				this.flatsList = new ArrayList<FlatDetailsPojo>();
				this.flatsList.addAll(flatsList);
				inflator = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			@Override
			public int getCount() {
				return flatsList.size();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				final ViewHolder holder;
				Log.v("ConvertView", String.valueOf(position));
				final FlatDetailsPojo flats = flatsList.get(position);
				if (convertView == null) {
					convertView = inflator
							.inflate(R.layout.flat_list_row, null);

					holder = new ViewHolder();
					holder.avenueNameTxt = (TextView) convertView
							.findViewById(R.id.avenueNameTxt);
					holder.flatNumTxt = (TextView) convertView
							.findViewById(R.id.flatNumTxt);

					holder.avenueNameTxt.setTypeface(typeMeduim);
					holder.flatNumTxt.setTypeface(typefaceLight);

					convertView.setTag(holder);

				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.avenueNameTxt.setText(flats.getAvenue_name());
				holder.flatNumTxt.setText(flats.getBuildingid());

				return convertView;
			}

			private class ViewHolder {
				public TextView avenueNameTxt, flatNumTxt;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			onBackPressed();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_out_right);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
