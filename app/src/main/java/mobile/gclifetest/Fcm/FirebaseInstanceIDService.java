package mobile.gclifetest.Fcm;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

/**
 * Created by Madhu on 31/05/17.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService{
    private static final String REG_TOKEN = "FCM_TOKEN";
    @Override
    public void onTokenRefresh() {
       String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, "onTokenRefresh: "+fcmToken);
        //sendRegistrationToServer(refreshedToken);
        storeRegIdInPref(fcmToken);
    }
    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("FCM_TOKEN", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("fcm_token", token);
        editor.apply();
    }
}
