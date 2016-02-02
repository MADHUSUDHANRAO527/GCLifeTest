package mobile.gclife.PojoGson;

import java.io.IOException;

import mobile.gclife.activity.HomeApp;
import mobile.gclife.activity.Login;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

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

public class MyApplication extends Application {

	public static MyApplication instance;
	public static UserDetailsPojo user;
	SharedPreferences userPref;
	public static String actiobarColor = "#000000";
	Gson gson;
	GoogleCloudMessaging gcm;
	String PROJECT_NUMBER = "3819835017";
	public static String gcmTokenid, tokenIdGCm;
	public void onCreate() {
		super.onCreate();
		instance = this;

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
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator()).build();
		ImageLoader.getInstance().init(config);

		userPref = getSharedPreferences("USER", MODE_PRIVATE);
		gson = new Gson();
		String jsonUser = userPref.getString("USER_DATA", "NV");

		System.out.println(jsonUser
				+ "!!!!!!!!!!!!________________!!!!!!!!!!!!!!!");

		if (jsonUser == "NV" || jsonUser.equals("NV")) {
			Intent login = new Intent(MyApplication.this, Login.class);
			login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(login);
		} else {
			user = gson.fromJson(jsonUser, UserDetailsPojo.class);
			Intent home = new Intent(MyApplication.this, HomeApp.class);
			home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(home);
		}
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
}
