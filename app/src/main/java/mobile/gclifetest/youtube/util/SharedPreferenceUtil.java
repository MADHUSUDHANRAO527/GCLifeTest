package mobile.gclifetest.youtube.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import mobile.gclifetest.youtube.PhotosCreateFrag;

public class SharedPreferenceUtil {

	public static String selectedGoogleAccount = "selectedGoogleAccount";

	private static String preferencesFileName = "PreferenceFile";

	public static String getPreferenceItemByName(PhotosCreateFrag context, String itemName) {
		SharedPreferences settings = context.getActivity().getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE);
		return settings.getString(itemName, "");
	}

	public static void savePreferenceItemByName(Context context, String itemName, String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE).edit();
		editor.putString(itemName, value);
		editor.commit();
	}

}
