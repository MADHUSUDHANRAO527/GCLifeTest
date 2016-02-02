package mobile.gclife.activity;

import java.util.Calendar;

import mobile.gclife.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclife.PojoGson.MyApplication;
import mobile.gclife.PojoGson.UserDetailsPojo;
import mobile.gclife.http.Hostname;
import mobile.gclife.http.SignUpPost;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class EditProfile extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	RelativeLayout dateToLay;
	static final int DATE_DIALOG_FROMID = 0;
	String hostname, userName, email, mobileNum, genderName, emeNum,
			occupation, dob, privacy = "false";
	TextView submitTxt;
	SharedPreferences userPref;
	ProgressBarCircularIndeterminate pDialog;
	EditText userNameEdit, emailEdit, mobileNumEdit, emeContaNum,
			occupatioEdit, dobEdit;
	JSONObject jsonSignupResult;
	Editor editor;
	UserDetailsPojo user;
	Switch privacySwitch;
	String checkPatternId = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_profile);
		dateToLay = (RelativeLayout) findViewById(R.id.dateToLay);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		dobEdit = (EditText) findViewById(R.id.dobEdit);
		submitTxt = (TextView) findViewById(R.id.submitTxt);

		userNameEdit = (EditText) findViewById(R.id.userName);
		emailEdit = (EditText) findViewById(R.id.mailEdit);
		mobileNumEdit = (EditText) findViewById(R.id.mobileNumEdit);

		emeContaNum = (EditText) findViewById(R.id.emeContaNum);
		occupatioEdit = (EditText) findViewById(R.id.occupationNumEdit);
		dobEdit = (EditText) findViewById(R.id.dobEdit);

		privacySwitch = (Switch) findViewById(R.id.privacySwitch);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Update Profile");

		Hostname host = new Hostname();
		hostname = host.globalVariable();

		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		editor = userPref.edit();

		Gson gson = new Gson();
		String jsonUser = userPref.getString("USER_DATA", "NV");
		user = gson.fromJson(jsonUser, UserDetailsPojo.class);

		userNameEdit.setText(user.getUsername());
		emailEdit.setText(user.getEmail());
		mobileNumEdit.setText(user.getMobile());

		emeContaNum.setText(user.getEmeNum());
		occupatioEdit.setText(user.getOccupation());
		dobEdit.setText(user.getDob());

		privacySwitch.setChecked(user.getPrivacy());

		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		getSupportActionBar().setElevation(0);
		dobEdit.setOnTouchListener(new OnTouchListener() {
			@SuppressWarnings("deprecation")
			public boolean onTouch(View v, MotionEvent event) {
				if (v == dobEdit)
					showDialog(DATE_DIALOG_FROMID);
				return false;
			}
		});
		submitTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userName = userNameEdit.getText().toString();
				email = emailEdit.getText().toString();
				mobileNum = mobileNumEdit.getText().toString();
				if (userName == null || userName == "null" || userName == ""
						|| userName.length() == 0) {
					showSnack(EditProfile.this,
							"Please enter a valid username!",
							"OK");
				} else if (mobileNum == null || mobileNum == "null"
						|| mobileNum == "" || mobileNum.length() == 0) {
					showSnack(EditProfile.this,
							"Please enter mobile number!",
							"OK");
				} else if (mobileNum.length() < 10) {
					showSnack(EditProfile.this,
							"Please enter proper mobile number!",
							"OK");
				} else if (email == null || email == "null" || email == ""
						|| email.length() == 0) {
					showSnack(EditProfile.this,
							"Please enter your email!",
							"OK");
				} else if (email != null && !email.isEmpty()
						&& !email.matches(checkPatternId)) {
					showSnack(EditProfile.this,
							"Please enter a valid email address!",
							"OK");
				} else {
					emeNum = emeContaNum.getText().toString();
					occupation = occupatioEdit.getText().toString();
					dob = dobEdit.getText().toString();
					emeNum = emeContaNum.getText().toString();
					occupation = occupatioEdit.getText().toString();
					dob = dobEdit.getText().toString();
					new UpdateProfile().execute();
				}

			}
		});
		privacySwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// do something, the isChecked will be
						// true if the switch is in the On position
						if (isChecked == true) {
							privacy = "true";
						} else {
							privacy = "false";
						}
					}
				});

	}

	private DatePickerDialog.OnDateSetListener dobListnder = new DatePickerDialog.OnDateSetListener() {
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
			dobEdit.setText(day + "-" + month + "-" + String.valueOf(year));
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		Calendar c = Calendar.getInstance();
		int cyear = c.get(Calendar.YEAR);
		int cmonth = c.get(Calendar.MONTH);
		int cday = c.get(Calendar.DAY_OF_MONTH);

		switch (id) {
		case DATE_DIALOG_FROMID:
			return new DatePickerDialog(this, dobListnder, cyear, cmonth, cday);
		}

		return null;
	}

	public class UpdateProfile extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			submitTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonSignUp = new JSONObject();
			try {
				jsonSignUp.put("user_id", user.getId());
				jsonSignUp.put("mobile", mobileNum);
				jsonSignUp.put("email", email);
				jsonSignUp.put("username", userName);
				jsonSignUp.put("emergency_contct_no", emeNum);
				jsonSignUp.put("occupation", occupation);
				jsonSignUp.put("gender", genderName);
				jsonSignUp.put("dob", dob);
				jsonSignUp.put("privacy", privacy);

				System.out.println(jsonSignUp + "PUSHHHHHHHHHHHHHHHHHHHHHhh");
				try {
					jsonSignupResult = SignUpPost.updateProfile(jsonSignUp,
							hostname);
					System.out.println(jsonSignUp + hostname);

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

				System.out.println("RESPONSE :::::::::::::::::::::"
						+ jsonSignupResult);
				if (jsonSignupResult.has("errors")) {

					showSnack(EditProfile.this,
							"email has already been taken!",
							"OK");
					pDialog.setVisibility(View.GONE);
					submitTxt.setVisibility(View.VISIBLE);

				} else {
					pDialog.setVisibility(View.GONE);
					submitTxt.setVisibility(View.VISIBLE);

					Gson gson = new GsonBuilder().create();
					MyApplication.user = gson.fromJson(
							jsonSignupResult.toString(), UserDetailsPojo.class);

					Gson gsonn = new Gson();
					String json = gsonn.toJson(MyApplication.user);

					editor.putString("USER_DATA", json);
					editor.commit();

					Intent otp = new Intent(getApplicationContext(),
							HomeApp.class);
					otp.putExtra("EACH_USER_DET", json);
					startActivity(otp);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
					showSnack(EditProfile.this,
							"Profile has been updated!",
							"OK");
				}

			} else {
				pDialog.setVisibility(View.GONE);
				submitTxt.setVisibility(View.VISIBLE);

				showSnack(EditProfile.this,
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
	void showSnack(EditProfile flats, String stringMsg, String ok) {
		new SnackBar(EditProfile.this, stringMsg, ok, new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		}).show();
	}
}