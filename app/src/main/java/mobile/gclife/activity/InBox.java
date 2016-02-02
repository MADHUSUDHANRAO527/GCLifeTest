package mobile.gclife.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.http.EvenstPost;
import mobile.gclife.http.Hostname;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import mobile.gclife.MaterialDesign.PagerSlidingTabStrip;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;

public class InBox extends ActionBarActivity {
	
	PagerSlidingTabStrip tabs;
	ViewPager pager;
	MyPagerAdapter adapter;
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
		actionBar.setTitle("Mail");
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
						.getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);

		// arrayUrl.add("discussions");

		// Attach the page change listener to tab strip and **not** the view
		// pager inside the activity
		tabs.setOnPageChangeListener(new OnPageChangeListener() {

			// This method will be invoked when a new page becomes selected.
			@Override
			public void onPageSelected(int position) {

			}

			// This method will be invoked when the current page is scrolled
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// Code goes here
			}

			// Called when the scroll state changes:
			// SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
			@Override
			public void onPageScrollStateChanged(int state) {
				// Code goes here
			}
		});

	}

	protected void callInboxRec() {
		// TODO Auto-generated method stub

	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "     Write     ", "     Inbox    ",
				"    Sent   " };

		public MyPagerAdapter(FragmentManager fm) {
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
			return InboxTabs.newInstance(position);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_out_right);
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class InboxTabs extends Fragment {
		private int position, actionBarHeight;
		private static final String ARG_POSITION = "position";
		EditText subEdit, discriptionEdit;
		AutoCompleteTextView toEdit;
		String toStr, subjectStr, discStr, unamesList;
		TextView sendTxt;
		ImageView searchImg;
		ListView listviewUsernames;
		SwipeRefreshLayout mSwipeRefreshLayout;
		Runnable run;
		Hostname host = new Hostname();
		JSONArray userNamesArr, sentMailsJArr;
		JSONObject msgJObj;
		Typeface typefaceLight;
		 String hostname,mailid;
		ArrayList<String> listUnames = new ArrayList<String>();
		SharedPreferences userPref;
		ProgressBarCircularIndeterminate pDialog, pDialog1;
		ArrayList<String> selectedUnameList = new ArrayList<String>();
		ArrayList<HashMap<String, String>> listSent = new ArrayList<HashMap<String, String>>();
		String msgType;
		ListUnamesBaseAdapter adapterUnames;
		ListView listviewSent;
		// ArrayAdapter<String> adapter;
		ListSendMailBaseAdapter adapterSentRcv;
		AutoCompleteAdapter adapter;
		ArrayList<String> strngArr = new ArrayList<String>();
		JSONObject jsonDelete;
		public static InboxTabs newInstance(int position) {
			InboxTabs f = new InboxTabs();
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
			typefaceLight = Typeface.createFromAsset(getActivity().getAssets(),
					"fonts/RobotoLight.ttf");

			userPref = getActivity().getSharedPreferences("USER", 0);

			View v;

			if (position == 0) {
				v = infaltor.inflate(R.layout.inbox_write, container, false);

				toEdit = (AutoCompleteTextView) v.findViewById(R.id.toEdit);
				subEdit = (EditText) v.findViewById(R.id.fromEdit);
				discriptionEdit = (EditText) v.findViewById(R.id.discrpEdit);
				sendTxt = (TextView) v.findViewById(R.id.sendTxt);
				searchImg = (ImageView) v.findViewById(R.id.searchImg);
				pDialog1 = (ProgressBarCircularIndeterminate) v
						.findViewById(R.id.pDialog);

				toEdit.setInputType(EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);

				new ListUsernames().execute();

				sendTxt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						toStr = toEdit.getText().toString();
						System.out.println(toStr);
						subjectStr = subEdit.getText().toString();
						discStr = discriptionEdit.getText().toString();

						if (toStr == "" || toStr.equals("") || toStr == null
								|| toStr == "null") {
							showSnack(getActivity(), "Enter username!", "OK");
						} else if (subjectStr == "" || subjectStr.equals("")
								|| subjectStr == null || subjectStr == "null") {
							showSnack(getActivity(), "Enter subject!", "OK");
						} else if(subjectStr.length()>180){
							showSnack(getActivity(), "Subject length should not be more than 180!", "OK");
						}else {

							System.out
									.println(selectedUnameList + "  " + toStr);
							selectedUnameList.add(toStr);

							List<String> myList = new ArrayList<String>(Arrays
									.asList(toStr.split(",")));
							System.out.println(myList.size()
									+ "    SIZE !!!!! ");
							for (int i = 0; i < myList.size(); i++) {

								String uname = myList.get(i);

								if (listUnames.contains(uname)) {

									System.out.println(selectedUnameList);
									unamesList = selectedUnameList.toString()
											.replaceAll("[\\[\\](){}]", "");
									unamesList = unamesList.replaceAll("\\s",
											"");

								} else {
									showSnack(getActivity(), uname
											+ " is not registered!", "OK");
									break;
								}
								if (i < myList.size() - 1) {

								} else {
									new SendEmail().execute();
								}

							}
						}
					}
				});
				toEdit.addTextChangedListener(new TextWatcher() {

					@Override
					public void afterTextChanged(Editable s) {
						selectedUnameList.remove(s);
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						if (s.length() == 0) {
							strngArr = new ArrayList<String>();
							selectedUnameList = new ArrayList<String>();
						}
					}
				});

				toEdit.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						/*
						 * Toast.makeText(getActivity(),
						 * adapter.getItem(position).toString(),
						 * Toast.LENGTH_SHORT).show();
						 */
						if (!strngArr.contains(adapter.getItem(position)
								.toString())) {
							strngArr.add(adapter.getItem(position).toString());
						}
						toEdit.setText(TextUtils.join(",", strngArr));
						toEdit.setSelection(toEdit.getText().length());
						// toEdit.setText(toEdit.getText().toString()+
						// adapter.getItem(position).toString());

					}
				});

				searchImg.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final Dialog m_dialog = new Dialog(getActivity());
						m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						m_dialog.setContentView(R.layout.imp_contacts_popup_listview);
						m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
						listviewUsernames = (ListView) m_dialog
								.findViewById(R.id.listview);

						adapterUnames = new ListUnamesBaseAdapter(
								getActivity(), listUnames);
						listviewUsernames.setAdapter(adapterUnames);

						listviewUsernames
								.setOnItemClickListener(new OnItemClickListener() {

									@SuppressWarnings("unchecked")
									@Override
									public void onItemClick(
											AdapterView<?> parent, View view,
											int position, long id) {
										// TODO Auto-generated method stub

										String uname = listUnames.get(position);

										if (!strngArr.contains(uname)) {
											strngArr.add(uname);
										}
										toEdit.setText(TextUtils.join(",",
												strngArr));
										toEdit.setSelection(toEdit.getText()
												.length());

										// selectedUnameList.add(uname);
										/*
										 * unamesList = selectedUnameList
										 * .toString().replaceAll(
										 * "[\\[\\](){}]", ""); unamesList =
										 * unamesList.replaceAll( "\\s", "");
										 * toEdit.setText(unamesList);
										 */
										m_dialog.dismiss();
									}
								});

						m_dialog.show();
					}
				});

			} else if (position == 1) {
				v = infaltor.inflate(R.layout.ideas_list, container, false);

				ButtonFloat addBtn = (ButtonFloat) v.findViewById(R.id.addBtn);
				pDialog = (ProgressBarCircularIndeterminate) v
						.findViewById(R.id.pDialog);
				listviewSent = (ListView) v.findViewById(R.id.listview);
				mSwipeRefreshLayout = (SwipeRefreshLayout) v
						.findViewById(R.id.activity_main_swipe_refresh_layout);

				mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
						R.color.green, R.color.blue);

				listviewSent.setOnItemClickListener(new OnItemClickListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						HashMap<String, String> o = (HashMap<String, String>) listviewSent
								.getItemAtPosition(position);
						Intent i = new Intent(getActivity(), InboxDetail.class);
						i.putExtra("sender_name", o.get("sender_name"));
						i.putExtra("subject", o.get("subject"));
						i.putExtra("message", o.get("message"));
						i.putExtra("created_at", o.get("created_at"));
						startActivity(i);
					}
				});

				mSwipeRefreshLayout
						.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
							@Override
							public void onRefresh() {
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {

										listSent = new ArrayList<HashMap<String, String>>();
										new ListSentMail().execute();
										// setupAdapter();
										// adapter.notifyDataSetChanged();
										getActivity().runOnUiThread(run);
										mSwipeRefreshLayout
												.setRefreshing(false);
									}
								}, 2500);
							}
						});
				run = new Runnable() {
					public void run() {
						// reload content
						// list.clear();
						if (listSent.toString() == "[]" || listSent.size() == 0) {

						} else {
							adapterSentRcv.notifyDataSetChanged();
							listviewSent.invalidateViews();
						}

						// mRecyclerView.refreshDrawableState();
					}
				};

				addBtn.setVisibility(View.GONE);
				msgType = "receive";

				new ListSentMail().execute();
			} else {
				v = infaltor.inflate(R.layout.ideas_list, container, false);

				ButtonFloat addBtn = (ButtonFloat) v.findViewById(R.id.addBtn);
				pDialog = (ProgressBarCircularIndeterminate) v
						.findViewById(R.id.pDialog);
				addBtn.setVisibility(View.GONE);
				listviewSent = (ListView) v.findViewById(R.id.listview);
				mSwipeRefreshLayout = (SwipeRefreshLayout) v
						.findViewById(R.id.activity_main_swipe_refresh_layout);

				mSwipeRefreshLayout.setColorSchemeResources(R.color.orange,
						R.color.green, R.color.blue);
				msgType = "sent";
				listviewSent.setOnItemClickListener(new OnItemClickListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						HashMap<String, String> o = (HashMap<String, String>) listviewSent
								.getItemAtPosition(position);
						Intent i = new Intent(getActivity(), InboxDetail.class);
						i.putExtra("sender_name", o.get("sender_name"));
						i.putExtra("subject", o.get("subject"));
						i.putExtra("message", o.get("message"));
						i.putExtra("created_at", o.get("created_at"));
						startActivity(i);
					}
				});

				new ListSentMail().execute();

				mSwipeRefreshLayout
						.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
							@Override
							public void onRefresh() {
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {

										listSent = new ArrayList<HashMap<String, String>>();
										new ListSentMail().execute();
										// setupAdapter();
										// adapter.notifyDataSetChanged();
										getActivity().runOnUiThread(run);
										mSwipeRefreshLayout
												.setRefreshing(false);
									}
								}, 2500);
							}
						});
				run = new Runnable() {
					public void run() {
						// reload content
						// list.clear();
						if (listSent.toString() == "[]" || listSent.size() == 0) {

						} else {
							adapterSentRcv.notifyDataSetChanged();
							listviewSent.invalidateViews();
						}

						// mRecyclerView.refreshDrawableState();
					}
				};

			}
			TypedValue tv = new TypedValue();

			if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize,
					tv, true)) {
				actionBarHeight = TypedValue.complexToDimensionPixelSize(
						tv.data, getResources().getDisplayMetrics());
			}
			return v;

		}

		public class ListUsernames extends AsyncTask<Void, Void, Void> {
			@Override
			protected void onPreExecute() {

			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					userNamesArr = EvenstPost.makeRequestUserNamesList(host
							.globalVariable());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;

			}

			@Override
			protected void onPostExecute(Void unused) {
				System.out
						.println(userNamesArr + "  *************  USERNAMES ");
				if (userNamesArr != null) {
					for (int i = 0; i < userNamesArr.length(); i++) {

						try {
							listUnames.add(userNamesArr.get(i).toString());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					// will start working from first character
					// String[] array = listUnames.toArray(new
					// String[listUnames.size()]);
					if (getActivity() == null) {

					} else {
						adapter = new AutoCompleteAdapter(getActivity(),
								R.layout.auto_suggestion_row, R.id.autoSugg,
								listUnames);
						toEdit.setAdapter(adapter);// setting the adapter data
													// into the
													// AutoCompleteTextView
						toEdit.setTextColor(Color.BLACK);
						toEdit.setThreshold(1);

					}

				} else {

					showSnack(
							getActivity(),
							"Oops! Something went wrong. Please wait a moment!",
							"OK");

				}

			}
		}

		public class AutoCompleteAdapter extends ArrayAdapter<String> implements
				Filterable {

			private ArrayList<String> fullList;
			private ArrayList<String> mOriginalValues;
			private ArrayFilter mFilter;

			public AutoCompleteAdapter(FragmentActivity context, int resource,
					int autosugg, ArrayList<String> fullList) {

				super(context, resource, autosugg, fullList);
				this.fullList = fullList;
				mOriginalValues = new ArrayList<String>(fullList);

			}

			@Override
			public int getCount() {
				return fullList.size();
			}

			@Override
			public String getItem(int position) {
				return fullList.get(position);
			}

			/*
			 * @Override public View getView(int position, View convertView,
			 * ViewGroup parent) { return super.getView(position, convertView,
			 * parent); }
			 */

			@Override
			public Filter getFilter() {
				if (mFilter == null) {
					mFilter = new ArrayFilter();
				}
				return mFilter;
			}

			private class ArrayFilter extends Filter {
				private Object lock;

				@Override
				protected FilterResults performFiltering(CharSequence prefix) {
					String[] arr = prefix.toString().split(",");
					prefix = arr[arr.length - 1];

					FilterResults results = new FilterResults();

					if (mOriginalValues == null) {
						synchronized (lock) {
							mOriginalValues = new ArrayList<String>(fullList);
						}
					}

					if (prefix == null || prefix.length() == 0) {
						synchronized (lock) {
							ArrayList<String> list = new ArrayList<String>(
									mOriginalValues);
							results.values = list;
							results.count = list.size();
						}
					} else {
						final String prefixString = prefix.toString()
								.toLowerCase();

						ArrayList<String> values = mOriginalValues;
						int count = values.size();

						ArrayList<String> newValues = new ArrayList<String>(
								count);

						for (int i = 0; i < count; i++) {
							String item = values.get(i);
							if (item.toLowerCase().contains(prefixString)) {
								newValues.add(item);
							}

						}

						results.values = newValues;
						results.count = newValues.size();
					}

					return results;
				}

				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint,
						FilterResults results) {

					if (results.values != null) {
						fullList = (ArrayList<String>) results.values;
					} else {
						fullList = new ArrayList<String>();
					}
					if (results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			}
		}

		public class ListUnamesBaseAdapter extends BaseAdapter {
			ArrayList<String> list;
			private LayoutInflater inflator;
			private Context context;

			public ListUnamesBaseAdapter(Activity activity,
					ArrayList<String> listArticles) {
				// TODO Auto-generated constructor stub
				this.context = activity;
				this.list = listArticles;
				inflator = (LayoutInflater) activity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}

			@Override
			public Object getItem(int pos) {
				// TODO Auto-generated method stub
				return list.get(pos);
			}

			@Override
			public long getItemId(int pos) {
				// TODO Auto-generated method stub
				return pos;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder holder;
				if (convertView == null) {
					convertView = inflator.inflate(R.layout.imp_contacts_row,
							parent, false);
					holder = new ViewHolder();
					holder.titleTxt = (TextView) convertView
							.findViewById(R.id.titleTxt);
					holder.optionsImg = (ImageView) convertView
							.findViewById(R.id.optionsImg);
					holder.optionsImg.setVisibility(View.GONE);
					holder.titleTxt.setTypeface(typefaceLight);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}

				holder.titleTxt.setText(list.get(position));

				return convertView;
			}

			public class ViewHolder {
				TextView titleTxt;
				ImageView optionsImg;
			}
		}

		public class SendEmail extends AsyncTask<Void, Void, Void> {
			@Override
			protected void onPreExecute() {
				pDialog1.setVisibility(View.VISIBLE);
				sendTxt.setVisibility(View.INVISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				JSONObject jsonMsg = new JSONObject();
				try {
					JSONObject jsonMsgs = new JSONObject();

					jsonMsg.put("from_user_id",
							userPref.getString("USERID", "NV"));
					jsonMsg.put("to_user_id", "2");
					jsonMsg.put("subject", subjectStr);
					jsonMsg.put("message", discStr);
					jsonMsg.put("read", "NO");

					jsonMsgs.put("message", jsonMsg);
					jsonMsgs.put("names", unamesList);
					msgJObj = EvenstPost.makeRequestForPostMsg(jsonMsgs,
							host.globalVariable());

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void unused) {
				System.out.println(msgJObj + " ^^^^^^^^^^^^^^^^^^^^^^^^^^^ ");
				if (msgJObj != null) {
					showSnack(getActivity(), "Mail has sent!", "OK");
					subEdit.setText("");
					discriptionEdit.setText("");
					toEdit.setText("");
					pDialog1.setVisibility(View.GONE);
					sendTxt.setVisibility(View.VISIBLE);
				} else {
					sendTxt.setVisibility(View.VISIBLE);
					pDialog1.setVisibility(View.GONE);
					showSnack(
							getActivity(),
							"Oops! Something went wrong. Please wait a moment!",
							"OK");
				}
			}
		}

		public class ListSentMail extends AsyncTask<Void, Void, Void> {
			@Override
			protected void onPreExecute() {
				pDialog.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					sentMailsJArr = EvenstPost.makeRequestSentList(
							host.globalVariable(),
							userPref.getString("USERID", "NV"), msgType);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;

			}

			@Override
			protected void onPostExecute(Void unused) {
				System.out.println(sentMailsJArr + "  *************  sent ");

				if (sentMailsJArr == null || sentMailsJArr.toString() == "null") {
					showSnack(
							getActivity(),
							"Oops! Something went wrong. Please wait a moment!",
							"OK");
				} else {
					if (sentMailsJArr.length() == 0
							|| sentMailsJArr.toString() == "[]"
							|| sentMailsJArr.toString() == ""
							|| sentMailsJArr.toString().equals("")) {
						pDialog.setVisibility(View.GONE);

					} else {
						listSent = new ArrayList<HashMap<String, String>>();
						for (int i = 0; i < sentMailsJArr.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();
							try {
								JSONObject jsonSent = sentMailsJArr
										.getJSONObject(i);
								map.put("sender_name",
										jsonSent.getString("sender_name"));
								map.put("subject",
										jsonSent.getString("subject"));
								map.put("message",
										jsonSent.getString("message"));
								map.put("id",
										jsonSent.getString("id"));

								String createdAt = jsonSent
										.getString("created_at");
								String datee = createdAt.substring(0, 10);
								String fromYear = datee.substring(0, 4);
								try {
									DateFormat df = new SimpleDateFormat(
											"yyyy-MM-dd");
									Date dat = df.parse(datee);
									System.out.println(createdAt);
									createdAt = dat.toString();
									System.out.println(createdAt.length());
									createdAt = createdAt.substring(3, 10);
									System.out.println(createdAt);
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println(jsonSent
										.getString("created_at").substring(12, 19));
								String time=jsonSent
										.getString("created_at").substring(11, 19);
								 SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
								 Date d;
								try {
									d = df.parse(time);
									 Calendar cal = Calendar.getInstance();
									 cal.setTime(d);
									 cal.add(Calendar.MINUTE, -8);
									  time = df.format(cal.getTime());
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} 
								
								
								DateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
								DateFormat outputFormat = new SimpleDateFormat("KK:mm a");
								timeFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								
								try {
									time=outputFormat.format(timeFormat.parse(time));
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								
								// holder.recvdDateTxt.setText(list.get(position).get("created_at"));

								map.put("created_at", createdAt + ","
										+ fromYear +","+time);
								listSent.add(map);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						if (getActivity() == null) {

						} else {
							adapterSentRcv = new ListSendMailBaseAdapter(
									getActivity(), listSent);
							listviewSent.setAdapter(adapterSentRcv);
						}

					}
				}
				pDialog.setVisibility(View.GONE);
			}
		}

		public class ListSendMailBaseAdapter extends BaseAdapter {
			ArrayList<HashMap<String, String>> list;
			private LayoutInflater inflator;
			private Context context;

			public ListSendMailBaseAdapter(Context context2,
					ArrayList<HashMap<String, String>> listArticles) {
				// TODO Auto-generated constructor stub
				this.context = context2;
				this.list = listArticles;
				inflator = (LayoutInflater) context2
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return list.size();
			}

			@Override
			public Object getItem(int pos) {
				// TODO Auto-generated method stub
				return list.get(pos);
			}

			@Override
			public long getItemId(int pos) {
				// TODO Auto-generated method stub
				return pos;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				// TODO Auto-generated method stub
				ViewHolder holder;
				if (convertView == null) {
					convertView = inflator.inflate(R.layout.inbox_list, parent,
							false);
					holder = new ViewHolder();
					holder.unameTxt = (TextView) convertView
							.findViewById(R.id.unameTxt);
					holder.subjectTxt = (TextView) convertView
							.findViewById(R.id.subjectTxt);
					holder.recvdDateTxt = (TextView) convertView
							.findViewById(R.id.recvdDateTxt);
					holder.deleteImg = (ImageView) convertView
							.findViewById(R.id.deleteImg);
					holder.unameTxt.setTypeface(typefaceLight);
					holder.subjectTxt.setTypeface(typefaceLight);
					holder.recvdDateTxt.setTypeface(typefaceLight);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.unameTxt.setText(list.get(position).get("sender_name"));
				holder.subjectTxt.setText(list.get(position).get("subject"));

				holder.deleteImg.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// to remove row
						
						final Dialog m_dialog = new Dialog(context);
						m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						m_dialog.setContentView(R.layout.areyousuredelepopup);
						m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;
						
						TextView noTxt=(TextView)m_dialog.findViewById(R.id.noTxt);
						TextView yesTxt=(TextView)m_dialog.findViewById(R.id.yesTxt);
						noTxt.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								m_dialog.dismiss();
							}
						});
						yesTxt.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								mailid = list.get(position).get("id");
								System.out.println(mailid + " !!!!!!!!!!!!!!!!!!!"+list.get(position).get("id"));
								new DeleteEvent().execute();
								list.remove(position);
								m_dialog.dismiss();
							}
						});
						
						m_dialog.show();
					}
				});
				holder.recvdDateTxt.setText(list.get(position)
						.get("created_at"));

				return convertView;
			}

			public class ViewHolder {
				TextView unameTxt, subjectTxt, recvdDateTxt;
				ImageView deleteImg;
			}
		}

		void showSnack(FragmentActivity flats, String stringMsg, String ok) {
			new SnackBar(getActivity(), stringMsg, ok, new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			}).show();
		}
		public class DeleteEvent extends AsyncTask<Void, Void, Void> {
			@Override
			protected void onPreExecute() {
			//	pDialogPop.setVisibility(View.VISIBLE);
			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				try {
					System.out.println(mailid + " **************");
					jsonDelete = EvenstPost.makeDelete(host
							.globalVariable(), mailid,"messages");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void unused) {
			//	m_dialog.dismiss();
			//	pDialogPop.setVisibility(View.GONE);
			//	submitTxt.setVisibility(View.INVISIBLE);
				showSnack(getActivity(),
						"Deleted!",
						"OK");
				adapterSentRcv.notifyDataSetChanged();
			}
		}
	}

}
