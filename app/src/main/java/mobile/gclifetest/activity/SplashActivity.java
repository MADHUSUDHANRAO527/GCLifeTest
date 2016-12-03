package mobile.gclifetest.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import mobile.gclifetest.pojoGson.UserDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.InternetConnectionDetector;

public class SplashActivity extends Activity {
    public static UserDetailsPojo user;
    Gson gson;
    SharedPreferences userPref;
    TextView login, signUp;
    InternetConnectionDetector netConn;
    Boolean isInternetPresent = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash);
        login = (TextView) findViewById(R.id.loginTxt);
        signUp = (TextView) findViewById(R.id.signUpTxt);
        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        gson = new Gson();
        netConn = new InternetConnectionDetector(this);
        isInternetPresent = netConn.isConnectingToInternet();
        String jsonUser = userPref.getString("USER_DATA", "NV");
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        } else if (jsonUser != "NV" || !jsonUser.equals("NV")) {
            user = gson.fromJson(jsonUser, UserDetailsPojo.class);
            Intent home = new Intent(SplashActivity.this, HomeActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
        }
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isInternetPresent) {
                    Intent regi = new Intent(SplashActivity.this, Register.class);
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
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isInternetPresent) {
                    Intent regi = new Intent(SplashActivity.this, Login.class);
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
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
}
