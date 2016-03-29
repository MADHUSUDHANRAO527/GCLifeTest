package mobile.gclifetest.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.Utils.MyApplication;
import mobile.gclifetest.PojoGson.UserDetailsPojo;
import mobile.gclifetest.Utils.InternetConnectionDetector;
import mobile.gclifetest.Utils.NothingSelectedSpinnerAdapter1;
import mobile.gclifetest.http.SignUpPost;
import mobile.gclifetest.http.SocietyNameGet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Register extends BaseActivity {
	TextView nextTxt;
	Typeface typefaceLight;
	EditText userNameEdit, emailEdit, passwordEdit, mobileNumEdit;
	SharedPreferences userPref;
	Editor editor;
	String userName, email, password, mobileNum;
	String checkPatternId = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	LinearLayout loginTab, flatTab, flatLay;
	RelativeLayout layLogin, reltionwithOwnerLay,dateToLay, line6;
	TextView finishTxt, loginDetailsTxt, flatDteailTxt;
	Spinner avenueSpinner, societyNameSpinner, buildingSpinner,
			flatTypeSpinner, ownerTypeSpinner, memberTypeSpinner,
			reltionwithOwnerSpinner;
	String avenueName, societyName, buildingNum, flatType, ownerType,
			memberType, flatNum, liscenseDateStr = "",
			realtionShipWithOwner = "";
	EditText datewithOwnerEdit, lisecnseEndsOntxtEdit, flatNumEdit;
	ProgressBarCircularIndeterminate pDialog;
	JSONObject jsonSignupResult;
	// View loginViewColor, flatDetailViewColor;
	static final int DATE_DIALOG_FROMID = 0;
	static final int DATE_DIALOG_TOID = 1;
	JSONArray jsonResultArry;
	ArrayAdapter<CharSequence> memberTypeAdapter;
	Map<String, List<String>> societyMap = new HashMap<String, List<String>>();
	Map<String, List<String>> buildingMap = new HashMap<String, List<String>>();
	InternetConnectionDetector netConn;
	Boolean isInternetPresent = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		loginTab = (LinearLayout) findViewById(R.id.loginTab);
		flatTab = (LinearLayout) findViewById(R.id.flatTab);
		layLogin = (RelativeLayout) findViewById(R.id.layLogin);
		reltionwithOwnerLay = (RelativeLayout) findViewById(R.id.reltionwithOwnerLay);
		dateToLay= (RelativeLayout) findViewById(R.id.dateToLay);
		line6 = (RelativeLayout) findViewById(R.id.line6);
		flatLay = (LinearLayout) findViewById(R.id.flatDetailsLay);
		finishTxt = (TextView) findViewById(R.id.finishTxt);
		loginDetailsTxt = (TextView) findViewById(R.id.loginDetailsTxt);
		flatDteailTxt = (TextView) findViewById(R.id.flatDteailTxt);

		flatDteailTxt.setTextColor(Color.parseColor("#C8C8C8"));

		avenueSpinner = (Spinner) findViewById(R.id.avenueSpin);
		societyNameSpinner = (Spinner) findViewById(R.id.societySpin);
		flatNumEdit = (EditText) findViewById(R.id.flatNoEdit);
		buildingSpinner = (Spinner) findViewById(R.id.buildingSpin);
		flatTypeSpinner = (Spinner) findViewById(R.id.flatTypeSpin);
		ownerTypeSpinner = (Spinner) findViewById(R.id.ownerSpin);
		memberTypeSpinner = (Spinner) findViewById(R.id.memberSpin);
		reltionwithOwnerSpinner = (Spinner) findViewById(R.id.reltionwithOwnerSpinner);
		datewithOwnerEdit = (EditText) findViewById(R.id.datewithOwnerEdit);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		lisecnseEndsOntxtEdit = (EditText) findViewById(R.id.lisecnseEndsOntxtEdit);

		// setting Action Bar

		setUpActionBar("GC Life Registeration");
		getSupportActionBar().setElevation(0);

		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		editor = userPref.edit();
		editor.clear();
		editor.commit();

		typefaceLight = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "fonts/RobotoLight.ttf");
		netConn = new InternetConnectionDetector(this);
		isInternetPresent = netConn.isConnectingToInternet();

		nextTxt = (TextView) findViewById(R.id.nextTxt);
		// cancellTxt = (TextView) findViewById(R.id.cancellTxt);

		userNameEdit = (EditText) findViewById(R.id.userName);
		emailEdit = (EditText) findViewById(R.id.mailEdit);
		passwordEdit = (EditText) findViewById(R.id.passEdit);
		mobileNumEdit = (EditText) findViewById(R.id.mobileNumEdit);

		nextTxt.setTypeface(typefaceLight);
		// cancellTxt.setTypeface(typefaceLight);
		userNameEdit.setTypeface(typefaceLight);
		emailEdit.setTypeface(typefaceLight);
		passwordEdit.setTypeface(typefaceLight);
		mobileNumEdit.setTypeface(typefaceLight);

		userNameEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				editor.putString("username", userNameEdit.getText().toString());
				editor.commit();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		emailEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				editor.putString("email", emailEdit.getText().toString());
				editor.commit();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		passwordEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				editor.putString("password", passwordEdit.getText().toString());
				editor.commit();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		mobileNumEdit.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable s) {
				editor.putString("mobileNum", mobileNumEdit.getText()
						.toString());
				editor.commit();
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
			}
		});

		nextTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userName = userNameEdit.getText().toString();
				email = emailEdit.getText().toString();
				password = passwordEdit.getText().toString();
				mobileNum = mobileNumEdit.getText().toString();
				if (userName == null || userName == "null" || userName == ""
						|| userName.length() == 0) {
					showSnack(Register.this,
							"Please enter a valid username!",
							"OK");
				} else if (email == null || email == "null" || email == ""
						|| email.length() == 0) {
					showSnack(Register.this,
							"Please enter your email!",
							"OK");
				} else if (email != null && !email.isEmpty()
						&& !email.matches(checkPatternId)) {

					showSnack(Register.this,
							"Please enter a valid email address!",
							"OK");
				} else if (password == null || password == "null"
						|| password == "" || password.length() == 0) {
					showSnack(Register.this,
							"Please enter your password!",
							"OK");
				} else if (password.length() < 6) {
					showSnack(Register.this,
							"Your password must be at least 6 characters long!",
							"OK");
				} else if (mobileNum == null || mobileNum == "null"
						|| mobileNum == "" || mobileNum.length() == 0) {
					showSnack(Register.this,
							"Please enter mobile number!",
							"OK");
				} else if (mobileNum.length() < 10) {
					showSnack(Register.this,
							"Please enter proper mobile number!",
							"OK");
				} else {

					if (isInternetPresent) {
						loginDetailsTxt.setTextColor(Color
								.parseColor("#C8C8C8"));
						flatDteailTxt.setTextColor(Color.parseColor("#ffffff"));

						editor.putString("username", userName);
						editor.putString("password", password);
						editor.putString("mobileNum", mobileNum);
						editor.putString("email", email);
						editor.commit();
						flatLay.setVisibility(View.VISIBLE);
						layLogin.setVisibility(View.GONE);
					} else {
						showSnack(Register.this,
								"Please check your internet settings!",
								"OK");
					}

				}

			}
		});
		flatNumEdit.setCursorVisible(false);
		flatNumEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flatNumEdit.setCursorVisible(true);
			}
		});
		loginTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flatLay.setVisibility(View.GONE);
				layLogin.setVisibility(View.VISIBLE);

				flatDteailTxt.setTextColor(Color.parseColor("#C8C8C8"));
				loginDetailsTxt.setTextColor(Color.parseColor("#ffffff"));
			}
		});
		flatTab.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				System.out.println(userPref.getString("username", "NV"));
				userName = userNameEdit.getText().toString();
				email = emailEdit.getText().toString();
				password = passwordEdit.getText().toString();
				mobileNum = mobileNumEdit.getText().toString();
				if (userName == null || userName == "null" || userName == ""
						|| userName.length() == 0) {
					showSnack(Register.this,
							"Please enter a valid username!",
							"OK");
				} else if (password == null || password == "null"
						|| password == "" || password.length() == 0) {
					showSnack(Register.this,
							"Please enter your password!",
							"OK");
				} else if (password.length() < 6) {
					showSnack(Register.this,
							"Your password must be at least 6 characters long!",
							"OK");
				} else if (mobileNum == null || mobileNum == "null"
						|| mobileNum == "" || mobileNum.length() == 0) {
					showSnack(Register.this,
							"Please enter mobile number!",
							"OK");
				} else if (mobileNum.length() < 10) {
					showSnack(Register.this,
							"Please enter proper mobile number!",
							"OK");
				} else if (email == null || email == "null" || email == ""
						|| email.length() == 0) {
					showSnack(Register.this,
							"Please enter your email!",
							"OK");
				} else if (email != null && !email.isEmpty()
						&& !email.matches(checkPatternId)) {
					
					showSnack(Register.this,
							"Please enter a valid email address!",
							"OK");
				}else {
					flatLay.setVisibility(View.VISIBLE);

					flatDteailTxt.setTextColor(Color.parseColor("#ffffff"));
					loginDetailsTxt.setTextColor(Color.parseColor("#C8C8C8"));

					layLogin.setVisibility(View.GONE);

				}
			}
		});

		finishTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flatNum = flatNumEdit.getText().toString();
				liscenseDateStr = lisecnseEndsOntxtEdit.getText().toString();
				System.out.println(ownerType + realtionShipWithOwner
						+ "  ________________________");
				if (avenueName == null || avenueName == "null"
						|| avenueName == "" || avenueName.length() == 0) {
					showSnack(Register.this,
							"Please select Avenue Name!",
							"OK");
				} else if (societyName == null || societyName == "null"
						|| societyName == "" || societyName.length() == 0) {
					showSnack(Register.this,
							"Please select Society Name!",
							"OK");
				} else if (buildingNum == null || buildingNum == "null"
						|| buildingNum.length() == 0) {
					showSnack(Register.this,
							"Please enter Building Number!",
							"OK");
				} else if (flatNum == null || flatNum == "null"
						|| flatNum.length() == 0) {
					showSnack(Register.this,
							"Please enter Flat Number!",
							"OK");
				}

				else if (flatType == null || flatType == "null"
						|| flatType == "" || flatType.length() == 0) {
					showSnack(Register.this,
							"Please select Flat Type!",
							"OK");
				} else if (ownerType == null || ownerType == "null"
						|| ownerType == "" || ownerType.length() == 0) {
					showSnack(Register.this,
							"Please select Owner Type!",
							"OK");
				} else if (memberType == null || memberType == "null"
						|| memberType.length() == 0) {
					System.out.println(memberType);
					showSnack(Register.this,
							"Please enter Member Type!",
							"OK");
				}

				else if (ownerType == "Dependant Owner"
						&& ownerType.equals("Dependant Owner")
						&& realtionShipWithOwner == null
						&& realtionShipWithOwner.equals(null)
						&& realtionShipWithOwner == "null") {
					showSnack(Register.this,
							"Please select a relation with owner!",
							"OK");

				} else if (ownerType == "Licensee Owner"
						|| ownerType.equals("Licensee Owner")&&liscenseDateStr.equals("") || liscenseDateStr == "") {
					System.out.println(liscenseDateStr);
					showSnack(Register.this,
							"select a license end date!",
							"OK");
				} 
				
				else {

					if (isInternetPresent) {
						if(liscenseDateStr==""||liscenseDateStr.equals("")){
							liscenseDateStr="12-12-2015";
						}
						new SignUp().execute();
					} else {
						showSnack(Register.this,
								"Please check your internet settings!",
								"OK");
					}

				}

			}
		});

		ArrayAdapter<CharSequence> avenueAdapter = ArrayAdapter
				.createFromResource(this, R.array.avenueArray,
						R.layout.spinr_txt);
		avenueAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		avenueSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				avenueAdapter, R.layout.avenue_spinner_nothing_selected,
				Register.this));

		ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
				.createFromResource(this, R.array.buildingNameArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> flatTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.flatsTypeArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> ownerTypeAdapter = ArrayAdapter
				.createFromResource(this, R.array.ownerTypeArray,
						R.layout.spinr_txt);
		memberTypeAdapter = ArrayAdapter.createFromResource(this,
				R.array.memberTypeArray, R.layout.spinr_txt);
		ArrayAdapter<CharSequence> sociAdapter = ArrayAdapter
				.createFromResource(this, R.array.societyNameArray,
						R.layout.spinr_txt);
		ArrayAdapter<CharSequence> reltionwithOwnerAdapter = ArrayAdapter
				.createFromResource(this, R.array.reltionwithOwnerTypeArray,
						R.layout.spinr_txt);

		buildingAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		flatTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ownerTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		memberTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sociAdapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
		reltionwithOwnerAdapter
				.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);

		societyNameSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				sociAdapter, R.layout.society_spinner_nothing_selected,
				Register.this));

		buildingSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				buildingAdapter, R.layout.building_spinner_nothing_selected,
				this));

		flatTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				flatTypeAdapter, R.layout.flattype_spinner_nothing_selected,
				this));

		ownerTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				ownerTypeAdapter, R.layout.ownertype_spinner_nothing_selected,
				this));
		memberTypeSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				memberTypeAdapter, R.layout.member_spinner_nothing_selected,
				this));
		reltionwithOwnerSpinner.setAdapter(new NothingSelectedSpinnerAdapter1(
				reltionwithOwnerAdapter,
				R.layout.relationship_spinner_nothing_selected, this));
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
							Register.this, android.R.layout.simple_list_item_1,
							societylist);

					sociAdapter
							.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
					societyNameSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									sociAdapter,
									R.layout.society_spinner_nothing_selected,
									Register.this));

					ArrayAdapter<CharSequence> buildingAdapter = ArrayAdapter
							.createFromResource(Register.this,
									R.array.buildingNameArray,
									R.layout.spinr_txt);

					buildingSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									buildingAdapter,
									R.layout.building_spinner_nothing_selected,
									Register.this));

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
									Register.this,
									android.R.layout.simple_list_item_1,
									buildinglist);

							buildAdapter
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							buildingSpinner
									.setAdapter(new NothingSelectedSpinnerAdapter1(
											buildAdapter,
											R.layout.building_spinner_nothing_selected,
											Register.this));
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

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				flatTypeSpinner.setEnabled(false);
			}
		});
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
									.createFromResource(Register.this,
											R.array.selectedmemberTypeArray,
											R.layout.spinr_txt);
							memberTypeAdapter
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							memberTypeSpinner
									.setAdapter(new NothingSelectedSpinnerAdapter1(
											memberTypeAdapter,
											R.layout.member_spinner_nothing_selected,
											Register.this));

						} else {

							ArrayAdapter<CharSequence> memberTypeAdapter = ArrayAdapter
									.createFromResource(Register.this,
											R.array.memberTypeArray,
											R.layout.spinr_txt);
							memberTypeAdapter
									.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							memberTypeSpinner
									.setAdapter(new NothingSelectedSpinnerAdapter1(
											memberTypeAdapter,
											R.layout.member_spinner_nothing_selected,
											Register.this));
						}
						memberTypeSpinner.setEnabled(true);
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

		reltionwithOwnerSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						realtionShipWithOwner = String
								.valueOf(reltionwithOwnerSpinner
										.getSelectedItem());

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
		new SocietyNames().execute();

	}

	public class SocietyNames extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			// pDialog.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				jsonResultArry = SocietyNameGet.callSocietyList(MyApplication.HOSTNAME);
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
						List<String> associationList = new ArrayList<String>(
								listSociety);
						societyMap.put(associationName, associationList);

						System.out.println(societyMap);

					}
					ArrayAdapter<String> associtationAdapter = new ArrayAdapter<String>(
							Register.this, android.R.layout.simple_list_item_1,
							listAssociation);

					associtationAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					avenueSpinner
							.setAdapter(new NothingSelectedSpinnerAdapter1(
									associtationAdapter,
									R.layout.avenue_spinner_nothing_selected,
									Register.this));

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				showSnack(Register.this,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
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

	public class SignUp extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			finishTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonSignUp = new JSONObject();
			try {
				jsonSignUp.put("mobile", userPref.getString("mobileNum", "NV"));
				jsonSignUp.put("email", userPref.getString("email", "NV"));
				jsonSignUp
						.put("username", userPref.getString("username", "NV"));
				jsonSignUp
						.put("password", userPref.getString("password", "NV"));
				jsonSignUp.put("device_token",MyApplication.gcmTokenid);
				jsonSignUp.put("password_confirmation",
						userPref.getString("password", "NV"));
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

				jsonSignUp.put("gclife_registration_flatdetails", jsonFlat);

				JSONObject userJson = new JSONObject();
				userJson.put("user", jsonSignUp);
				System.out.println(userJson + "PUSHHHHHHHHHHHHHHHHHHHHHhh");
				try {
					jsonSignupResult = SignUpPost.makeRequestRegister(userJson,
							MyApplication.HOSTNAME);
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
			if (jsonSignupResult != null) {

				try {
					if (jsonSignupResult.has("errors")) {
						JSONObject jsonError = jsonSignupResult
								.getJSONObject("errors");

						if (jsonError.has("email")) {
							showSnack(Register.this,
									"email has already been taken!",
									"OK");
							
							flatLay.setVisibility(View.GONE);
							layLogin.setVisibility(View.VISIBLE);
							emailEdit.requestFocus();
							flatDteailTxt.setTextColor(Color.parseColor("#C8C8C8"));
							loginDetailsTxt.setTextColor(Color.parseColor("#ffffff"));
						} else if (jsonError.has("mobile")) {
							showSnack(Register.this,
									"mobile no. has already been taken!",
									"OK");
							flatLay.setVisibility(View.GONE);
							layLogin.setVisibility(View.VISIBLE);
							mobileNumEdit.requestFocus();
							flatDteailTxt.setTextColor(Color.parseColor("#C8C8C8"));
							loginDetailsTxt.setTextColor(Color.parseColor("#ffffff"));
						} else if (jsonError.has("flat")) {
							showSnack(Register.this,
									"Invalid flat number!",
									"OK");
						}

						pDialog.setVisibility(View.GONE);
						finishTxt.setVisibility(View.VISIBLE);

					} else {
						pDialog.setVisibility(View.GONE);
						finishTxt.setVisibility(View.VISIBLE);

						Gson gson = new GsonBuilder().create();
						MyApplication.user = gson.fromJson(
								jsonSignupResult.toString(),
								UserDetailsPojo.class);

						Gson gsonn = new Gson();
						String json = gsonn.toJson(MyApplication.user);

						try {
							editor.putString("USER_DATA", json);
							editor.putString("USERID",
									jsonSignupResult.getString("id"));
							editor.putString("OTP",
									jsonSignupResult.getString("otp"));
							editor.commit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Intent otp = new Intent(getApplicationContext(),
								OTP.class);
						startActivity(otp);
						overridePendingTransition(R.anim.slide_in_left,
								R.anim.slide_out_left);
						showSnack(Register.this,
								"OTP has sent to your mobile!",
								"OK");
					}

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else {
				pDialog.setVisibility(View.GONE);
				finishTxt.setVisibility(View.VISIBLE);

				showSnack(Register.this,
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
	void showSnack(Register login, String stringMsg, String ok) {
		new SnackBar(Register.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}
