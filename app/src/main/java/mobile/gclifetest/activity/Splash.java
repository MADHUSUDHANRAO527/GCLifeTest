package mobile.gclifetest.activity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.gson.Gson;

import mobile.gclifetest.PojoGson.UserDetailsPojo;
import mobile.gclifetest.Utils.InternetConnectionDetector;

public class Splash extends Activity {
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
        if (jsonUser != "NV" || !jsonUser.equals("NV")) {
            user = gson.fromJson(jsonUser, UserDetailsPojo.class);
            Intent home = new Intent(Splash.this, HomeApp.class);
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
                    Intent regi = new Intent(Splash.this, Register.class);
                    startActivity(regi);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                } else {
                    showSnack(Splash.this, "Please check network connection!",
                            "OK");

                }

            }
        });
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (isInternetPresent) {
                    Intent regi = new Intent(Splash.this, Login.class);
                    startActivity(regi);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                } else {
                    showSnack(Splash.this, "Please check network connection!",
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
            showSnack(Splash.this, "Press again to close app!", "OK");
        }

        mBackPressed = System.currentTimeMillis();
    }

    void showSnack(Splash login, String stringMsg, String ok) {
        new SnackBar(Splash.this, stringMsg, ok, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        }).show();
    }
}
