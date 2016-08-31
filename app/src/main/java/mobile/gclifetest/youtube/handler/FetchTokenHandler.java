package mobile.gclifetest.youtube.handler;

import android.os.Handler;
import android.os.Message;

import mobile.gclifetest.youtube.PhotosCreateFrag;

public class FetchTokenHandler extends Handler {

	private PhotosCreateFrag activity;

	public FetchTokenHandler(PhotosCreateFrag activity) {
		this.activity = activity;
	}

	public void handleMessage(Message msg) {
		if (msg.what == HandlerMessage.YOUTUBE_TOKEN_FETCHED) {
			activity.uploadYouTubeVideo();
		}
	}
}
