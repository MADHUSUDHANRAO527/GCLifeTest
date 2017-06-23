package mobile.gclifetest.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.gclifetest.http.SignUpPost;
import mobile.gclifetest.http.SocietyNameGet;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.GclifeApplication;
import mobile.gclifetest.utils.NothingSelectedSpinnerAdapter1;

public class AddFlats extends BaseActivity {
	Typeface typefaceLight;
	TextView finishTxt;
	String avenueName, societyName, buildingNum, flatType, ownerType,
			memberType, flatNum, relationShipDateStr = "",
			liscenseDateStr = "", realtionShipWithOwner = "";
	EditText datewithOwnerEdit, lisecnseEndsOntxtEdit, flatNumEdit;
	ProgressBarCircularIndeterminate pDialog, pDialog1;
	Spinner avenueSpinner, societyNameSpinner, buildingSpinner,
			flatTypeSpinner, ownerTypeSpinner, memberTypeSpinner,
			reltionwithOwnerSpinner;
	Map<String, List<String>> societyMap = new HashMap<String, List<String>>();
	Map<String, List<String>> buildingMap = new HashMap<String, List<String>>();
	static final int DATE_DIALOG_FROMID = 0;
	static final int DATE_DIALOG_TOID = 1;
	SharedPreferences userPref;
	RelativeLayout reltionwithOwnerLay, line6,dateToLay;
	Editor editor;
	JSONObject jsonFlatResults;
	JSONArray jsonResultArry;
    RelativeLayout snackLay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_flat_details);

		avenueSpinner = (Spinner) findViewById(R.id.avenueSpin);
		societyNameSpinner = (Spinner) findViewById(R.id.societySpin);
		flatNumEdit = (EditText) findViewById(R.id.flatNoEdit);
		buildingSpinner = (Spinner) findViewById(R.id.buildingSpin);
		flatTypeSpinner = (Spinner) findViewById(R.id.flatTypeSpin);
		ownerTypeSpinner = (Spinner) findViewById(R.id.ownerSpin);
		memberTypeSpinner = (Spinner) findViewById(R.id.memberSpin);
		datewithOwnerEdit = (EditText) findViewById(R.id.datewithOwnerEdit);
		reltionwithOwnerSpinner = (Spinner) findViewById(R.id.reltionwithOwnerSpinner);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog);
		pDialog1 = (ProgressBarCircularIndeterminate) findViewById(R.id.pDialog1);
		dateToLay= (RelativeLayout) findViewById(R.id.dateToLay);
		reltionwithOwnerLay = (RelativeLayout) findViewById(R.id.reltionwithOwnerLay);
		line6 = (RelativeLayout) findViewById(R.id.line6);
		lisecnseEndsOntxtEdit = (EditText) findViewById(R.id.lisecnseEndsOntxtEdit);
		finishTxt = (TextView) findViewById(R.id.finishTxt);
        snackLay=(RelativeLayout)findViewById(R.id.snackLay);
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		editor = userPref.edit();

		setUpActionBar("Add flat details");

		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		ArrayAdapter<CharSequence> avenueAdapter = ArrayAdapter
				.createFromResource(this, R.array.avenueArray,
						R.layout.spinr_txt);

		ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
				.createFromResource(this, R.array.buildingNameArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> flatTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.flatsTypeArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> ownerTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.ownerTypeArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> memberTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.memberTypeArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> sociAdapter = ArrayAdapter
				.createFromResource(this, R.array.societyNameArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> reltionwithOwnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.reltionwithOwnerTypeArray,
						R.layout.spinr_txt);
		avenueAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		buildingAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		flatTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ownerTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		memberTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		reltionwithOwnerAdapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

		sociAdapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

		avenueSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				avenueAdapter, R.layout.avenue_spinner_nothing_selected,
				AddFlats.this));

		societyNameSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				sociAdapter, R.layout.society_spinner_nothing_selected,
				AddFlats.this));

		buildingSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				buildingAdapter, R.layout.building_spinner_nothing_selected,
				AddFlats.this));

		flatTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				flatTypeAdapter, R.layout.flattype_spinner_nothing_selected,
				AddFlats.this));

		ownerTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				ownerTypeAdapter, R.layout.ownertype_spinner_nothing_selected,
				AddFlats.this));
		memberTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				memberTypeAdapter, R.layout.member_spinner_nothing_selected,
				AddFlats.this));
		reltionwithOwnerSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				reltionwithOwnerAdapter,
				R.layout.relationship_spinner_nothing_selected, this));

		societyNameSpinner.setEnabled(false);

		avenueSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				avenueName = String.valueOf(avenueSpinner.getSelectedItem());
				System.out.println(societyMap
						+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				// ((TextView) arg0.getChildAt(0)).setTextColor(0x00000000);

				((TextView) avenueSpinner.getSelectedView())
						.setTextColor(getResources().getColor(R.color.black));

				if (avenueName == null || avenueName.equals("null")) {
					System.out.println("1st TIME NULLLLLLLLLLLLLLLLLL");
					societyNameSpinner.setEnabled(false);
					buildingSpinner.setEnabled(false);
					flatTypeSpinner.setEnabled(false);
					ownerTypeSpinner.setEnabled(false);
					memberTypeSpinner.setEnabled(false);

				} else {
					List<String> societylist = societyMap.get(avenueName);
					ArrayAdapter<String> sociAdapter = new ArrayAdapter<String>(
							AddFlats.this, R.layout.spinr_txt,
							societylist);

					sociAdapter
							.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
					societyNameSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									sociAdapter,
									R.layout.society_spinner_nothing_selected,
									AddFlats.this));

					ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
							.createFromResource(AddFlats.this,
									R.array.buildingNameArray,
									R.layout.spinr_txt);

					buildingSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									buildingAdapter,
									R.layout.building_spinner_nothing_selected,
									AddFlats.this));

					societyNameSpinner.setEnabled(true);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		societyNameSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int pos, long arg3) {
						// TODO Auto-generated method stub
						societyName = String.valueOf(societyNameSpinner
								.getSelectedItem());

						((TextView) societyNameSpinner.getSelectedView())
								.setTextColor(getResources().getColor(
										R.color.black));
						System.out
								.println(buildingMap
										+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

						if (societyName == null || societyName.equals("null")) {
							System.out.println("1st TIME NULLLLLLLLLLLLLLLLLL");
						} else {
							List<String> buildinglist = buildingMap
									.get(societyName);
							ArrayAdapter<String> buildAdapter = new ArrayAdapter<String>(
									AddFlats.this,
									R.layout.spinr_txt,
									buildinglist);

							buildAdapter
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							buildingSpinner
									.setAdapter(new NothingSelectedSpinnerAdapter1(
											buildAdapter,
											R.layout.building_spinner_nothing_selected,
											AddFlats.this));
							buildingSpinner.setEnabled(true);
						}
						// System.out.println(buildingMap);

						System.out.println(societyMap);

						List<String> societyNames = societyMap.get(societyName);

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});
		buildingSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				((TextView) buildingSpinner.getSelectedView())
						.setTextColor(getResources().getColor(R.color.black));
				buildingNum = String.valueOf(buildingSpinner.getSelectedItem());
				flatTypeSpinner.setEnabled(true);

			//	flatNumEdit.setCursorVisible(false);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				flatTypeSpinner.setEnabled(false);
			}
		});
		/*flatNumEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flatNumEdit.setCursorVisible(true);
			}
		});*/
		flatTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				flatType = String.valueOf(flatTypeSpinner.getSelectedItem());
				ownerTypeSpinner.setEnabled(true);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				ownerTypeSpinner.setEnabled(false);
			}
		});

		ownerTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						ownerType = String.valueOf(ownerTypeSpinner
								.getSelectedItem());
						if (ownerType == "Licensee"
								|| ownerType.equals("Licensee")) {
							dateToLay.setVisibility(View.VISIBLE);
						}else{
							dateToLay.setVisibility(View.GONE);
						}

						if (ownerType == "Licensee"
								|| ownerType.equals("Licensee")
								|| ownerType == "Dependant"
								|| ownerType.equals("Dependant")) {

							System.out
									.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

							ArrayAdapter<CharSequence> memberTypeAdapter = ArrayAdapter
									.createFromResource(AddFlats.this,
											R.array.selectedmemberTypeArray,
											R.layout.spinr_txt);
							memberTypeAdapter
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							memberTypeSpinner
									.setAdapter(new NothingSelectedSpinnerAdapter1(
											memberTypeAdapter,
											R.layout.member_spinner_nothing_selected,
											AddFlats.this));

						} else {

							if (ownerType.equals("Individual Owner")
									|| ownerType.equals("Joint Owner")) {
								List<String> memsList = Arrays.asList(getResources().getStringArray(R.array.memberTypeOutNonmemberArray));
								ArrayAdapter<String> memberTypeAdapter = new ArrayAdapter<String>(AddFlats.this,
										R.layout.spinr_txt, memsList);
								memberTypeAdapter
										.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								memberTypeSpinner
										.setAdapter(new NothingSelectedSpinnerAdapter1(
												memberTypeAdapter,
												R.layout.member_spinner_nothing_selected,
												AddFlats.this));
							} else {
								ArrayAdapter<CharSequence> memberTypeAdapter = ArrayAdapter
										.createFromResource(AddFlats.this,
												R.array.memberTypeArray,
												R.layout.spinr_txt);
								memberTypeAdapter
										.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								memberTypeSpinner
										.setAdapter(new NothingSelectedSpinnerAdapter1(
												memberTypeAdapter,
												R.layout.member_spinner_nothing_selected,
												AddFlats.this));
							}
						}

						memberTypeSpinner.setEnabled(true);
						System.out
								.println("222222222222222222222222222222222222");
						if (ownerType == "Dependant"
								|| ownerType.equals("Dependant")) {
							reltionwithOwnerLay.setVisibility(View.VISIBLE);
							line6.setVisibility(View.VISIBLE);
						} else {
							reltionwithOwnerLay.setVisibility(View.GONE);
							line6.setVisibility(View.GONE);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						memberTypeSpinner.setEnabled(false);
					}
				});
		memberTypeSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						memberType = String.valueOf(memberTypeSpinner
								.getSelectedItem());
						if (memberType == "Committee member"
								|| memberType.equals("Committee member")) {
							memberType = "Committee_member";
						}
						if (memberType == "Non member"
								|| memberType.equals("Non member")) {
							memberType = "Non_members";
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		datewithOwnerEdit.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				if (v == datewithOwnerEdit)
					showDialog(DATE_DIALOG_FROMID);
				return false;
			}
		});

		lisecnseEndsOntxtEdit.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				if (v == lisecnseEndsOntxtEdit)
					showDialog(DATE_DIALOG_TOID);
				return false;
			}
		});

		reltionwithOwnerSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (realtionShipWithOwner == null || realtionShipWithOwner.equals("")) {
							realtionShipWithOwner = "";
						}
						realtionShipWithOwner = String
								.valueOf(reltionwithOwnerSpinner
										.getSelectedItem());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

		new SocietyNamees().execute();

		finishTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flatNum = flatNumEdit.getText().toString();
				relationShipDateStr = datewithOwnerEdit.getText().toString();
				liscenseDateStr = lisecnseEndsOntxtEdit.getText().toString();

				if (avenueName == null || avenueName == "null"
						|| avenueName == "" || avenueName.length() == 0) {
					Constants.showSnack(v, "Please select Avenue Name!", "OK");
				} else if (societyName == null || societyName == "null"
						|| societyName == "" || societyName.length() == 0) {
					Constants.showSnack(v, "Please select Society Name!",
							"OK");
				} else if (buildingNum == null || buildingNum == "null"
						|| buildingNum.length() == 0) {

					Constants.showSnack(v, "Please enter Building Number!",
							"OK");
				} else if (flatNum == null || flatNum == "null"
						|| flatNum.length() == 0) {
					Constants.showSnack(v, "Please enter Flat Number!", "OK");
				}

				else if (flatType == null || flatType == "null"
						|| flatType == "" || flatType.length() == 0) {
					Constants.showSnack(v, "Please select Flat Type!", "OK");
				} else if (ownerType == null || ownerType == "null"
						|| ownerType == "" || ownerType.length() == 0) {
					Constants.showSnack(v, "Please select Owner Type!", "OK");
				} else if (memberType == null || memberType == "null"
						|| memberType.length() == 0) {
					Constants.showSnack(v, "Please enter Member Type!", "OK");
				} else if (ownerType == "Dependant Owner"
						&& ownerType.equals("Dependant Owner")
						&& realtionShipWithOwner == null
						&& realtionShipWithOwner.equals(null)
						&& realtionShipWithOwner == "null") {
					Constants.showSnack(v,
							"Please select a relation with owner!",
							"OK");

				} else if (ownerType == "Licensee Owner"
						|| ownerType.equals("Licensee Owner")&&liscenseDateStr.equals("") || liscenseDateStr == "") {
					System.out.println(liscenseDateStr);
					Constants.showSnack(v,
							"select a license end date!",
							"OK");
				} else {

					new AddFlatDetail(v).execute();

				}

			}
		});

	}

	public class AddFlatDetail extends AsyncTask<Void, Void, Void> {
		View view;
		public AddFlatDetail(View v) {
			view=v;
		}

		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			finishTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonAddFlat = new JSONObject();
			try {
				JSONObject jsonFlat = new JSONObject();
				jsonFlat.put("avenue_name", avenueName);
				jsonFlat.put("societyid", societyName);
				jsonFlat.put("buildingid", buildingNum);
				jsonFlat.put("flat_number", flatNum);
				jsonFlat.put("flat_type", flatType);
				jsonFlat.put("ownertypeid", ownerType);
				System.out.println(memberType);
				jsonFlat.put("member_type", memberType);
				jsonFlat.put("tenurestart", liscenseDateStr);
				jsonFlat.put("tenureend", liscenseDateStr);
				jsonFlat.put("relationshipid", realtionShipWithOwner);
				jsonFlat.put("user_id", userPref.getString("USERID", "NV"));
				jsonAddFlat.put("gclife_registration_flatdetail", jsonFlat);

				System.out.println(jsonAddFlat + "PUSHHHHHHHHHHHHHHHHHHHHHhh");
				try {
					jsonFlatResults = SignUpPost
							.addFlats(jsonAddFlat, GclifeApplication.HOSTNAME);
					System.out.println("RESPONSE :::::::::::::::::::::"
							+ jsonFlatResults);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (jsonFlatResults != null) {
				if (jsonFlatResults.has("errors")) {
					try {
						JSONObject jsonError = jsonFlatResults
								.getJSONObject("errors");
						if (jsonError.has("flat")) {
							Constants.showSnack(view,
									"Invalid flat number!",
									"OK");
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					pDialog.setVisibility(View.GONE);
					finishTxt.setVisibility(View.VISIBLE);

				} else {
					pDialog.setVisibility(View.GONE);
					finishTxt.setVisibility(View.VISIBLE);

					Gson gson = new GsonBuilder().create();
					GclifeApplication.user = gson.fromJson(
							jsonFlatResults.toString(), UserDetailsPojo.class);

					Gson gsonn = new Gson();
					String json = gsonn.toJson(GclifeApplication.user);

					editor.putString("USER_DATA", json);

					editor.commit();

					Intent otp = new Intent(getApplicationContext(),
							UserProfile.class);
					otp.putExtra("EACH_USER_DET", "");
					startActivity(otp);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
				}

			} else {
				pDialog.setVisibility(View.GONE);
				finishTxt.setVisibility(View.VISIBLE);

				Constants.showSnack(view,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}

	public class SocietyNamees extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog1.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = SocietyNameGet.callSocietyList(GclifeApplication.HOSTNAME);
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

						System.out.println(societyMap);

					}
					ArrayAdapter<String> associtationAdapter = new ArrayAdapter<String>(
							AddFlats.this, R.layout.spinr_txt,
							listAssociation);

					associtationAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					avenueSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									associtationAdapter,
									R.layout.avenue_spinner_nothing_selected,
									AddFlats.this));
					pDialog1.setVisibility(View.GONE);
					societyNameSpinner.setEnabled(true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				Constants.showSnack(snackLay,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
				pDialog1.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);

		switch (id) {
		case DATE_DIALOG_FROMID:
			return new DatePickerDialog(this, mDateFROMSetListener, cyear,
					cmonth, cday);
		}
		switch (id) {
		case DATE_DIALOG_TOID:
			return new DatePickerDialog(this, mDateTOSetListener, cyear,
					cmonth, cday);
		}

		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateFROMSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String day = String.valueOf(dayOfMonth);
			String month = String.valueOf(monthOfYear + 1);
			System.out.println(month);
			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}
			datewithOwnerEdit.setText(day + "-" + month + "-"
					+ String.valueOf(year));
		}
	};
	private DatePickerDialog.OnDateSetListener mDateTOSetListener = new DatePickerDialog.OnDateSetListener() {
		// onDateSet method
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			String day = String.valueOf(dayOfMonth);
			String month = String.valueOf(monthOfYear + 1);
			System.out.println(month);
			if (day.length() == 1) {
				day = "0" + day;
			}
			if (month.length() == 1) {
				month = "0" + month;
			}
			lisecnseEndsOntxtEdit.setText(day + "-" + month + "-"
					+ String.valueOf(year));
		}
	};

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
