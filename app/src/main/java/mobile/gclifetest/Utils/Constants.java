package mobile.gclifetest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import mobile.gclifetest.activity.R;

/**
 * Created by MRaoKorni on 8/1/2016.
 */
public class Constants {
    public static String[] homeMenuList = {"GC News", "Notice Board",
            "My Society", "Ideas", "Write", "Photos", "Videos", "Friends",
            "Imp Contacts", "Admin"};
    public static String paymentTypes[] = {"Cash", "Demand Draft", "Cheque", "Electronic Transfer (NEFT/IMPS/Net Banking)", "Debit Card", "Credit Card", "Other"};
    public static int[] homeMenuImages = {R.drawable.icon_news,
            R.drawable.icon_noticeboard, R.drawable.icon_society,
            R.drawable.icon_ideas, R.drawable.icon_write,
            R.drawable.icon_photos, R.drawable.icon_videos,
            R.drawable.icon_friends, R.drawable.icon_contacts, R.drawable.admin};

    public static final String PREFS_NAME = "USER";

    public static void closeSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToast(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setRobotoRegularFont(Context context, TextView txt) {
        Typeface typeRobotoRegular = Typeface.createFromAsset(context.getAssets(),
                "fonts/RobotoRegular.ttf");
        txt.setTypeface(typeRobotoRegular);
    }

    public static void setRobotoLightFont(Context context, TextView txt) {
        Typeface typeRobotoLight = Typeface.createFromAsset(context.getAssets(),
                "fonts/RobotoLight.ttf");
        txt.setTypeface(typeRobotoLight);
    }

    public static void setPreferences(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreference(Context parent, String key, String defValue) {
        SharedPreferences preferences = parent.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getString(key, defValue);
    }

    public static void clearPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void showSnack(View view, String stringMsg, String ok) {
        Snackbar snackbar = Snackbar
                .make(view, stringMsg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
