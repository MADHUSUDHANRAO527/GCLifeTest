package mobile.gclifetest.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cloudinary.Cloudinary;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.parse.Parse;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import mobile.gclifetest.pojoGson.UserDetailsPojo;

public class GclifeApplication extends Application {
    public static GclifeApplication instance;
    public static UserDetailsPojo user;
    SharedPreferences userPref;
    public static String actiobarColor = "#000000";
    Gson gson;
    //  GoogleCloudMessaging gcm;
    String PROJECT_NUMBER = "874207551772";
    public static String gcmTokenid;
    public static RequestQueue queue;
    public static Cloudinary cloudinary;
    boolean isUploading;
    private String billIdForPayment;
    //HTTP Constants
    //Production
    //   public static String HOSTNAME="http://54.169.40.151:3000/";
    //dev
     public static String HOSTNAME="http://35.166.172.142:3000/";
    //production
    //public static String HOSTNAME = "http://meljol.tech:3000/";
    private boolean isFromPaymentPage;

    //payment Domain
    public static String HOSTNAME_PAYMENT="http://35.166.172.142/";
    //  public static String HOSTNAME_PAYMENT = "http://meljol.tech/";

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
        EventBus.builder().logNoSubscriberMessages(false)
                .sendNoSubscriberEvent(false).installDefaultEventBus();
        // get GCM token
        //getRegId();
        //cloudinary setup
        cloudinarySetup();
        setUpFlurry();
    }

    public static synchronized GclifeApplication getInstance() {
        return instance;
    }

   /* private void getRegId() {
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
    }*/

    public static String convertDateEmail(String createdAt) {
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

    public static String convertTimeEmail(String time) {
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

        DateFormat df = new SimpleDateFormat(".-MM-dd");
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void cloudinarySetup() {
        Map config = new HashMap();
        config.put("cloud_name", "globalcityflatowners-org");
        config.put("api_key", "554359813976545");
        config.put("api_secret", "QWqoKmbLPvHTH57uMdlbOkZF51Q");
        cloudinary = new Cloudinary(config);
    }

    public void setUploadingMedia(boolean isUploading) {
        this.isUploading = isUploading;
    }

    public boolean isUploading() {
        return isUploading;
    }

    public void setUpFlurry() {
        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, mobile.gclifetest.utils.Constants.flurryApiKey);
    }

    public String getBillIdForPayment() {
        return billIdForPayment;
    }

    public void setBillIdForPayment(String billIdForPayment) {
        this.billIdForPayment = billIdForPayment;
    }

    public boolean isFromPaymentPage() {
        return isFromPaymentPage;
    }

    public void setFromPaymentPage(boolean fromPaymentPage) {
        isFromPaymentPage = fromPaymentPage;
    }
    /*public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            chain[0].checkValidity();
        } catch (Exception e) {
            throw new CertificateException("Certificate not valid or trusted.");
        }
    }*/
}
