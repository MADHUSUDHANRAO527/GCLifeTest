package mobile.gclifetest.youtube.task;

import android.os.AsyncTask;
import android.os.Handler;

import mobile.gclifetest.youtube.PhotosCreateFrag;
import mobile.gclifetest.youtube.handler.HandlerMessage;
import mobile.gclifetest.youtube.util.MessageUtil;

/*import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;*/

public class FetchYouTubeTokenTask extends AsyncTask<Void, Void, Void> {
	private static final String LOG_TAG = "FetchYouTubeTokenTask";

	private PhotosCreateFrag activity = null;
	private String selectedGoogleAccount = null;
	private Handler handler = null;

	private String token = null;

	public FetchYouTubeTokenTask(PhotosCreateFrag activity, String selectedGoogleAccount, Handler handler) {
		this.activity = activity;
		this.selectedGoogleAccount = selectedGoogleAccount;
		this.handler = handler;
	}

	@Override
	protected Void doInBackground(Void... voids)
	{
		/*try
		{
			token = GoogleAuthUtil.getToken(activity.getActivity(), selectedGoogleAccount, YouTubeUploadTask.scope);
			SharedPreferenceUtil.savePreferenceItemByName(activity.getActivity(), SharedPreferenceUtil.selectedGoogleAccount, selectedGoogleAccount);
		}
		catch (UserRecoverableAuthException userAuthEx)
		{
			// In case Android complains that Access not Configured, refer to comment of this class for how to configure OAuth client ID for this app.
			activity.startActivityForResult(userAuthEx.getIntent(), IntentRequestCode.REQUEST_AUTHORIZATION);
		}
		catch (GoogleAuthIOException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}
		catch (GoogleAuthException e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}*/
		return null;
	}

	@Override
	protected void onPostExecute(Void result)
	{
		if(token != null) {
			MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUTUBE_TOKEN_FETCHED);
		}
	}
}
