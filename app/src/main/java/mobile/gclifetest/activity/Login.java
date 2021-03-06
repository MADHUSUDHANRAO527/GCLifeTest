package mobile.gclifetest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import mobile.gclifetest.http.SignUpPost;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.GclifeApplication;
import mobile.gclifetest.utils.InternetConnectionDetector;

public class Login extends BaseActivity {
	TextView login, signUp, loginwithEmailTxt,forgotPasswordTxt;
	String email, password;
	EditText emailEdit, passwordEdit;
	JSONObject jsonSignInResult;
	SharedPreferences userPref,fcmPref;
	SharedPreferences.Editor editor;
	android.support.v7.app.ActionBar actionBar;
	Typeface typefaceLight;
	ProgressBarCircularIndeterminate pDialog;
	InternetConnectionDetector netConn;
	Boolean isInternetPresent = false;
	RelativeLayout snackLay;
    public static boolean clearLogs = true;
	String fcmToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		login = (TextView) findViewById(R.id.loginTxt);
		signUp = (TextView) findViewById(R.id.signUpTxt);
		forgotPasswordTxt = (TextView) findViewById(R.id.forgot_password_txt);

		loginwithEmailTxt = (TextView) findViewById(R.id.loginwithEmailTxt);
		emailEdit = (EditText) findViewById(R.id.emailSignIn);
		passwordEdit = (EditText) findViewById(R.id.passwordSignIn);
		pDialog = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		snackLay=(RelativeLayout)findViewById(R.id.snackLay);
		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		fcmPref = getSharedPreferences("FCM_TOKEN",MODE_PRIVATE);
		editor = userPref.edit();

		typefaceLight = Typeface.createFromAsset(getAssets(),
				"fonts/RobotoLight.ttf");
		fcmToken = fcmPref.getString("fcm_token","NV");
		if(fcmToken.equals("NV"))
		{
			fcmToken = FirebaseInstanceId.getInstance().getToken();
			SharedPreferences pref = getApplicationContext().getSharedPreferences("FCM_TOKEN", 0);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("fcm_token", fcmToken);
			editor.apply();
		}
		login.setTypeface(typefaceLight);
		signUp.setTypeface(typefaceLight);
		emailEdit.setTypeface(typefaceLight);
		passwordEdit.setTypeface(typefaceLight);
		loginwithEmailTxt.setTypeface(typefaceLight);

		//emailEdit.setText("m@mail.com");
		//passwordEdit.setText("123456");
        netConn = new InternetConnectionDetector(this);
        isInternetPresent = netConn.isConnectingToInternet();

		if (isInternetPresent) {

		} else {
			Constants.showSnack(snackLay, "Please check network connection!", "OK");

		}
		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				email = emailEdit.getText().toString();
				password = passwordEdit.getText().toString();
				if (email == null || email == "null" || email == ""
						|| email.length() == 0) {

					Constants.showSnack(v, "Please enter your email!", "OK");
				} else if (password == null || password == "null"
						|| password == "" || password.length() == 0) {
					Constants.showSnack(v, "Please enter your password!", "OK");
				} else if (password.length() < 6) {
					Constants.showSnack(
							v,
							"Your password must be at least 6 characters long!",
							"OK");
				} else {
					netConn = new InternetConnectionDetector(Login.this);
					isInternetPresent = netConn.isConnectingToInternet();
					if (isInternetPresent) {
						new SignIn().execute();
					} else {
						Constants.showSnack(v,
								"Please check network connection!", "OK");

					}

				}

			}
		});
		signUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isInternetPresent) {
					Intent regi = new Intent(Login.this, Register.class);
					regi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(regi);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);
				} else {
					Constants.showSnack(v, "Please check network connection!",
							"OK");

				}

			}
		});
		forgotPasswordTxt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://meljol.tech:3000/users/password/new"));
                startActivity(browserIntent);
			}
		});
	}

	public class SignIn extends AsyncTask<Void, Void, Void> {
		@Override
		protected void onPreExecute() {
			pDialog.setVisibility(View.VISIBLE);
			login.setVisibility(View.INVISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			JSONObject jsonSignUp = new JSONObject();
			try {
				jsonSignUp.put("email", email);
				jsonSignUp.put("password", password);
				jsonSignUp.put("device_token", fcmToken);
				Log.d("FCM_TOKEN", "LoginCall: "+fcmToken);
				JSONObject userJson = new JSONObject();
				userJson.put("user", jsonSignUp);
				try {
					jsonSignInResult = SignUpPost.makeRequestLogin(userJson,
							GclifeApplication.HOSTNAME);
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
			if (jsonSignInResult != null) {

				if (jsonSignInResult.has("error")) {
					Constants.showSnack(snackLay,
							"You have entered wrong Email or Password!", "OK");
					pDialog.setVisibility(View.GONE);
					login.setVisibility(View.VISIBLE);

				} else {
					Intent dash = new Intent(Login.this, HomeActivity.class);
                    dash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dash);
					overridePendingTransition(R.anim.slide_in_left,
							R.anim.slide_out_left);

					Gson gson = new GsonBuilder().create();
					GclifeApplication.user = gson.fromJson(
							jsonSignInResult.toString(), UserDetailsPojo.class);

					Gson gsonn = new Gson();
					String json = gsonn.toJson(GclifeApplication.user);
					editor.putString("USER_DATA", json);
					editor.commit();

					try {
						editor.putString("USERID",
								jsonSignInResult.getString("id"));
						editor.commit();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					pDialog.setVisibility(View.VISIBLE);
					login.setVisibility(View.INVISIBLE);
				}

			} else {
				pDialog.setVisibility(View.GONE);
				login.setVisibility(View.VISIBLE);
				Constants.showSnack(snackLay,
						"Oops! Something went wrong. Please wait a moment!",
						"OK");
			}
		}
	}

	private static final int TIME_INTERVAL = 10000; // # milliseconds, desired
	private long mBackPressed;

	@Override
	public void onBackPressed() {
		if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
		} else {
			Toast.makeText(this, "Please Tap BACK again to exit", Toast.LENGTH_SHORT).show();
		}

		mBackPressed = System.currentTimeMillis();
	}


    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().hide();
    }

}
