package mobile.gclifetest.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static final String logerFileLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Gclife" + File.separator + "Logs";
    public static final String AllLogerfileName = "/All_logs";
    public static final String flurryApiKey = "6MJDT2W9CSR2SGXTFCNB";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
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

    public static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {

            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    public static String encodeString(String str) throws UnsupportedEncodingException {
     //   byte[] encodeValue = Base64.encode(str.getBytes(), Base64.DEFAULT);
        return str;
    }

    public static String decodeString(String str) throws UnsupportedEncodingException {
      //  byte[] decodeValue = Base64.decode(str, Base64.DEFAULT);
        return str;
    }

    public static boolean isFileSizeAcceptable(String videoPath) {

        boolean isAcceptable = false;
        float ACCEPTABLE_FILE_SIZE_IN_MB = 100;
        double CONVERSION_FACTOR = 1024.0 * 1024.0; //To convert from bytes to megabytes

        double mFileSize = (double) new File(videoPath).length(); //file length is in bytes
        mFileSize /= CONVERSION_FACTOR;
        if (mFileSize < ACCEPTABLE_FILE_SIZE_IN_MB) {
            isAcceptable = true;
        }
        return isAcceptable;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
