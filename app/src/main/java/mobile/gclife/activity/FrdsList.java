package mobile.gclife.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.FlatDetailsPojo;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import mobile.gclife.application.InternetConnectionDetector;
import mobile.gclife.application.NothingSelectedSpinnerAdapter1;
import mobile.gclife.http.EvenstPost;
import mobile.gclife.http.Hostname;
import mobile.gclife.http.SocietyNameGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.SnackBar;

public class FrdsList extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	ButtonFloat addBtn;
	ProgressBarCircularIndeterminate pDialog;
	InternetConnectionDetector netConn;
	UserDetailsPojo user;
	Boolean isInternetPresent = false;
	String hostname, eventName, avenueName, societyName, buildingNum;
	ListView listviewIdeas;
	JSONArray jsonResultArry;
	SharedPreferences userPref;
	List<FlatDetailsPojo> flatsList = new ArrayList<FlatDetailsPojo>();
	FlatDetailsPojo flats;
	Typeface typefaceLight;
	Map<String, List<String>> societyMap = new HashMap<String, List<String>>();
	Map<String, List<String>> buildingMap = new HashMap<String, List<String>>();
	Spinner avenueSpinner, societyNameSpinner, buildingSpinner;
	EditText flatNoEdit;
	ArrayAdapter<String> associtationAdapter;
	JSONObject jsonDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends_list);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		actionBar.setTitle("Friends");
		addBtn = (ButtonFloat) findViewById(R.id.addBtn);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
		listviewIdeas = (ListView) findViewById(R.id.listview);

		avenueSpinner = (Spinner) findViewById(R.id.avenueSpin);
		societyNameSpinner = (Spinner) findViewById(R.id.societySpin);
		buildingSpinner = (Spinner) findViewById(R.id.buildingSpin);

		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		Hostname host = new Hostname();
		hostname = host.globalVariable();

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (jsonResultArry == null
						|| jsonResultArry.toString() == "null") {
					showSnack(FrdsList.this,
							"Please wait! Societies are loading...!", "OK");
				} else {
					final Dialog m_dialog = new Dialog(FrdsList.this);
					m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					m_dialog.setContentView(R.layout.frds_filter);
					m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

					avenueSpinner = (Spinner) m_dialog
							.findViewById(R.id.avenueSpin);
					societyNameSpinner = (Spinner) m_dialog
							.findViewById(R.id.societySpin);
					flatNoEdit = (EditText) m_dialog
							.findViewById(R.id.flatNoEdit);
					buildingSpinner = (Spinner) m_dialog
							.findViewById(R.id.buildingSpin);
					TextView cancellTxt = (TextView) m_dialog
							.findViewById(R.id.cancellTxt);
					TextView submitTxt = (TextView) m_dialog
							.findViewById(R.id.submitTxt);

					cancellTxt.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							m_dialog.dismiss();
						}
					});
					submitTxt.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
						}

					});

					ArrayAdapter<CharSequence> avenueAdapter = ArrayAdapter
							.createFromResource(FrdsList.this,
									R.array.avenueArray, R.layout.spinr_txt);
					ArrayAdapter<CharSequence> sociAdapter = ArrayAdapter
							.createFromResource(FrdsList.this,
									R.array.societyNameArray,
									R.layout.spinr_txt);
					ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
							.createFromResource(FrdsList.this,
									R.array.buildingNameArray,
									R.layout.spinr_txt);

					avenueAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					buildingAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sociAdapter
							.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
					/*
					 * avenueSpinner .setAdapter(new
					 * NothingSelectedSpinnerAdapter1( avenueAdapter,
					 * R.layout.avenue_spinner_nothing_selected,
					 * FrdsList.this));
					 */
					societyNameSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									sociAdapter,
									R.layout.society_spinner_nothing_selected,
									FrdsList.this));

					buildingSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									buildingAdapter,
									R.layout.building_spinner_nothing_selected,
									FrdsList.this));
					// load data from server

					avenueSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									associtationAdapter,
									R.layout.avenue_spinner_nothing_selected,
									FrdsList.this));
					societyNameSpinner.setEnabled(true);

					avenueSpinner
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									avenueName = String.valueOf(avenueSpinner
											.getSelectedItem());
									System.out
											.println(societyMap
													+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
									// ((TextView)
									// arg0.getChildAt(0)).setTextColor(0x00000000);

									((TextView) avenueSpinner.getSelectedView())
											.setTextColor(getResources()
													.getColor(R.color.black));

									if (avenueName == null
											|| avenueName.equals("null")) {
										System.out
												.println("1st TIME NULLLLLLLLLLLLLLLLLL");
										societyNameSpinner.setEnabled(false);
										buildingSpinner.setEnabled(false);

									} else {
										List<String> societylist = societyMap
												.get(avenueName);
										ArrayAdapter<String> sociAdapter = new ArrayAdapter<String>(
												FrdsList.this,
												android.R.layout.simple_list_item_1,
												societylist);

										sociAdapter
												.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
										societyNameSpinner
												.setAdapter(new NothingSelectedSpinnerAdapter1(
														sociAdapter,
														R.layout.society_spinner_nothing_selected,
														FrdsList.this));

										ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
												.createFromResource(
														FrdsList.this,
														R.array.buildingNameArray,
														R.layout.spinr_txt);

										buildingSpinner
												.setAdapter(new NothingSelectedSpinnerAdapter1(
														buildingAdapter,
														R.layout.building_spinner_nothing_selected,
														FrdsList.this));

										societyNameSpinner.setEnabled(true);
									}

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}
							});

					societyNameSpinner
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int pos, long arg3) {
									// TODO Auto-generated method stub
									societyName = String
											.valueOf(societyNameSpinner
													.getSelectedItem());

									((TextView) societyNameSpinner
											.getSelectedView())
											.setTextColor(getResources()
													.getColor(R.color.black));
									System.out
											.println(buildingMap
													+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

									if (societyName == null
											|| societyName.equals("null")) {
										System.out
												.println("1st TIME NULLLLLLLLLLLLLLLLLL");
									} else {
										List<String> buildinglist = buildingMap
												.get(societyName);
										ArrayAdapter<String> buildAdapter = new ArrayAdapter<String>(
												FrdsList.this,
												android.R.layout.simple_list_item_1,
												buildinglist);

										buildAdapter
												.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
										buildingSpinner
												.setAdapter(new NothingSelectedSpinnerAdapter1(
														buildAdapter,
														R.layout.building_spinner_nothing_selected,
														FrdsList.this));
										buildingSpinner.setEnabled(true);
									}
									// System.out.println(buildingMap);

									System.out.println(societyMap);

									List<String> societyNames = societyMap
											.get(societyName);

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub

								}
							});
					buildingSpinner
							.setOnItemSelectedListener(new OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									// TODO Auto-generated method stub
									((TextView) buildingSpinner
											.getSelectedView())
											.setTextColor(getResources()
													.getColor(R.color.black));
									buildingNum = String
											.valueOf(buildingSpinner
													.getSelectedItem());

									flatNoEdit.setCursorVisible(false);

								}

								@Override
								public void onNothingSelected(
										AdapterView<?> arg0) {
									// TODO Auto-generated method stub
								}
							});
					flatNoEdit.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							flatNoEdit.setCursorVisible(true);
						}
					});
					m_dialog.show();
				}
			}
		});

		listviewIdeas.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> o = (HashMap<String, String>) listviewIdeas
						.getItemAtPosition(position);
				Intent i = new Intent(FrdsList.this, FrdsDetail.class);
				i.putExtra("jsonDetails", o.get("jsonDetails"));

				startActivity(i);
			}
		});

		new ListFrds().execute();
		new SocietyNamees().execute();
	}

	public class ListFrds extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = EvenstPost.makeRequestFrdsList(hostname);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF Ideas !!!!!!!!!!!!!!!!!!!!");
			if (jsonResultArry != null) {
				if (jsonResultArry.length() == 0
						|| jsonResultArry.toString() == "[]"
						|| jsonResultArry.toString() == ""
						|| jsonResultArry.toString().equals("")) {
					pDialog.setVisibility(View.GONE);

					showSnack(FrdsList.this, "Oops! There is no Friends!", "OK");
				} else {

					ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

					for (int i = 0; i < jsonResultArry.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						try {
							jsonDetails = jsonResultArry.getJSONObject(i);
							map.put("uname", jsonDetails.getString("username"));
							map.put("email", jsonDetails.getString("email"));
							map.put("mobile", jsonDetails.getString("mobile"));
							JSONArray flatDetailsArr = jsonDetails
									.getJSONArray("gclife_registration_flatdetails");
							map.put("jsonDetails", jsonDetails.toString());
							for (int j = 0; j < flatDetailsArr.length(); j++) {
								JSONObject jsonFlat = flatDetailsArr
										.getJSONObject(j);
								map.put("avenue_name",
										jsonFlat.getString("avenue_name"));
								map.put("buildingid",
										jsonFlat.getString("buildingid"));
								map.put("flat_number",
										jsonFlat.getString("flat_number"));
							}

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						list.add(map);
					}
					ListFreindsBaseAdapter adapter = new ListFreindsBaseAdapter(
							FrdsList.this, list);
					listviewIdeas.setAdapter(adapter);
					pDialog.setVisibility(View.GONE);

				}
			} else {
				pDialog.setVisibility(View.GONE);

				showSnack(FrdsList.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}

		}
	}

	public class ListFreindsBaseAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> list;
		private LayoutInflater inflator;
		private Context context;

		public ListFreindsBaseAdapter(Activity activity,
				ArrayList<HashMap<String, String>> listArticles) {
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
				convertView = inflator.inflate(R.layout.friends_list_row,
						parent, false);
				holder = new ViewHolder();
				holder.unameTxt = (TextView) convertView
						.findViewById(R.id.titleTxt);
				holder.avaneuNameTxt = (TextView) convertView
						.findViewById(R.id.avaneuNameTxt);

				holder.unameTxt.setTypeface(typefaceLight);
				holder.avaneuNameTxt.setTypeface(typefaceLight);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.unameTxt.setText(list.get(position).get("uname"));
			holder.avaneuNameTxt.setText(list.get(position).get("avenue_name")
					+ "," + list.get(position).get("buildingid") + ","
					+ list.get(position).get("flat_number"));

			return convertView;
		}

		public class ViewHolder {
			TextView unameTxt, avaneuNameTxt;
		}
	}

	public class SocietyNamees extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = SocietyNameGet.callSocietyList(hostname);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {

			System.out.println(jsonResultArry
					+ "LIST OF MEMS !!!!!!!!!!!!!!!!!!!!");
			if (jsonResultArry != null) {
				try {
					ArrayList<String> listAssociation = new ArrayList<String>();
					for (int i = 0; i < jsonResultArry.length(); i++) {
						ArrayList<String> listSociety = new ArrayList<String>();
						JSONObject json = jsonResultArry.getJSONObject(i);

						String associationName = json
								.getString("associationname");

						listAssociation.add(associationName);

						JSONArray societyArray;
						societyArray = json.getJSONArray("society_masters");
						ArrayList<String> listSocietydata = new ArrayList<String>();
						ArrayList<String> listbuilddata = null;

						System.out.println(societyArray);
						final int numberOfItemsInResp = societyArray.length();
						for (int j = 0; j < numberOfItemsInResp; j++) {

							JSONObject societyjson = societyArray
									.getJSONObject(j);

							String societyName = societyjson
									.getString("societyname");

							listSociety.add(societyName);
							listSocietydata.add(societyArray.get(j).toString());

							System.out.println(listSociety);

							System.out.println(listSocietydata);

							JSONArray buildingArray;
							buildingArray = societyjson
									.getJSONArray("building_masters");
							listbuilddata = new ArrayList<String>();
							for (int k = 0; k < buildingArray.length(); k++) {
								JSONObject jsonBuild = buildingArray
										.getJSONObject(k);
								String buildingName = jsonBuild
										.getString("buildinname");
								BigDecimal number = new BigDecimal(buildingName);
								buildingName = number.stripTrailingZeros()
										.toPlainString();

								listbuilddata.add(buildingName);

								// listbuilddata.add(buildingArray.get(k).toString());
							}
							List<String> buildList = new ArrayList<String>(
									listbuilddata);
							buildingMap.put(societyName, buildList);

							System.out.println(buildingMap);
						}
						// ArrayList<String> societyList = new
						// ArrayList<String>(listbuilddata);
						List<String> associationList = new ArrayList<String>(
								listSociety);
						societyMap.put(associationName, associationList);

					}
					associtationAdapter = new ArrayAdapter<String>(
							FrdsList.this, android.R.layout.simple_list_item_1,
							listAssociation);

					associtationAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				showSnack(FrdsList.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
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

	void showSnack(FrdsList flats, String stringMsg, String ok) {
		new SnackBar(FrdsList.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
