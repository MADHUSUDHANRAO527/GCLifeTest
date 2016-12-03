package mobile.gclifetest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.gclifetest.http.SignUpPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

public class OTP extends BaseActivity implements OnClickListener {
	TextView resendOtpTxt, submitTxt, otpTxt;
	Typeface typefaceLight;
	EditText otpEditxt;
	String otpStr;
	SharedPreferences userPref;
	SharedPreferences.Editor editor;
	JSONObject jsonResult;
	ProgressBarCircularIndeterminate pDialog;
	ProgressBarCircularIndeterminate pDialogResend;
	LinearLayout snackLay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_popup);
		resendOtpTxt = (TextView) findViewById(R.id.resendOtpTxt);
		submitTxt = (TextView) findViewById(R.id.submitTxt);
		otpEditxt = (EditText) findViewById(R.id.otpEditxt);
		otpTxt = (TextView) findViewById(R.id.otpTxt);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		pDialogResend = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarResend);
		snackLay=(LinearLayout)findViewById(R.id.snackLay);
		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		resendOtpTxt.setTypeface(typefaceLight);
		submitTxt.setTypeface(typefaceLight);
		otpTxt.setTypeface(typefaceLight);
		otpEditxt.setTypeface(typefaceLight);

		setUpActionBar("Enter OTP");

		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		editor = userPref.edit();
		System.out.println(userPref.getString("OTP", "NV")+"!!!!!!!!!!!!!!!!!!!!!!");
		otpEditxt.setText(userPref.getString("OTP", "NV"));

		resendOtpTxt.setOnClickListener(this);
		submitTxt.setOnClickListener(this);

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.resendOtpTxt:
			new ReSendOTP().execute();
			break;
		case R.id.submitTxt:
			otpStr = otpEditxt.getText().toString();
			if (otpStr == null || otpStr.equals("null") || otpStr == ""
					|| otpStr.equals("")) {
				Constants.showSnack(snackLay,
						"Please enter OTP!",
						"OK");
			} else {
				new SendOTP().execute();
			}

			break;
		default:
			break;
		}
	}

	public class SendOTP extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			submitTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				jsonResult = SignUpPost.sendOtp(MyApplication.HOSTNAME,
						userPref.getString("USERID", "NV"), otpStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (jsonResult != null) {

				if (jsonResult.has("errors")) {

					pDialog.setVisibility(View.INVISIBLE);
					submitTxt.setVisibility(View.VISIBLE);

					Constants.showSnack(snackLay,
							"You have entered wrong OTP!",
							"OK");

				} else {

					try {
						editor.putString("USERID", jsonResult.getString("id"));
						editor.putString("CREATED",
								jsonResult.getString("created_at"));
						editor.putString("EMAIL", jsonResult.getString("email"));
						editor.putString("OTP", jsonResult.getString("otp"));
						editor.putString("MOBILE",
								jsonResult.getString("mobile"));
						editor.putString("OTPFLAG",
								jsonResult.getString("otpflag"));
						editor.commit();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent dash = new Intent(OTP.this, HomeActivity.class);
					startActivity(dash);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
				}

			} else {
				pDialog.setVisibility(View.INVISIBLE);
				submitTxt.setVisibility(View.VISIBLE);
				Constants.showSnack(snackLay,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}

	public class ReSendOTP extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialogResend.setVisibility(View.VISIBLE);
			resendOtpTxt.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			try {
				jsonResult = SignUpPost.resendOtp(MyApplication.HOSTNAME,
						userPref.getString("USERID", "NV"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			if (jsonResult != null) {

				if (jsonResult.has("success")) {
					pDialogResend.setVisibility(View.INVISIBLE);
					resendOtpTxt.setVisibility(View.VISIBLE);
					Constants.showSnack(snackLay,
							"OTP has sent!",
							"OK");
				} else {
					pDialogResend.setVisibility(View.INVISIBLE);
					resendOtpTxt.setVisibility(View.VISIBLE);
					Constants.showSnack(snackLay,
							"Oops! Something went wrong. Please wait a moment!",
							"OK");
				}

			} else {
				pDialogResend.setVisibility(View.INVISIBLE);
				resendOtpTxt.setVisibility(View.VISIBLE);
				Constants.showSnack(snackLay,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}
}
