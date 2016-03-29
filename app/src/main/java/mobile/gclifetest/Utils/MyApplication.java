package mobile.gclifetest.Utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.parse.Parse;

import mobile.gclifetest.PojoGson.UserDetailsPojo;

public class MyApplication extends Application {

    public static MyApplication instance;

    public static UserDetailsPojo user;
    SharedPreferences userPref;
    public static String actiobarColor = "#000000";
    Gson gson;
    GoogleCloudMessaging gcm;
    String PROJECT_NUMBER = "682888148825";
    public static String gcmTokenid;
    public static RequestQueue queue;
    //HTTP Constants
    public static String HOSTNAME="http://52.34.160.74:3000/";
    ActionBar actionBar;
    public void onCreate() {
        super.onCreate();
        instance = this;
        queue = Volley.newRequestQueue(instance);
        Parse.enableLocalDatastore(instance);
        Parse.initialize(this, "W2Lq9fizjRE6ZeiJYxIH6dyZS59IL4FiyoRZ9aTp",
                "jW9WRFV1QJOR0em6hKsMeKUkqz9WCAzGiDehc60r");

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(false).cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).resetViewBeforeLoading()
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getBaseContext()).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)).denyCacheImageMultipleSizesInMemory()
                .discCacheSize(50 * 1024 * 1024).writeDebugLogs()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).build();
        ImageLoader.getInstance().init(config);

        userPref = getSharedPreferences("USER", MODE_PRIVATE);
        gson = new Gson();
        String jsonUser = userPref.getString("USER_DATA", "NV");

        // get GCM token
        getRegId();
    }

    private void getRegId() {
        // TODO Auto-generated method stub
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging
                                .getInstance(getApplicationContext());
                    }
                    gcmTokenid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=    " + gcmTokenid;
                    Log.i("GCM", msg);

                    System.out.println(gcmTokenid + "REGISTERATION ID");

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // etRegId.setText(msg + "\n");
            }
        }.execute(null, null, null);
    }

    public String convertDateEmail(String createdAt) {
        String datee = createdAt.substring(0, 10);
        String fromYear = datee.substring(0, 4);
        try {
            DateFormat df = new SimpleDateFormat(
                    "yyyy-MM-dd");
            Date dat = df.parse(datee);
            System.out.println(createdAt);
            createdAt = dat.toString();
            System.out.println(createdAt.length());
            createdAt = createdAt.substring(3, 10);
            System.out.println(createdAt);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return createdAt + "," + fromYear;
    }

    public String convertTimeEmail(String time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date d;
        try {
            d = df.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.add(Calendar.MINUTE, -8);
            time = df.format(cal.getTime());
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("KK:mm a");
        timeFormat.setTimeZone(TimeZone.getTimeZone("IST"));

        try {
            time = outputFormat.format(timeFormat.parse(time));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return time;
    }

    public String convertTimeComnts(String comntCreated) {
        comntCreated = comntCreated.substring(0, 10);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = df.parse(comntCreated);
            System.out.println(date);
            comntCreated = date.toString();
            System.out.println(comntCreated.length());
            comntCreated = comntCreated.substring(4, 10);
            System.out.println(comntCreated);
            comntCreated.replaceAll("\\s+", " ");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return comntCreated;
    }

}
